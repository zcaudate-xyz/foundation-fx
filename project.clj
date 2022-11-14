(defproject zcaudate/foundation-fx "4.0.1"
  :description "gui libraries for foundation"
  :url "https://www.gitlab.com/zcaudate/foundation-fx"
  :aliases
  {"test"  ["exec" "-ep" "(use 'code.test) (def res (run :all)) (System/exit (+ (:failed res) (:thrown res)))"]
   "test-unit"   ["run" "-m" "code.test" "exit"]
   "publish"     ["exec" "-ep" "(use 'code.doc)     (deploy-template :all) (publish :all)"]
   "install"     ["exec" "-ep" "(use 'code.maven)  (install :all {:tag :all}) (System/exit 0)"]
   "deploy"      ["exec" "-ep" "(use 'code.maven)  (deploy :all {:tag :all}) (System/exit 0)"]}
  :dependencies
  [[org.clojure/clojure "1.11.1"]
   foundation/code.test           "4.0.1"]
   foundation/code.manage         "4.0.1"]
   foundation/code.java           "4.0.1"]
   foundation/code.maven          "4.0.1"]
   foundation/code.doc            "4.0.1"]
   foundation/code.dev            "4.0.1"]

   ;; fx.gui
   [eu.lestard/advanced-bindings "0.4.0"]
   [org.fxmisc.easybind/easybind "1.0.3"]
   [org.openjfx/javafx-controls "16"]
   [org.openjfx/javafx-swing "16"]
   [org.openjfx/javafx-base "16"]
   [org.openjfx/javafx-graphics "16"]
   [org.openjfx/javafx-web "16"]
   [org.openjfx/javafx-media "16"]
   [org.openjfx/javafx-fxml "16"]
                 
   ;; rt.jocl
   [org.jocl/jocl "2.0.4"]]
  :global-vars {*warn-on-reflection* true}
  :cljfmt {:file-pattern #"^[^\.].*\.clj$"
           :indents {script [[:inner 0]]
                     template-vars [[:inner 0]]
                     fact [[:inner 0]]
                     comment [[:inner 0]]}}
  :profiles {:dev {:plugins [[lein-ancient "0.6.15"]
                             [lein-exec "0.3.7"]
                             [lein-cljfmt "0.7.0"]
                             [cider/cider-nrepl "0.25.11"]]}
             :repl {:injections [(try (require 'jvm.tool)
                                      (require '[std.lib :as h])
                                      (catch Throwable t (.printStackTrace t)))]}}
  :resource-paths    ["resources" "test-data" "test-code"]
  :java-source-paths ["src-java" "test-java"]
  :java-output-path  "target/classes"
  :repl-options {:host "0.0.0.0" :port 51311}
  :jvm-opts
  ["-Xms2048m"
   "-Xmx2048m"
   "-XX:MaxMetaspaceSize=1048m"
   "-XX:-OmitStackTraceInFastThrow"
   
   ;;
   ;; GC FLAGS
   ;;
   "-XX:+UseAdaptiveSizePolicy"
   "-XX:+AggressiveHeap"
   "-XX:+ExplicitGCInvokesConcurrent"
   "-XX:+UseCMSInitiatingOccupancyOnly"
   "-XX:+CMSClassUnloadingEnabled"
   "-XX:+CMSParallelRemarkEnabled"

   ;;
   ;; GC TUNING
   ;;   
   "-XX:MaxNewSize=256m"
   "-XX:NewSize=256m"
   "-XX:CMSInitiatingOccupancyFraction=60"
   "-XX:MaxTenuringThreshold=8"
   "-XX:SurvivorRatio=4"
   
   ;;
   ;; JVM
   ;;
   "-Djdk.tls.client.protocols=\"TLSv1,TLSv1.1,TLSv1.2\""
   "-Djdk.attach.allowAttachSelf=true"
   "--add-opens" "javafx.graphics/com.sun.javafx.util=ALL-UNNAMED"
   "--add-opens" "java.base/java.lang=ALL-UNNAMED"
   "--illegal-access=permit"])
