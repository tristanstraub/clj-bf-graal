(ns language.bf
  (:require [clojure.java.io :as io])
  (:import [org.graalvm.polyglot Context]
           [org.graalvm.polyglot.Source]
           [org.graalvm.polyglot Value]
           [com.oracle.truffle.api CompilerDirectives CompilerDirectives$TruffleBoundary TruffleLanguage]
           [com.oracle.truffle.api.frame FrameDescriptor FrameSlot FrameSlotTypeException]
           com.oracle.truffle.api.nodes.Node
           com.oracle.truffle.api.nodes.RootNode
           com.oracle.truffle.api.source.Source
           com.oracle.truffle.api.frame.FrameSlotKind
           com.oracle.truffle.api.frame.VirtualFrame
           com.oracle.truffle.api.Truffle
           com.oracle.truffle.api.nodes.ExplodeLoop
           com.oracle.truffle.api.nodes.Node
           com.oracle.truffle.api.nodes.RepeatingNode
           com.oracle.truffle.api.CompilerAsserts
           com.oracle.truffle.api.TruffleLanguage$Registration
           com.oracle.truffle.api.TruffleLanguage$Env
           com.oracle.truffle.api.instrumentation.ProvidedTags
           com.oracle.truffle.api.instrumentation.StandardTags$StatementTag
           com.oracle.truffle.api.instrumentation.StandardTags$RootTag
           com.oracle.truffle.api.TruffleLanguage$ParsingRequest
           com.oracle.truffle.api.CallTarget
           java.util.Stack
           java.io.IOException)
  (:gen-class))

(defn -main
  [& args]
  (let [f       (io/file (first args))
        src     (.. (org.graalvm.polyglot.Source/newBuilder "bf" f)
                    (build))
        context (.. (Context/newBuilder (into-array ["bf"]))
                    (in System/in)
                    (out System/out)
                    (build))]

    (.eval context src)))

(def MEMORY_SIZE
  10000)

(gen-class
 :name language.bf.BFContext
 :init init
 :state state
 :prefix -bfcontext-
 :constructors {[com.oracle.truffle.api.TruffleLanguage com.oracle.truffle.api.TruffleLanguage$Env] []})

(gen-class
 :name  ^{com.oracle.truffle.api.TruffleLanguage$Registration {:id       "bf"
                                                               :name     "BF"
                                                               :version  "1.0-SNAPSHOT"
                                                               :mimeType ["application/x-bf"]}
          com.oracle.truffle.api.instrumentation.ProvidedTags [StandardTags$StatementTag
                                                               StandardTags$RootTag]}
 language.bf.BFLanguage
 :extends com.oracle.truffle.api.TruffleLanguage
 :init init
 :prefix -bflanguage-
 :constructors {[] []})

(defn -bfrootnode-execute
  [this frame]
  (let [{:keys [nodes]} (.state this)]
    (CompilerAsserts/compilationConstant (count nodes))

    (doseq [node nodes]
      (.execute node frame))

    0))

(definterface BFNode
  (^void execute [^com.oracle.truffle.api.frame.VirtualFrame frame]))

(gen-class
 :name language.bf.BFInitNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node
 :init init
 :state state
 :prefix -bfinitnode-
 :constructors {[] []
                [com.oracle.truffle.api.frame.FrameSlot com.oracle.truffle.api.frame.FrameSlot] []})

(gen-class
 :name language.bf.BFRootNode
 :extends com.oracle.truffle.api.nodes.RootNode
 :init init
 :state state
 :prefix -bfrootnode-
 :constructors {[] []
                [com.oracle.truffle.api.TruffleLanguage com.oracle.truffle.api.frame.FrameDescriptor java.util.List] [com.oracle.truffle.api.TruffleLanguage com.oracle.truffle.api.frame.FrameDescriptor]})

(gen-class
 :name language.bf.ReadDataNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node

 :init init
 :state state
 :prefix -readdatanode-
 :constructors {[] []
                [com.oracle.truffle.api.frame.FrameSlot com.oracle.truffle.api.frame.FrameSlot] []})

(gen-class
 :name language.bf.PrintDataNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node

 :init init
 :state state
 :prefix -printdatanode-
 :constructors {[] []
                [com.oracle.truffle.api.frame.FrameSlot com.oracle.truffle.api.frame.FrameSlot] []})

(gen-class
 :name language.bf.IncDataNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node

 :init init
 :state state
 :prefix -incdatanode-
 :constructors {[] []
                [com.oracle.truffle.api.frame.FrameSlot com.oracle.truffle.api.frame.FrameSlot] []})

(gen-class
 :name language.bf.ConditionNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node

 :init init
 :state state
 :prefix -conditionnode-
 :constructors {[] []
                [com.oracle.truffle.api.frame.FrameSlot com.oracle.truffle.api.frame.FrameSlot] []}
 :methods [[executeCondition [com.oracle.truffle.api.frame.VirtualFrame]
            boolean]])

(gen-class
 :name language.bf.DecDataNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node

 :init init
 :state state
 :prefix -decdatanode-
 :constructors {[] []
                [com.oracle.truffle.api.frame.FrameSlot com.oracle.truffle.api.frame.FrameSlot] []})

(gen-class
 :name language.bf.IncPointerNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node

 :init init
 :state state
 :prefix -incpointernode-
 :constructors {[] []
                [com.oracle.truffle.api.frame.FrameSlot] []})

(gen-class
 :name language.bf.DecPointerNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node

 :init init
 :state state
 :prefix -decpointernode-
 :constructors {[] []
                [com.oracle.truffle.api.frame.FrameSlot] []})

(gen-class
 :name language.bf.BFLoopNode
 :implements [language.bf.BFNode]
 :extends com.oracle.truffle.api.nodes.Node

 :init init
 :state state
 :prefix -loopnode-
 :constructors {[] []
                [language.bf.ConditionNode "[Llanguage.bf.BFNode;"] []})

(gen-class
 :name language.bf.BFRepeatingNode
 :extends com.oracle.truffle.api.nodes.Node
 :implements [com.oracle.truffle.api.nodes.RepeatingNode]
 :init init
 :state state
 :prefix -repeatingnode-
 :constructors {[] []
                [language.bf.ConditionNode "[Llanguage.bf.BFNode;"] []})

(gen-class
 :name language.bf.BFParser
 :init init
 :state state
 :prefix -bfparser-
 :constructors {[] []}
 :methods [^:static [parse [com.oracle.truffle.api.TruffleLanguage com.oracle.truffle.api.source.Source]
            com.oracle.truffle.api.nodes.RootNode]])

(defn -bfparser-parse
  [bfLanguage source]
  (let [
        frameDescriptor (FrameDescriptor.)
        ptr             (.addFrameSlot frameDescriptor "ptr" FrameSlotKind/Int)
        data            (.addFrameSlot frameDescriptor "data" FrameSlotKind/Object)
        loops           (Stack.)
        istream         (.getInputStream source)
        nodes           (java.util.ArrayList.)]


    (.add nodes (language.bf.BFInitNode. ptr data))

    (let [nodes (loop [i     (.read istream)
                       nodes nodes]
                  (if (= i -1)
                    nodes
                    (case (char i)
                      \>  (do (.add nodes (language.bf.IncPointerNode. ptr))
                              (recur (.read istream) nodes))
                      \<  (do (.add nodes (language.bf.DecPointerNode. ptr))
                              (recur (.read istream) nodes))
                      \+  (do (.add nodes (language.bf.IncDataNode. ptr data))
                              (recur (.read istream) nodes))
                      \-  (do (.add nodes (language.bf.DecDataNode. ptr data))
                              (recur (.read istream) nodes))
                      \.  (do (.add nodes (language.bf.PrintDataNode. ptr data))
                              (recur (.read istream) nodes))
                      \,  (do (.add nodes (language.bf.ReadDataNode. ptr data))
                              (recur (.read istream) nodes))
                      \[  (do (.push loops nodes)
                              (recur (.read istream) (java.util.ArrayList.)))
                      \]  (do (let [loopNode (language.bf.BFLoopNode. (language.bf.ConditionNode. ptr data)
                                                          (into-array BFNode nodes))
                                    nodes    (.pop loops)]

                                (.add nodes loopNode)

                                (recur (.read istream) nodes)))
                      (recur (.read istream) nodes))))]
      (language.bf.BFRootNode. bfLanguage frameDescriptor nodes))))

(defn -bfinitnode-init
  [ptr data]
  [[] {:ptr  ptr
       :data data}])

(defn -bfinitnode-execute
  [this frame]
  (let [{:keys [ptr data]} (.state this)]
    (.. frame (setInt ptr (int 0)))
    (.. frame (setObject data (byte-array MEMORY_SIZE)))))

(defn ^CompilerDirectives$TruffleBoundary stdinRead
  []
  (.read System/in))

(defn ^CompilerDirectives$TruffleBoundary stdoutPrint
  [b]
  (.print System/out (char b)))

(defn -readdatanode-init
  [ptr data]
  [[] {:ptr  ptr
       :data data}])

(defn -readdatanode-execute
  [this frame]
  (try
    (let [{:keys [ptr data]} (.state this)]
      (aset (.getObject frame data)
            (.getInt frame ptr)
            (byte (stdinRead))))
    (catch FrameSlotTypeException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))
    (catch IOException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))))

(defn -printdatanode-init
  [ptr data]
  [[] {:ptr  ptr
       :data data}])

(defn -printdatanode-execute
  [this frame]
  (try
    (let [{:keys [ptr data]} (.state this)]
      (stdoutPrint (aget (.getObject frame data)
                         (.getInt frame ptr))))
    (catch FrameSlotTypeException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))
    (catch IOException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))))


(defn -incdatanode-init
  [ptr data]
  [[] {:ptr  ptr
       :data data}])

(defn -incdatanode-execute
  [this frame]
  (try
    (let [{:keys [ptr data]} (.state this)
          index              (.getInt frame ptr)
          buffer             (.getObject frame data)]
      (aset buffer index (unchecked-byte (unchecked-inc (aget buffer index)))))
    (catch FrameSlotTypeException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))))

(defn -decdatanode-init
  [ptr data]
  [[] {:ptr  ptr
       :data data}])

(defn -decdatanode-execute
  [this frame]
  (try
    (let [{:keys [ptr data]} (.state this)
          index              (.getInt frame ptr)
          buffer             (.getObject frame data)]
      (aset buffer index (unchecked-byte (unchecked-dec (aget buffer index)))))
    (catch FrameSlotTypeException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))))

(defn -incpointernode-init
  [ptr]
  [[] {:ptr  ptr}])

(defn -incpointernode-execute
  [this frame]
  (try
    (let [{:keys [ptr]} (.state this)]
      (.setInt frame ptr (inc (.getInt frame ptr))))
    (catch FrameSlotTypeException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))))

(defn -decpointernode-init
  [ptr]
  [[] {:ptr  ptr}])

(defn -decpointernode-execute
  [this frame]
  (try
    (let [{:keys [ptr]} (.state this)]
      (.setInt frame ptr (dec (.getInt frame ptr))))
    (catch FrameSlotTypeException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))))

(defn -conditionnode-init
  [ptr data]
  [[] {:ptr  ptr
       :data data}])

(defn -conditionnode-execute
  [this frame]
  (.executeCondition this frame))

(defn -conditionnode-executeCondition
  [this frame]
  (try
    (let [{:keys [ptr data]} (.state this)
          index              (.getInt frame ptr)
          buffer             (.getObject frame data)]
      (not= 0 (aget buffer index)))
    (catch FrameSlotTypeException e
      (CompilerDirectives/transferToInterpreter)
      (.printStackTrace e))))

(defn -loopnode-init
  [conditionNode body]
  [[] {:loopNode (.createLoopNode (Truffle/getRuntime)
                                  (language.bf.BFRepeatingNode. conditionNode body))}])

(defn -loopnode-execute
  [this frame]
  (let [{:keys [loopNode]} (.state this)]
    (.executeLoop loopNode frame)))

(defn -repeatingnode-init
  [conditionNode body]
  [[] {:conditionNode conditionNode
       :body          body}])

(defn -repeatingnode-executeRepeating
  [this frame]
  (let [{:keys [conditionNode body]} (.state this)]
    (if (not (.executeCondition conditionNode frame))
      false
      (do (CompilerAsserts/compilationConstant (count body))

          (doseq [node body]
            (.execute node frame))

          true))))

(defn -bfrootnode-init
  [language frameDescriptor nodes]
  [[language frameDescriptor] {:nodes (into-array BFNode nodes)}])

(defn -bfrootnode-execute
  [this frame]
  (let [{:keys [nodes]} (.state this)]
    (CompilerAsserts/compilationConstant (count nodes))

    (doseq [node nodes]
      (.execute node frame))

    0))

(defn -bfcontext-init
  [bfLanguage env]
  [[] {:bfLanguage bfLanguage
       :env        env
       :memory     (byte-array MEMORY_SIZE)}])


(defn -bflanguage-init
  []
  [[] {}])

(defn -bflanguage-createContext
  [this env]
  (language.bf.BFContext. this env))

(defn -bflanguage-isObjectOfLanguage
  [_ _]
  false)

(defn -bflanguage-parse
  [this request]
  (.createCallTarget (Truffle/getRuntime)
                     (language.bf.BFParser/parse this (.getSource request))))
