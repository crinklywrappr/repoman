(defproject repoman "0.1.1"
  :description "Tiny tool to query repology"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.2-alpha1"]
                 [org.clojure/tools.cli "1.0.194"]
                 ;; clj-http incompatible with graal
                 ;; 0.4.3-1 is built locally, using the latest commit
                 [org.martinklepsch/clj-http-lite "0.4.3-1"]
                 [cheshire "5.10.0"]
                 [camel-snake-kebab "0.4.1"]]
  :plugins [[io.taylorwood/lein-native-image "0.3.1"]]
  :main ^:skip-aot repoman.core
  :target-path "target/%s"
  :java-cmd "C:\\Users\\doubl\\scoop\\apps\\openjdk15\\current\\bin\\java.exe"
  :javac-options ["-server"]
  :native-image
  {:name "rp"
   :graal-bin "C:\\Program Files\\GraalVM\\graalvm-ce-java11-20.1.0\\bin"
   ;; :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
   :opts ["--verbose"
          ;; "--report-unsupported-elements-at-runtime"
          "--initialize-at-build-time"
          "--enable-https"
          "--no-fallback"]}
  :profiles
  {:uberjar
   {:aot :all
    :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
