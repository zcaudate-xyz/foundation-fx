(ns rt.jocl.exec-test
  (:use code.test)
  (:require [rt.jocl.exec :refer :all]
            [rt.jocl.meta :as meta]
            [rt.jocl.type :as type]
            [std.lang :as  l]
            [std.lib :as h]))

(l/script- :c)

(define.c MUL= [c a b i] (:= (. c [i]) (* (. a [i]) (. b [i]))))

(define.c G0  (get-global-id 0))

(defn.c ^{:- [:__kernel :void]
          :rt/kernel {:worksize '(fn [{:keys [a]}] [(count a)])}}
  sample
  ([:__global :const :float :* a
    :__global :const :float :* b
    :__global :float :* c]
   (var :int i := -/G0)
   (-/MUL= c a b i)))


;;
;;
;;

(defonce +exec+
  (-> (exec {:source sample
             :worksize (fn [{:keys [a]}]
                         [(count a)])})
      (h/start)))

^{:refer rt.jocl.exec/CANARY :adopt true :added "3.0"}
(fact "play around with the spec"

  (meta #'sample)
  => (contains '{:- [:__kernel :void],
                 :rt/kernel {:worksize (fn [{:keys [a]}] [(count a)])}})

  (:rt/kernel @sample)
  => '{:worksize (fn [{:keys [a]}] [(count a)])})

^{:refer rt.jocl.exec/exec-prep :added "3.0"}
(fact "preps the source for the exec"
  ^:hidden

  (exec-prep (meta/platform:default)
             (meta/device:gpu)
             "sample"
             (first (exec-source sample)))
  => map?)

^{:refer rt.jocl.exec/exec-source :added "3.0"}
(fact "preps the source for the exec"
  ^:hidden

  (def -e- 
    (exec {:source sample
           :worksize (fn [{:keys [a]}]
                       [(count a)])}))
  
  (exec-source sample)
  => [(std.string/|
       "#define MUL_eq(c,a,b,i) c[i] = (a[i] * b[i])"
       ""
       "#define G0 get_global_id(0)"
       ""
       "__kernel void sample(__global const float * a,__global const float * b,__global float * c){"
       "  int i = G0;"
       "  MUL_eq(c,a,b,i);"
       "}")

      "sample"])

^{:refer rt.jocl.exec/exec-start :added "3.0"}
(fact "starts the exec"

  (def -e- 
    (exec-start (exec {:source sample
                       :worksize (fn [{:keys [a]}]
                                   [(count a)])})))
  (exec-stop -e-))

^{:refer rt.jocl.exec/exec-stop :added "3.0"}
(fact "stops the exec")

^{:refer rt.jocl.exec/exec-invoke:worksize :added "3.0"}
(fact "gets the worksize of the executable"
  
  (vec (exec-invoke:worksize +exec+ (:spec @(:state +exec+))
                             [(float-array [10 10])]))
  => [2])

^{:refer rt.jocl.exec/set-kernel-buffer :added "3.0"}
(fact "sets the kernel buffer"

  (set-kernel-buffer (:context @(:state +exec+))
                     (:kernel  @(:state +exec+))
                     1
                     {:buffer true :const true :dsize 4}
                     {:length 10}
                     (float-array 10))
  => org.jocl.cl_mem)

^{:refer rt.jocl.exec/set-kernel-value :added "3.0"}
(comment
  "sets the kernel value"

  ;; RUNNING THIS IN EMACS/REPL CAUSES A STACKFAULT
  ;; MOST LIKELY DUE TO THREAD SAFETLY ISSUES
  (set-kernel-value (:kernel @(:state +exec+))
                    1
                    {:dsize 8}
                    1))

^{:refer rt.jocl.exec/exec-invoke:setup :added "3.0"
  
  :let [|args| [(float-array (range 10))
                (float-array (range 10))
                (float-array 10)]]}
(fact "sets up the exec"
  
  (exec-invoke:setup +exec+
                     (type/type-args (:spec @(:state +exec+))
                                     |args|)
                     |args|)
  => (contains [org.jocl.cl_mem
                org.jocl.cl_mem
                org.jocl.cl_mem]))

^{:refer rt.jocl.exec/exec-invoke:process :added "3.0"}
(fact "enqueues the kernel call")

^{:refer rt.jocl.exec/exec-invoke:output :added "3.0"}
(fact "writes to output from buffer and release")

^{:refer rt.jocl.exec/exec-invoke :added "3.0"}
(fact "main invoke function"
  
  (seq (exec-invoke +exec+
                    (float-array (range 10))
                    (float-array (range 10))
                    (float-array 10)))
  => '(0.0 1.0 4.0 9.0 16.0 25.0 36.0 49.0 64.0 81.0))

^{:refer rt.jocl.exec/exec? :added "3.0"}
(fact "checks that object is of type exec"

  (exec? +exec+)
  => true)

^{:refer rt.jocl.exec/exec :added "3.0"}
(fact "creates an opencl exec")
