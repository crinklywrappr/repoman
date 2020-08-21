(defproject repoman "0.1.0-SNAPSHOT"
  :description "Tiny tool to query repology"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 ;; clj-http incompatible with graal
                 [org.martinklepsch/clj-http-lite "0.4.3"]
                 [cheshire "5.10.0"]
                 [camel-snake-kebab "0.4.1"]]
  :plugins [[io.taylorwood/lein-native-image "0.3.1"]]
  :main ^:skip-aot repoman.core
  :target-path "target/%s"
  :native-image
  {:name "rp"
   :graal-bin "C:\\Program Files\\GraalVM\\graalvm-ce-java11-20.1.0\\bin"
   :opts ["--verbose"
          "--report-unsupported-elements-at-runtime"
          "--initialize-at-build-time"
          "--allow-incomplete-classpath" ;; clj-http-lite requires this
          "--enable-https"
          "--no-fallback"]}
  :profiles
  {:uberjar
   {:aot :all
    :native-image
    {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}})
