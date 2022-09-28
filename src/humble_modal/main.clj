(ns humble-modal.main
  (:require
    [io.github.humbleui.debug :as debug]
    [io.github.humbleui.paint :as paint]
    [io.github.humbleui.ui :as ui]
    [io.github.humbleui.ui.clickable :as clickable]
    [io.github.humbleui.window :as window]))

(set! *warn-on-reflection* true)

(reset! debug/*enabled? true)

(def dark-grey 0xff404040)
(def light-grey 0xffeeeeee)
(def blue 0xff0d7fbe)
(def yellow 0xfffae317)
(def red 0xfff50f0f)
(def white 0xfff3f3f3)
(def black 0xff000000)

(defonce *window
  (atom nil))

(defn redraw!
  "Requests a redraw on the next available frame."
  []
  (some-> @*window window/request-frame))

(def initial-app-state
  {::modal-showing? false})

(defonce *app-state
  (atom initial-app-state))

(defn close-modal! []
  (swap! *app-state assoc ::modal-showing? false)
  (redraw!))

(defn open-modal! []
  (swap! *app-state assoc ::modal-showing? true)
  (redraw!))

(defn reset-app-state! []
  (reset! *app-state initial-app-state)
  (redraw!))

(def layout-padding-px 10)

(def Buttons
  (ui/padding layout-padding-px
    (ui/valign 0
      (ui/column
        (ui/button open-modal! (ui/label "Open Modal"))))))
        ; (ui/gap 0 10)))))
        ;; TODO: open modal with animation

(def ModalContent
  (ui/center
    (ui/clickable
      {:on-click (fn [evt]
                   (println "Clicking inside ModalContent does not close the modal")
                   nil)}
      (ui/clip-rrect 5
        (ui/rect (paint/fill white)
          (ui/padding 30 30
            (ui/column
              (ui/label "Your modal content goes here")
              (ui/gap 0 20)
              (ui/button close-modal! (ui/label "Close")))))))))

(def ModalLayer
  (ui/clickable
    {:on-click (fn [_evt]
                 (println "Clicking on ModalLayer closes the modal.")
                 (close-modal!)
                 nil)}
    (ui/rect
      (paint/fill 0x88000000)
      (ui/label ""))))

(def Modal
  (ui/stack
    ModalLayer
    ModalContent))

(def AppBody
  (ui/row
    Buttons
    [:stretch 1 nil]))

(def HumbleModal
  "top-level component"
  (ui/default-theme {}
    (ui/dynamic _ctx [modal-showing? (::modal-showing? @*app-state)]
     (ui/stack
       AppBody
       (when modal-showing?
         Modal)))))

;; re-draw the UI when we load this namespace
(redraw!)

(defn -main [& args]
  (ui/start-app!
    (reset! *window
      (ui/window
        {:title    "HumbleUI Modal Example"
         :bg-color 0xFFFFFFFF}
        #'HumbleModal))))
