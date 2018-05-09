(set-env! :dependencies '[[org.clojure/clojure "1.9.0"]
                          [com.oracle.truffle/truffle-api "1.0.0-rc1"]
                          [org.graalvm/graal-sdk "1.0.0-rc1"]
                          [com.oracle.truffle/truffle-dsl-processor "1.0.0-rc1"]]
          :source-paths #{"src"}
          :resource-paths #{"resources"})

(deftask annotate
  []
  (let [tmp  (tmp-dir!)]
    (with-pre-wrap fs
      (empty-dir! tmp)
      (let [path (or (.getAbsolutePath tmp) "target")]
        ((sh "javac" "-cp" (boot.core/get-env :boot-class-path) "-verbose" "-d" path "-s" path "-proc:only" "language.bf.BFLanguage"))
        (-> fs
            (add-resource (clojure.java.io/file tmp "."))
            (commit!))))))

(deftask build
  []
  (comp (aot :all true)
        (annotate)
        (uber)
        (jar)
        (target)))
