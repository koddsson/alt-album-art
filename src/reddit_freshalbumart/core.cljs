(ns reddit-freshalbumart.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [goog.net.XhrIo :as xhr]
            [cljs-time.core :refer [plus now hour]]
            [goog.crypt.base64 :as b64]
            [cljs.core.async :refer [<! put! chan close!]]
            [alandipert.storage-atom :refer [local-storage]])
  (:import [goog.net Jsonp]
           [goog Uri]))


(def url "https://reddit.com/r/freshalbumart/hot.json?limit=100")

(enable-console-print!)

(defn jsonp [uri]
  "Make a GET request with a jsonp callback"
  (let [out (chan)
        req (Jsonp. (Uri. uri) "jsonp")]
    (.send req nil (fn [res] (put! out res)))
    out))

(defn GET [url]
  (let [ch (chan 1)]
    (xhr/send url
              (fn [event]
                (let [res (-> event .-target .getResponseText)]
                  (go (>! ch res)
                      (close! ch)))))
    ch))

(comment (go (js/alert (b64/encodeByteArray(<! (GET "https://i.imgur.com/GZkIfQQ.jpg"))))))

"atom/localStorage"
(defonce prefs (local-storage (atom {}) :prefs))

(defn get-imgur-id [url]
  "Get's the imgur url ID"
  (last (re-find #"(?im)(?:imgur\.com/)(.*)(?:.jpg|.png)$" url)))

(defn filter-post [post]
  (not (or 
    (not (get-imgur-id (:url post)))
    (= (:thumbnail post) "nsfw")
    (= (:thumbnail post) "self"))))

(defn render-image [{:keys [url permalink title]}]
  "TODO: Use a HTML library"
  (str "<a href='https://reddit.com/" permalink "' class='Image' style='background-image: url(" url ");'></div>"))

(defn render []
  "Renders urls as images in container"
  (print (:invalidateTimestamp @prefs))
  (let [container (dom/getElementByClass "Container")]
    (set! (.-innerHTML container) (apply str (map render-image (filter filter-post (:data @prefs)))))))

"Check the atom/localstorage for data and fetch if empty"
(if (empty? @prefs)
  (go
    (swap! prefs assoc :data (map :data (:children (:data (js->clj (<! (jsonp url)) :keywordize-keys true)))))
    (swap! prefs assoc :invalidateTimestamp (now))))

"Re-render when the atom is updated"
(add-watch prefs :new (fn [_ _ _ ns] (render))) 

"Initial render from atom/localStorage"
(render)
