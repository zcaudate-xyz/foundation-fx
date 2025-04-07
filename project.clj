(require '[leiningen.core.eval :as eval])

(def javafx-classifier
  (let [os-name (System/getProperty "os.name")
        arch    (System/getProperty "os.arch")]
    (cond
      (and (.contains os-name "Linux") (.equals arch "aarch64")) "linux-aarch64"
      (and (.contains os-name "Linux") (.equals arch "amd64"))   "linux"
      (and (.contains os-name "Mac"))                           "mac"
      (and (.contains os-name "Windows"))                       "win"
      :else (throw (RuntimeException.
                    (str "Unsupported OS/architecture: " os-name " / " arch))))))

(defproject xyz.zcaudate/foundation-fx "4.0.6"
  :description "gui libraries for foundation"
  :url "https://www.gitlab.com/zcaudate/foundation-fx"
  :license  {:name "MIT License"
             :url  "http://opensource.org/licenses/MIT"}
  :aliases
  {"test"  ["exec" "-ep" "(use 'code.test) (def res (run :all)) (System/exit (+ (:failed res) (:thrown res)))"]
   "test-unit"   ["run" "-m" "code.test" "exit"]
   "publish"     ["exec" "-ep" "(use 'code.doc)     (deploy-template :all) (publish :all)"]
   "incomplete"  ["exec" "-ep" "(use 'code.manage)  (incomplete :all) (System/exit 0)"]
   "install"     ["exec" "-ep" "(use 'code.maven)  (install :all {:tag :all}) (System/exit 0)"]
   "deploy"      ["exec" "-ep" "(use 'code.maven)  (deploy :all {:tag :all}) (System/exit 0)"]
   "deploy-lein" ["exec" "-ep" "(use 'code.maven)   (deploy-lein :all {:tag :all}) (System/exit 0)"]}
  :dependencies
  [[org.clojure/clojure "1.11.1"]
   [xyz.zcaudate/code.test           "4.0.6"]
   [xyz.zcaudate/code.manage         "4.0.6"]
   [xyz.zcaudate/code.java           "4.0.6"]
   [xyz.zcaudate/code.maven          "4.0.6"]
   [xyz.zcaudate/code.doc            "4.0.6"]
   [xyz.zcaudate/code.dev            "4.0.6"]

   [xyz.zcaudate/js.core             "4.0.6"]
   [xyz.zcaudate/js.lib.d3           "4.0.6"]
   [xyz.zcaudate/js.react            "4.0.6"]
   [xyz.zcaudate/lib.docker          "4.0.6"]
   [xyz.zcaudate/net.http            "4.0.6"]
   [xyz.zcaudate/rt.graal            "4.0.6"]
   [xyz.zcaudate/std.image           "4.0.6"]
   [xyz.zcaudate/std.log             "4.0.6"]
   [xyz.zcaudate/std.lang            "4.0.6"]
   [xyz.zcaudate/std.make            "4.0.6"]
   [xyz.zcaudate/std.text            "4.0.6"]
   [xyz.zcaudate/xtalk.lang          "4.0.6"]

   ;; fx.gui
   [eu.lestard/advanced-bindings "0.4.0"]
   [org.fxmisc.easybind/easybind "1.0.3"]
   [org.openjfx/javafx-controls "17" :classifier ~javafx-classifier]
   [org.openjfx/javafx-swing "17" :classifier ~javafx-classifier]
   [org.openjfx/javafx-base "17" :classifier ~javafx-classifier]
   [org.openjfx/javafx-graphics "17" :classifier ~javafx-classifier]
   [org.openjfx/javafx-web "17" :classifier ~javafx-classifier]
   [org.openjfx/javafx-media "17" :classifier ~javafx-classifier]
   [org.openjfx/javafx-fxml "17" :classifier ~javafx-classifier]
   
   ;; rt.graal
   [org.graalvm.sdk/graal-sdk "23.0.6"]
   [org.graalvm.truffle/truffle-api "23.0.6"]
   [org.graalvm.js/js "23.0.6"]
   [org.graalvm.js/js-scriptengine "23.0.6"]
   [commons-io/commons-io "2.15.1"]
   
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
                             [cider/cider-nrepl "0.45.0"]]}
             :repl {:injections [(try (require 'jvm.tool)
                                      (require '[std.lib :as h])
                                      (catch Throwable t (.printStackTrace t)))]}}
  :resource-paths    ["resources" "test-data" "test-code"]
  :java-source-paths ["src-java" "test-java"]
  :java-output-path  "target/classes"
  :repl-options {:host "0.0.0.0"
                 :port 10234}
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
   ;;"-XX:+UseCMSInitiatingOccupancyOnly"
   ;;"-XX:+CMSClassUnloadingEnabled"
   ;;"-XX:+CMSParallelRemarkEnabled"

   ;;
   ;; GC TUNING
   ;;   
   "-XX:MaxNewSize=256m"
   "-XX:NewSize=256m"
   ;;"-XX:CMSInitiatingOccupancyFraction=60"
   "-XX:MaxTenuringThreshold=8"
   "-XX:SurvivorRatio=4"

   ;;
   ;; Truffle
   ;;
   "-Dpolyglot.engine.WarnInterpreterOnly=false"
   
   ;;
   ;; JVM
   ;;
   "-Djdk.tls.client.protocols=\"TLSv1,TLSv1.1,TLSv1.2\""
   "-Djdk.attach.allowAttachSelf=true"
   "--enable-native-access=ALL-UNNAMED"
   "--add-opens" "java.base/java.io=ALL-UNNAMED"
   "--add-opens" "java.base/java.lang=ALL-UNNAMED"
   "--add-opens" "java.base/java.lang.annotation=ALL-UNNAMED"
   "--add-opens" "java.base/java.lang.invoke=ALL-UNNAMED"
   "--add-opens" "java.base/java.lang.module=ALL-UNNAMED"
   "--add-opens" "java.base/java.lang.ref=ALL-UNNAMED"
   "--add-opens" "java.base/java.lang.reflect=ALL-UNNAMED"
   "--add-opens" "java.base/java.math=ALL-UNNAMED"
   "--add-opens" "java.base/java.net=ALL-UNNAMED"
   "--add-opens" "java.base/java.nio=ALL-UNNAMED"
   "--add-opens" "java.base/java.nio.channels=ALL-UNNAMED"
   "--add-opens" "java.base/java.nio.charset=ALL-UNNAMED"
   "--add-opens" "java.base/java.nio.file=ALL-UNNAMED"
   "--add-opens" "java.base/java.nio.file.attribute=ALL-UNNAMED"
   "--add-opens" "java.base/java.nio.file.spi=ALL-UNNAMED"
   "--add-opens" "java.base/java.security=ALL-UNNAMED"
   "--add-opens" "java.base/java.security.cert=ALL-UNNAMED"
   "--add-opens" "java.base/java.security.interfaces=ALL-UNNAMED"
   "--add-opens" "java.base/java.security.spec=ALL-UNNAMED"
   "--add-opens" "java.base/java.text=ALL-UNNAMED"
   "--add-opens" "java.base/java.time=ALL-UNNAMED"
   "--add-opens" "java.base/java.time.chrono=ALL-UNNAMED"
   "--add-opens" "java.base/java.time.format=ALL-UNNAMED"
   "--add-opens" "java.base/java.time.temporal=ALL-UNNAMED"
   "--add-opens" "java.base/java.time.zone=ALL-UNNAMED"
   "--add-opens" "java.base/java.util=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.concurrent=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.concurrent.atomic=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.concurrent.locks=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.function=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.jar=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.regex=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.spi=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.stream=ALL-UNNAMED"
   "--add-opens" "java.base/java.util.zip=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.loader=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.misc=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.module=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.org.xml.sax=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.perf=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.reflect=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.util=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.vm=ALL-UNNAMED"
   "--add-opens" "java.base/jdk.internal.vm.annotation=ALL-UNNAMED"

   "--add-opens" "java.net.http/java.net.http=ALL-UNNAMED"
   "--add-opens" "java.net.http/jdk.internal.net.http=ALL-UNNAMED"
   "--add-opens" "java.management/java.lang.management=ALL-UNNAMED"
   "--add-opens" "java.management/sun.management=ALL-UNNAMED"
   
   "--add-opens" "java.desktop/java.applet=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.color=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.dnd=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.event=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.font=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.geom=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.im=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.im.spi=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.image=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.image.renderable=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.awt.print=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.beans=ALL-UNNAMED"
   "--add-opens" "java.desktop/java.beans.beancontext=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.accessibility=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.imageio=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.imageio.event=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.imageio.metadata=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.imageio.plugins.bmp=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.imageio.plugins.jpeg=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.imageio.spi=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.imageio.stream=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.print=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.print.attribute=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.print.attribute.standard=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.print.event=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.sound.midi=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.sound.midi.spi=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.sound.sampled=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.sound.sampled.spi=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.border=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.colorchooser=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.event=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.filechooser=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.plaf=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.plaf.basic=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.plaf.metal=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.plaf.multi=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.plaf.nimbus=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.plaf.synth=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.table=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.text=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.text.html=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.text.html.parser=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.text.rtf=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.tree=ALL-UNNAMED"
   "--add-opens" "java.desktop/javax.swing.undo=ALL-UNNAMED"])
