(ns flow-storm.json-serializer
  (:require [cognitect.transit :as transit :refer [tagged-value write-handler]]
            [flow-storm.utils :refer [log-error]]
            [flow-storm.trace-types :as trace-types :refer [FlowInitTrace FormInitTrace ExecTrace FnCallTrace BindTrace]]))

(defn serialize [o]
  (try
    (let [writer (transit/writer :json {:handlers {FlowInitTrace (write-handler (fn [_ _] "flow_storm.trace_types.FlowInitTrace") (fn [rec] (tagged-value "map" rec)) )
                                                   FormInitTrace (write-handler (fn [_ _] "flow_storm.trace_types.FormInitTrace") (fn [rec] (tagged-value "map" rec)) )
                                                   ExecTrace     (write-handler (fn [_ _] "flow_storm.trace_types.ExecTrace")     (fn [rec] (tagged-value "map" rec)) )
                                                   FnCallTrace   (write-handler (fn [_ _] "flow_storm.trace_types.FnCallTrace")   (fn [rec] (tagged-value "map" rec)))
                                                   BindTrace     (write-handler (fn [_ _] "flow_storm.trace_types.BindTrace")     (fn [rec] (tagged-value "map" rec)))
                                                   js/RegExp     (write-handler (fn [_ _] "regex")           str)
                                                   :default      (write-handler (fn [_ _] "object")        pr-str)}})]
      (transit/write writer o))
    (catch js/Error e (log-error (str "Error serializing " o) e) (throw e))))

(defn deserialize [^String s]
  (try
    (let [reader (transit/reader :json {:handlers {"flow_storm.trace_types.FlowInitTrace" (fn [tv] (trace-types/map->FlowInitTrace tv))
                                                   "flow_storm.trace_types.FormInitTrace" (fn [tv] (trace-types/map->FormInitTrace tv))
                                                   "flow_storm.trace_types.ExecTrace"     (fn [tv] (trace-types/map->ExecTrace tv))
                                                   "flow_storm.trace_types.FnCallTrace"   (fn [tv] (trace-types/map->FnCallTrace tv))
                                                   "flow_storm.trace_types.BindTrace"     (fn [tv] (trace-types/map->BindTrace tv))
                                                   "object"        (fn [s] s)
                                                   "regex"         (fn [s] (re-pattern s))}})]
      (transit/read reader s))
    (catch js/Error e (log-error (str "Error deserializing " s " ERROR: " (.-message e))) (throw e))))
