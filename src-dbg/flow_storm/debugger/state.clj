(ns flow-storm.debugger.state
  (:require [flow-storm.debugger.trace-indexer.protos :as indexer])
  (:import [java.util HashMap Map$Entry]))


;; (s/def :thread/form (s/keys :req [:form/id
;;                                   :form/ns
;;                                   :form/form]))
;; (s/def :thread/forms (s/map-of :form/id :thread/form))

;; (s/def :thread/exec-trace (s/or :fn-call ::fn-call-trace :expr ::exec-trace))

;; (s/def :thread/traces (s/coll-of :thread/exec-trace :kind vector?))

;; (s/def :thread/curr-trace-idx (s/nilable number?))

;; (s/def :thread/execution (s/keys :req [:thread/traces
;;                                        :thread/curr-trace-idx]))

;; (s/def :thread/bind-traces (s/coll-of ::bind-trace :kind vector?))

;; (s/def :thread/callstack-tree any?) ;; TODO: finish this

;; (s/def :thread/forms-hot-traces (s/map-of :form/id (s/coll-of ::exec-trace :kind vector?)))

;; (s/def :callstack-tree/hidden-fn (s/keys :req-un [:fn/name :fn/ns]))
;; (s/def :thread/callstack-tree-hidden-fns (s/coll-of :callstack-tree/hidden-fn :kind set?))
;; (s/def ::thread (s/keys :req [:thread/id
;;                               :thread/forms
;;                               :thread/execution
;;                               :thread/callstack-tree
;;                               :thread/callstack-tree-hidden-fns
;;                               :thread/forms-hot-traces]))

;; (s/def :flow/threads (s/map-of :thread/id ::thread))

;; (s/def ::flow (s/keys :req [:flow/id
;;                             :flow/threads]
;;                       :req-un [::timestamp]))

;; (s/def :state/flows (s/map-of :flow/id ::flow))
;; (s/def :state/selected-flow-id (s/nilable :flow/id))
;; (s/def :state/trace-counter number?)

;; (s/def ::state (s/keys :req-un [:state/flows
;;                                 :state/selected-flow-id
;;                                  :state/trace-counter]))

(def orphans-flow-id -1)

(def *state nil)
(def ^HashMap fn-call-stats-map nil)

(def initial-state {:trace-counter 0
                    :flows {}
                    :selected-flow-id nil})

(def remote-connection (atom nil))

;;;;;;;;;;;
;; Utils ;;
;;;;;;;;;;;

(defn clear-flow-fn-call-stats [flow-id]
  (locking fn-call-stats-map
    (let [flow-keys (->> (.entrySet fn-call-stats-map)
                         (keep (fn [^Map$Entry entry]
                                 (let [key (.getKey entry)]
                                   (when (= flow-id (first key))
                                     key))))
                         doall)]
      (doseq [key flow-keys]
        (.remove fn-call-stats-map key)))))

(defn create-flow [flow-id exec-form-ns exec-form timestamp]
  ;; if a flow for `flow-id` already exist we discard it and
  ;; will be GCed

  (swap! *state assoc-in [:flows flow-id] {:flow/id flow-id
                                           :flow/threads {}
                                           :flow/execution-expr {:ns exec-form-ns
                                                                 :form exec-form}
                                           :timestamp timestamp})

  (clear-flow-fn-call-stats flow-id))

(defn remove-flow [flow-id]
  (swap! *state update :flows dissoc flow-id)
  (clear-flow-fn-call-stats flow-id))

(defn get-flow [flow-id]
  (get-in @*state [:flows flow-id]))

(defn create-thread [flow-id thread-id trace-indexer]
               (swap! *state assoc-in [:flows flow-id :flow/threads thread-id]
                      {:thread/id thread-id
                       :thread/trace-indexer trace-indexer
                       :thread/curr-trace-idx nil
                       :thread/callstack-tree-hidden-fns #{}
                       :thread/callstack-expanded-traces #{}
                       :thread/callstack-selected-trace-idx nil
                       :thread/fn-call-stats {}}))

(defn get-thread [flow-id thread-id]
  (get-in @*state [:flows flow-id :flow/threads thread-id]))

(defn thread-trace-indexer [flow-id thread-id]
  (:thread/trace-indexer (get-thread flow-id thread-id)))

(defn thread-trace-count [flow-id thread-id]
  (indexer/thread-exec-count (thread-trace-indexer flow-id thread-id)))

(defn current-trace-idx [flow-id thread-id]
  (:thread/curr-trace-idx (get-thread flow-id thread-id)))

(defn set-trace-idx [flow-id thread-id idx]
  (swap! *state assoc-in [:flows flow-id :flow/threads thread-id :thread/curr-trace-idx] idx))

(defn update-fn-call-stats [flow-id thread-id {:keys [fn-ns fn-name form-id]}]
  (locking fn-call-stats-map
    (let [k [flow-id thread-id fn-ns fn-name form-id]
          curr-val (.getOrDefault fn-call-stats-map k 0)]
      (.put fn-call-stats-map k (inc curr-val)))))

(defn fn-call-stats [target-flow-id target-thread-id]
  (locking fn-call-stats-map
    (let [indexer (thread-trace-indexer target-flow-id target-thread-id)]
      (->> (.entrySet fn-call-stats-map)
           (keep (fn [^Map$Entry entry]
                   (let [[flow-id thread-id fn-ns fn-name form-id] (.getKey entry)
                         {:keys [form/form form/def-kind multimethod/dispatch-val]} (indexer/get-form indexer form-id)]
                     (when (and (= target-flow-id flow-id)
                                (= target-thread-id thread-id))
                       {:fn-ns fn-ns
                        :fn-name fn-name
                        :form-id form-id
                        :form form
                        :form-def-kind def-kind
                        :dispatch-val dispatch-val
                        :cnt (.getValue entry)}))))))))

(defn callstack-tree-hide-fn [flow-id thread-id fn-name fn-ns]
  (swap! *state update-in [:flows flow-id :flow/threads thread-id :thread/callstack-tree-hidden-fns] conj {:name fn-name :ns fn-ns}))

(defn callstack-tree-hidden? [flow-id thread-id fn-name fn-ns]
  (let [hidden-set (get-in @*state [:flows flow-id :flow/threads thread-id :thread/callstack-tree-hidden-fns])]
    (contains? hidden-set {:name fn-name :ns fn-ns})))

(defn increment-trace-counter []
  (swap! *state update :trace-counter inc))

(defn callstack-tree-item-expanded? [flow-id thread-id fn-call-trace-idx]
  (let [expanded-set (get-in @*state [:flows flow-id :flow/threads thread-id :thread/callstack-expanded-traces])]
    (contains? expanded-set fn-call-trace-idx)))

(defn callstack-tree-expand-calls [flow-id thread-id fn-call-trace-indexes]
  (swap! *state update-in [:flows flow-id :flow/threads thread-id :thread/callstack-expanded-traces] into fn-call-trace-indexes))

(defn callstack-tree-select-path [flow-id thread-id select-path]
  (let [[target-id & parents-ids] select-path]
    (swap! *state update-in [:flows flow-id :flow/threads thread-id]
           (fn [thread]
             (-> thread
                 (assoc :thread/callstack-expanded-traces (into #{} parents-ids))
                 (assoc :thread/callstack-selected-trace-idx target-id))))))

(defn callstack-tree-collapse-calls [flow-id thread-id fn-call-trace-indexes]
  (swap! *state update-in [:flows flow-id :flow/threads thread-id :thread/callstack-expanded-traces] (fn [traces] (apply disj traces fn-call-trace-indexes))))

(defn callstack-tree-collapse-all-calls [flow-id thread-id]
  (swap! *state assoc-in [:flows flow-id :flow/threads thread-id :thread/callstack-expanded-traces] #{}))

(defn init-state! []
  (alter-var-root #'*state (constantly (atom initial-state)))
  (alter-var-root #'fn-call-stats-map (constantly (HashMap.))))
