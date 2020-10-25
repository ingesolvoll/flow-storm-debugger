(ns flow-storm-debugger.ui.styles
  (:require [cljfx.css :as css]
            [cljfx.api :as fx]))

(def style 
  (css/register
   ::style
   (let [background-color-2 "#1e1e1e"
         background-color "#424242"
         basic-font-color "#eaeaea"
         button-back "#4b79b9"
         locals-label-color :pink
         return-label-color "#00ffa5"
         icon (fn [i] {:-fx-icon-code i
                       :-fx-icon-color basic-font-color
                       :-fx-icon-size 16})]
     {".root" {:-fx-background-color background-color
               " .label" {:-fx-text-fill basic-font-color}
               " .button" {:-fx-background-color button-back
                           :-fx-text-fill basic-font-color}
               " .scroll-bar" {" .track-background" {:-fx-background-color background-color-2}
                               " .thumb" {:-fx-background-color background-color}}
               " .bottom-bar" {:-fx-background-color background-color-2
                               :-fx-padding 5}
               " .list-view" {" .list-cell:even" {:-fx-background-color background-color}
                              " .list-cell:odd" {:-fx-background-color "#4a4a4a"}}
               " .no-flows" {:-fx-font-size 15}
               " .controls-pane" {:-fx-background-color background-color-2
                                 :-fx-padding 10}         
               " .tab-header-area .tab-header-background" {:-fx-background-color background-color-2}
               " .tab-pane .tab" {:-fx-background-color background-color}
               " .split-pane-divider" {:-fx-padding 1}
               " .pane-text-area" {:-fx-text-fill basic-font-color
                                   :-fx-background-color background-color
                                   :-fx-padding 10
                                   " .scroll-pane" {" .content" {:-fx-background-color background-color}}}
               " .web-view" {:-fx-background-color :blue}
               " .load-button" {" .ikonli-font-icon" (icon "mdi-folder-plus")}
               " .save-button" {" .ikonli-font-icon" (icon "mdi-content-save")}
               " .reset-button" {" .ikonli-font-icon" (icon "mdi-reload")}
               " .prev-button" {" .ikonli-font-icon" (icon "mdi-chevron-left")}
               " .next-button" {" .ikonli-font-icon" (icon "mdi-chevron-right")}
               " .result-label" {" .ikonli-font-icon" (merge (icon "mdi-arrow-right-bold")
                                                             {:-fx-icon-color return-label-color})}
               " .strong-text" {:-fx-font-weight :bold}
               " .locals-view" {" .label" {:-fx-padding [0 10 0 0]}
                                " .local-name" {:-fx-text-fill locals-label-color}}}})))
#_(def style2
  (css/register ::style
    (let [base-color "#222"
          style {:app.style/text-color base-color
                 :app.style/help-color (str base-color "8")
                 :app.style/border-color (str base-color "4")
                 :app.style/shadow-color (str base-color "3")
                 :app.style/focus-color (str base-color "8")
                 :app.style/control-color "#fff"
                 :app.style/control-hover-color "#f4f4f4"
                 :app.style/background-color "#eee"
                 :app.style/spacing 10
                 :app.style/scroll-bar-size 9
                 :app.style/padding 20
                 :app.style/corner-size 5
                 :app.style/label-padding "2px 4px"}
          text (fn [size weight]
                 {:-fx-text-fill (:app.style/text-color style)
                  :-fx-wrap-text true
                  :-fx-font-weight weight
                  :-fx-font-size size})
          control-shadow (format "dropshadow(gaussian, %s, 5, 0, 0, 1)"
                                 (:app.style/shadow-color style))
          inner-shadow (format "innershadow(gaussian, %s, 5, 0, 0, 2)"
                               (:app.style/shadow-color style))
          hover-shadow (format "dropshadow(gaussian, %s, 7, 0, 0, 2)"
                               (:app.style/shadow-color style))
          armed-shadow (format "dropshadow(gaussian, %s, 3, 0, 0, 1)"
                               (:app.style/shadow-color style))
          border {:-fx-border-color (:app.style/border-color style)
                  :-fx-background-color (:app.style/control-color style)
                  :-fx-border-radius (:app.style/corner-size style)
                  :-fx-background-radius (:app.style/corner-size style)}
          button (merge
                   (text 13 :normal)
                   border
                   {:-fx-padding (:app.style/label-padding style)
                    :-fx-effect control-shadow
                    ":focused" {:-fx-border-color (:app.style/focus-color style)}
                    ":hover" {:-fx-effect hover-shadow
                              :-fx-background-color (:app.style/control-hover-color style)}
                    ":armed" {:-fx-effect armed-shadow}})]
      {".label" {:-fx-text-fill :red}}
      #_(merge
        style
        {".app" {"-label" (text 23 :normal)
                 "-header" (text 20 :bold)
                 "-sub-header" (text 16 :bold)
                 "-code" (merge
                           (text 13 :normal)
                           {:-fx-font-family "monospace"
                            :-fx-padding (:app.style/spacing style)})
                 "-container" {:-fx-spacing (:app.style/spacing style)}
                 "-root" {:-fx-padding (:app.style/padding style)
                          :-fx-background-color (:app.style/background-color style)}
                 "-button" {"-primary" button
                            "-secondary" button}
                 "-check-box" {:-fx-text-fill (:app.style/text-color style)
                               :-fx-label-padding (format "0 0 0 %spx"
                                                          (:app.style/spacing style))
                               ":focused > .box" {:-fx-border-color (:app.style/focus-color style)}
                               ":hover > . box" {:-fx-effect hover-shadow
                                                 :-fx-background-color (:app.style/control-hover-color style)}
                               ":armed > .box" {:-fx-effect armed-shadow}
                               "> .box" (merge
                                          border
                                          {:-fx-effect control-shadow
                                           :-fx-padding "3px 2px"
                                           "> .mark" {:-fx-padding "5px 6px"
                                                      :-fx-shape "'M7.629,14.566c0.125,0.125,0.291,0.188,0.456,0.188c0.164,0,0.329-0.062,0.456-0.188l8.219-8.221c0.252-0.252,0.252-0.659,0-0.911c-0.252-0.252-0.659-0.252-0.911,0l-7.764,7.763L4.152,9.267c-0.252-0.251-0.66-0.251-0.911,0c-0.252,0.252-0.252,0.66,0,0.911L7.629,14.566z'"}})
                               ":selected > .box > .mark" {:-fx-background-color (:app.style/text-color style)}}
                 "-text-field" (merge
                                 (text 13 :normal)
                                 border
                                 {:-fx-highlight-fill (:app.style/text-color style)
                                  :-fx-padding (:app.style/label-padding style)
                                  :-fx-prompt-text-fill (:app.style/help-color style)
                                  :-fx-highlight-text-fill (:app.style/background-color style)
                                  :-fx-effect inner-shadow
                                  ":focused" {:-fx-border-color (:app.style/focus-color style)}})}
         ".scroll-pane" (merge
                          border
                          {:-fx-effect inner-shadow
                           :-fx-focus-traversable true
                           ":focused" {:-fx-border-color (:app.style/focus-color style)
                                       :-fx-background-insets 0}
                           "> .viewport" {:-fx-background-color (:app.style/control-color style)}
                           "> .corner" {:-fx-background-color :transparent}})
         ".scroll-bar" {:-fx-background-color :transparent
                        "> .thumb" {:-fx-background-color (:app.style/focus-color style)
                                    :-fx-background-radius (:app.style/scroll-bar-size style)
                                    :-fx-background-insets 1
                                    ":pressed" {:-fx-background-color (:app.style/text-color style)}}
                        ":horizontal" {"> .increment-button > .increment-arrow" {:-fx-pref-height (:app.style/scroll-bar-size style)}
                                       "> .decrement-button > .decrement-arrow" {:-fx-pref-height (:app.style/scroll-bar-size style)}}
                        ":vertical" {"> .increment-button > .increment-arrow" {:-fx-pref-width (:app.style/scroll-bar-size style)}
                                     "> .decrement-button > .decrement-arrow" {:-fx-pref-width (:app.style/scroll-bar-size style)}}
                        "> .decrement-button" {:-fx-padding 0
                                               "> .decrement-arrow" {:-fx-shape nil
                                                                     :-fx-padding 0}}
                        "> .increment-button" {:-fx-padding 0
                                               "> .increment-arrow" {:-fx-shape nil
                                                                     :-fx-padding 0}}}}))))