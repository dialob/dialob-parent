import * as g from "react";
import Ct, { forwardRef as Op, useContext as Sp, Children as ml, isValidElement as Aa, cloneElement as Fa, useImperativeHandle as ib, useEffect as Bt, createElement as _i, createContext as lb, useRef as bn, useState as gn, useCallback as nt, useMemo as Ya, useLayoutEffect as cb } from "react";
import * as ub from "react-dom";
import Ea from "react-dom";
var go = typeof globalThis < "u" ? globalThis : typeof window < "u" ? window : typeof global < "u" ? global : typeof self < "u" ? self : {};
function ms(e) {
  return e && e.__esModule && Object.prototype.hasOwnProperty.call(e, "default") ? e.default : e;
}
function On(e) {
  if (e.__esModule) return e;
  var t = e.default;
  if (typeof t == "function") {
    var n = function o() {
      return this instanceof o ? Reflect.construct(t, arguments, this.constructor) : t.apply(this, arguments);
    };
    n.prototype = t.prototype;
  } else n = {};
  return Object.defineProperty(n, "__esModule", { value: !0 }), Object.keys(e).forEach(function(o) {
    var a = Object.getOwnPropertyDescriptor(e, o);
    Object.defineProperty(n, o, a.get ? a : {
      enumerable: !0,
      get: function() {
        return e[o];
      }
    });
  }), n;
}
var Mi = { exports: {} }, yo = {};
/**
 * @license React
 * react-jsx-runtime.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Wc;
function db() {
  if (Wc) return yo;
  Wc = 1;
  var e = Ct, t = Symbol.for("react.element"), n = Symbol.for("react.fragment"), o = Object.prototype.hasOwnProperty, a = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner, s = { key: !0, ref: !0, __self: !0, __source: !0 };
  function i(l, c, u) {
    var d, f = {}, p = null, m = null;
    u !== void 0 && (p = "" + u), c.key !== void 0 && (p = "" + c.key), c.ref !== void 0 && (m = c.ref);
    for (d in c) o.call(c, d) && !s.hasOwnProperty(d) && (f[d] = c[d]);
    if (l && l.defaultProps) for (d in c = l.defaultProps, c) f[d] === void 0 && (f[d] = c[d]);
    return { $$typeof: t, type: l, key: p, ref: m, props: f, _owner: a.current };
  }
  return yo.Fragment = n, yo.jsx = i, yo.jsxs = i, yo;
}
var vo = {};
/**
 * @license React
 * react-jsx-runtime.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Uc;
function pb() {
  return Uc || (Uc = 1, process.env.NODE_ENV !== "production" && function() {
    var e = Ct, t = Symbol.for("react.element"), n = Symbol.for("react.portal"), o = Symbol.for("react.fragment"), a = Symbol.for("react.strict_mode"), s = Symbol.for("react.profiler"), i = Symbol.for("react.provider"), l = Symbol.for("react.context"), c = Symbol.for("react.forward_ref"), u = Symbol.for("react.suspense"), d = Symbol.for("react.suspense_list"), f = Symbol.for("react.memo"), p = Symbol.for("react.lazy"), m = Symbol.for("react.offscreen"), v = Symbol.iterator, h = "@@iterator";
    function y(k) {
      if (k === null || typeof k != "object")
        return null;
      var ae = v && k[v] || k[h];
      return typeof ae == "function" ? ae : null;
    }
    var w = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
    function C(k) {
      {
        for (var ae = arguments.length, me = new Array(ae > 1 ? ae - 1 : 0), De = 1; De < ae; De++)
          me[De - 1] = arguments[De];
        E("error", k, me);
      }
    }
    function E(k, ae, me) {
      {
        var De = w.ReactDebugCurrentFrame, je = De.getStackAddendum();
        je !== "" && (ae += "%s", me = me.concat([je]));
        var He = me.map(function(Me) {
          return String(Me);
        });
        He.unshift("Warning: " + ae), Function.prototype.apply.call(console[k], console, He);
      }
    }
    var O = !1, T = !1, P = !1, S = !1, j = !1, $;
    $ = Symbol.for("react.module.reference");
    function V(k) {
      return !!(typeof k == "string" || typeof k == "function" || k === o || k === s || j || k === a || k === u || k === d || S || k === m || O || T || P || typeof k == "object" && k !== null && (k.$$typeof === p || k.$$typeof === f || k.$$typeof === i || k.$$typeof === l || k.$$typeof === c || // This needs to include all possible module reference object
      // types supported by any Flight configuration anywhere since
      // we don't know which Flight build this will end up being used
      // with.
      k.$$typeof === $ || k.getModuleId !== void 0));
    }
    function _(k, ae, me) {
      var De = k.displayName;
      if (De)
        return De;
      var je = ae.displayName || ae.name || "";
      return je !== "" ? me + "(" + je + ")" : me;
    }
    function L(k) {
      return k.displayName || "Context";
    }
    function M(k) {
      if (k == null)
        return null;
      if (typeof k.tag == "number" && C("Received an unexpected object in getComponentNameFromType(). This is likely a bug in React. Please file an issue."), typeof k == "function")
        return k.displayName || k.name || null;
      if (typeof k == "string")
        return k;
      switch (k) {
        case o:
          return "Fragment";
        case n:
          return "Portal";
        case s:
          return "Profiler";
        case a:
          return "StrictMode";
        case u:
          return "Suspense";
        case d:
          return "SuspenseList";
      }
      if (typeof k == "object")
        switch (k.$$typeof) {
          case l:
            var ae = k;
            return L(ae) + ".Consumer";
          case i:
            var me = k;
            return L(me._context) + ".Provider";
          case c:
            return _(k, k.render, "ForwardRef");
          case f:
            var De = k.displayName || null;
            return De !== null ? De : M(k.type) || "Memo";
          case p: {
            var je = k, He = je._payload, Me = je._init;
            try {
              return M(Me(He));
            } catch {
              return null;
            }
          }
        }
      return null;
    }
    var R = Object.assign, D = 0, F, z, N, q, A, H, te;
    function re() {
    }
    re.__reactDisabledLog = !0;
    function B() {
      {
        if (D === 0) {
          F = console.log, z = console.info, N = console.warn, q = console.error, A = console.group, H = console.groupCollapsed, te = console.groupEnd;
          var k = {
            configurable: !0,
            enumerable: !0,
            value: re,
            writable: !0
          };
          Object.defineProperties(console, {
            info: k,
            log: k,
            warn: k,
            error: k,
            group: k,
            groupCollapsed: k,
            groupEnd: k
          });
        }
        D++;
      }
    }
    function G() {
      {
        if (D--, D === 0) {
          var k = {
            configurable: !0,
            enumerable: !0,
            writable: !0
          };
          Object.defineProperties(console, {
            log: R({}, k, {
              value: F
            }),
            info: R({}, k, {
              value: z
            }),
            warn: R({}, k, {
              value: N
            }),
            error: R({}, k, {
              value: q
            }),
            group: R({}, k, {
              value: A
            }),
            groupCollapsed: R({}, k, {
              value: H
            }),
            groupEnd: R({}, k, {
              value: te
            })
          });
        }
        D < 0 && C("disabledDepth fell below zero. This is a bug in React. Please file an issue.");
      }
    }
    var ee = w.ReactCurrentDispatcher, W;
    function J(k, ae, me) {
      {
        if (W === void 0)
          try {
            throw Error();
          } catch (je) {
            var De = je.stack.trim().match(/\n( *(at )?)/);
            W = De && De[1] || "";
          }
        return `
` + W + k;
      }
    }
    var se = !1, le;
    {
      var X = typeof WeakMap == "function" ? WeakMap : Map;
      le = new X();
    }
    function U(k, ae) {
      if (!k || se)
        return "";
      {
        var me = le.get(k);
        if (me !== void 0)
          return me;
      }
      var De;
      se = !0;
      var je = Error.prepareStackTrace;
      Error.prepareStackTrace = void 0;
      var He;
      He = ee.current, ee.current = null, B();
      try {
        if (ae) {
          var Me = function() {
            throw Error();
          };
          if (Object.defineProperty(Me.prototype, "props", {
            set: function() {
              throw Error();
            }
          }), typeof Reflect == "object" && Reflect.construct) {
            try {
              Reflect.construct(Me, []);
            } catch (Tt) {
              De = Tt;
            }
            Reflect.construct(k, [], Me);
          } else {
            try {
              Me.call();
            } catch (Tt) {
              De = Tt;
            }
            k.call(Me.prototype);
          }
        } else {
          try {
            throw Error();
          } catch (Tt) {
            De = Tt;
          }
          k();
        }
      } catch (Tt) {
        if (Tt && De && typeof Tt.stack == "string") {
          for (var ke = Tt.stack.split(`
`), Re = De.stack.split(`
`), Ie = ke.length - 1, ct = Re.length - 1; Ie >= 1 && ct >= 0 && ke[Ie] !== Re[ct]; )
            ct--;
          for (; Ie >= 1 && ct >= 0; Ie--, ct--)
            if (ke[Ie] !== Re[ct]) {
              if (Ie !== 1 || ct !== 1)
                do
                  if (Ie--, ct--, ct < 0 || ke[Ie] !== Re[ct]) {
                    var jt = `
` + ke[Ie].replace(" at new ", " at ");
                    return k.displayName && jt.includes("<anonymous>") && (jt = jt.replace("<anonymous>", k.displayName)), typeof k == "function" && le.set(k, jt), jt;
                  }
                while (Ie >= 1 && ct >= 0);
              break;
            }
        }
      } finally {
        se = !1, ee.current = He, G(), Error.prepareStackTrace = je;
      }
      var Or = k ? k.displayName || k.name : "", Gn = Or ? J(Or) : "";
      return typeof k == "function" && le.set(k, Gn), Gn;
    }
    function K(k, ae, me) {
      return U(k, !1);
    }
    function Y(k) {
      var ae = k.prototype;
      return !!(ae && ae.isReactComponent);
    }
    function he(k, ae, me) {
      if (k == null)
        return "";
      if (typeof k == "function")
        return U(k, Y(k));
      if (typeof k == "string")
        return J(k);
      switch (k) {
        case u:
          return J("Suspense");
        case d:
          return J("SuspenseList");
      }
      if (typeof k == "object")
        switch (k.$$typeof) {
          case c:
            return K(k.render);
          case f:
            return he(k.type, ae, me);
          case p: {
            var De = k, je = De._payload, He = De._init;
            try {
              return he(He(je), ae, me);
            } catch {
            }
          }
        }
      return "";
    }
    var Oe = Object.prototype.hasOwnProperty, Ne = {}, fe = w.ReactDebugCurrentFrame;
    function ve(k) {
      if (k) {
        var ae = k._owner, me = he(k.type, k._source, ae ? ae.type : null);
        fe.setExtraStackFrame(me);
      } else
        fe.setExtraStackFrame(null);
    }
    function oe(k, ae, me, De, je) {
      {
        var He = Function.call.bind(Oe);
        for (var Me in k)
          if (He(k, Me)) {
            var ke = void 0;
            try {
              if (typeof k[Me] != "function") {
                var Re = Error((De || "React class") + ": " + me + " type `" + Me + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof k[Me] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`.");
                throw Re.name = "Invariant Violation", Re;
              }
              ke = k[Me](ae, Me, De, me, null, "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED");
            } catch (Ie) {
              ke = Ie;
            }
            ke && !(ke instanceof Error) && (ve(je), C("%s: type specification of %s `%s` is invalid; the type checker function must return `null` or an `Error` but returned a %s. You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument).", De || "React class", me, Me, typeof ke), ve(null)), ke instanceof Error && !(ke.message in Ne) && (Ne[ke.message] = !0, ve(je), C("Failed %s type: %s", me, ke.message), ve(null));
          }
      }
    }
    var ce = Array.isArray;
    function I(k) {
      return ce(k);
    }
    function Q(k) {
      {
        var ae = typeof Symbol == "function" && Symbol.toStringTag, me = ae && k[Symbol.toStringTag] || k.constructor.name || "Object";
        return me;
      }
    }
    function ne(k) {
      try {
        return ue(k), !1;
      } catch {
        return !0;
      }
    }
    function ue(k) {
      return "" + k;
    }
    function ge(k) {
      if (ne(k))
        return C("The provided key is an unsupported type %s. This value must be coerced to a string before before using it here.", Q(k)), ue(k);
    }
    var ye = w.ReactCurrentOwner, xe = {
      key: !0,
      ref: !0,
      __self: !0,
      __source: !0
    }, be, _e, st;
    st = {};
    function rt(k) {
      if (Oe.call(k, "ref")) {
        var ae = Object.getOwnPropertyDescriptor(k, "ref").get;
        if (ae && ae.isReactWarning)
          return !1;
      }
      return k.ref !== void 0;
    }
    function Qe(k) {
      if (Oe.call(k, "key")) {
        var ae = Object.getOwnPropertyDescriptor(k, "key").get;
        if (ae && ae.isReactWarning)
          return !1;
      }
      return k.key !== void 0;
    }
    function Te(k, ae) {
      if (typeof k.ref == "string" && ye.current && ae && ye.current.stateNode !== ae) {
        var me = M(ye.current.type);
        st[me] || (C('Component "%s" contains the string ref "%s". Support for string refs will be removed in a future major release. This case cannot be automatically converted to an arrow function. We ask you to manually fix this case by using useRef() or createRef() instead. Learn more about using refs safely here: https://reactjs.org/link/strict-mode-string-ref', M(ye.current.type), k.ref), st[me] = !0);
      }
    }
    function $e(k, ae) {
      {
        var me = function() {
          be || (be = !0, C("%s: `key` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", ae));
        };
        me.isReactWarning = !0, Object.defineProperty(k, "key", {
          get: me,
          configurable: !0
        });
      }
    }
    function Ge(k, ae) {
      {
        var me = function() {
          _e || (_e = !0, C("%s: `ref` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", ae));
        };
        me.isReactWarning = !0, Object.defineProperty(k, "ref", {
          get: me,
          configurable: !0
        });
      }
    }
    var xt = function(k, ae, me, De, je, He, Me) {
      var ke = {
        // This tag allows us to uniquely identify this as a React Element
        $$typeof: t,
        // Built-in properties that belong on the element
        type: k,
        key: ae,
        ref: me,
        props: Me,
        // Record the component responsible for creating this element.
        _owner: He
      };
      return ke._store = {}, Object.defineProperty(ke._store, "validated", {
        configurable: !1,
        enumerable: !1,
        writable: !0,
        value: !1
      }), Object.defineProperty(ke, "_self", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: De
      }), Object.defineProperty(ke, "_source", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: je
      }), Object.freeze && (Object.freeze(ke.props), Object.freeze(ke)), ke;
    };
    function Qt(k, ae, me, De, je) {
      {
        var He, Me = {}, ke = null, Re = null;
        me !== void 0 && (ge(me), ke = "" + me), Qe(ae) && (ge(ae.key), ke = "" + ae.key), rt(ae) && (Re = ae.ref, Te(ae, je));
        for (He in ae)
          Oe.call(ae, He) && !xe.hasOwnProperty(He) && (Me[He] = ae[He]);
        if (k && k.defaultProps) {
          var Ie = k.defaultProps;
          for (He in Ie)
            Me[He] === void 0 && (Me[He] = Ie[He]);
        }
        if (ke || Re) {
          var ct = typeof k == "function" ? k.displayName || k.name || "Unknown" : k;
          ke && $e(Me, ct), Re && Ge(Me, ct);
        }
        return xt(k, ke, Re, je, De, ye.current, Me);
      }
    }
    var Rn = w.ReactCurrentOwner, Dn = w.ReactDebugCurrentFrame;
    function It(k) {
      if (k) {
        var ae = k._owner, me = he(k.type, k._source, ae ? ae.type : null);
        Dn.setExtraStackFrame(me);
      } else
        Dn.setExtraStackFrame(null);
    }
    var on;
    on = !1;
    function $n(k) {
      return typeof k == "object" && k !== null && k.$$typeof === t;
    }
    function Ue() {
      {
        if (Rn.current) {
          var k = M(Rn.current.type);
          if (k)
            return `

Check the render method of \`` + k + "`.";
        }
        return "";
      }
    }
    function gt(k) {
      return "";
    }
    var an = {};
    function Nt(k) {
      {
        var ae = Ue();
        if (!ae) {
          var me = typeof k == "string" ? k : k.displayName || k.name;
          me && (ae = `

Check the top-level render call using <` + me + ">.");
        }
        return ae;
      }
    }
    function wa(k, ae) {
      {
        if (!k._store || k._store.validated || k.key != null)
          return;
        k._store.validated = !0;
        var me = Nt(ae);
        if (an[me])
          return;
        an[me] = !0;
        var De = "";
        k && k._owner && k._owner !== Rn.current && (De = " It was passed a child from " + M(k._owner.type) + "."), It(k), C('Each child in a list should have a unique "key" prop.%s%s See https://reactjs.org/link/warning-keys for more information.', me, De), It(null);
      }
    }
    function Er(k, ae) {
      {
        if (typeof k != "object")
          return;
        if (I(k))
          for (var me = 0; me < k.length; me++) {
            var De = k[me];
            $n(De) && wa(De, ae);
          }
        else if ($n(k))
          k._store && (k._store.validated = !0);
        else if (k) {
          var je = y(k);
          if (typeof je == "function" && je !== k.entries)
            for (var He = je.call(k), Me; !(Me = He.next()).done; )
              $n(Me.value) && wa(Me.value, ae);
        }
      }
    }
    function oi(k) {
      {
        var ae = k.type;
        if (ae == null || typeof ae == "string")
          return;
        var me;
        if (typeof ae == "function")
          me = ae.propTypes;
        else if (typeof ae == "object" && (ae.$$typeof === c || // Note: Memo only checks outer props here.
        // Inner props are checked in the reconciler.
        ae.$$typeof === f))
          me = ae.propTypes;
        else
          return;
        if (me) {
          var De = M(ae);
          oe(me, k.props, "prop", De, k);
        } else if (ae.PropTypes !== void 0 && !on) {
          on = !0;
          var je = M(ae);
          C("Component %s declared `PropTypes` instead of `propTypes`. Did you misspell the property assignment?", je || "Unknown");
        }
        typeof ae.getDefaultProps == "function" && !ae.getDefaultProps.isReactClassApproved && C("getDefaultProps is only used on classic React.createClass definitions. Use a static property named `defaultProps` instead.");
      }
    }
    function qn(k) {
      {
        for (var ae = Object.keys(k.props), me = 0; me < ae.length; me++) {
          var De = ae[me];
          if (De !== "children" && De !== "key") {
            It(k), C("Invalid prop `%s` supplied to `React.Fragment`. React.Fragment can only have `key` and `children` props.", De), It(null);
            break;
          }
        }
        k.ref !== null && (It(k), C("Invalid attribute `ref` supplied to `React.Fragment`."), It(null));
      }
    }
    var Cr = {};
    function yt(k, ae, me, De, je, He) {
      {
        var Me = V(k);
        if (!Me) {
          var ke = "";
          (k === void 0 || typeof k == "object" && k !== null && Object.keys(k).length === 0) && (ke += " You likely forgot to export your component from the file it's defined in, or you might have mixed up default and named imports.");
          var Re = gt();
          Re ? ke += Re : ke += Ue();
          var Ie;
          k === null ? Ie = "null" : I(k) ? Ie = "array" : k !== void 0 && k.$$typeof === t ? (Ie = "<" + (M(k.type) || "Unknown") + " />", ke = " Did you accidentally export a JSX literal instead of a component?") : Ie = typeof k, C("React.jsx: type is invalid -- expected a string (for built-in components) or a class/function (for composite components) but got: %s.%s", Ie, ke);
        }
        var ct = Qt(k, ae, me, je, He);
        if (ct == null)
          return ct;
        if (Me) {
          var jt = ae.children;
          if (jt !== void 0)
            if (De)
              if (I(jt)) {
                for (var Or = 0; Or < jt.length; Or++)
                  Er(jt[Or], k);
                Object.freeze && Object.freeze(jt);
              } else
                C("React.jsx: Static children should always be an array. You are likely explicitly calling React.jsxs or React.jsxDEV. Use the Babel transform instead.");
            else
              Er(jt, k);
        }
        if (Oe.call(ae, "key")) {
          var Gn = M(k), Tt = Object.keys(ae).filter(function(sb) {
            return sb !== "key";
          }), si = Tt.length > 0 ? "{key: someKey, " + Tt.join(": ..., ") + ": ...}" : "{key: someKey}";
          if (!Cr[Gn + si]) {
            var ab = Tt.length > 0 ? "{" + Tt.join(": ..., ") + ": ...}" : "{}";
            C(`A props object containing a "key" prop is being spread into JSX:
  let props = %s;
  <%s {...props} />
React keys must be passed directly to JSX without using spread:
  let props = %s;
  <%s key={someKey} {...props} />`, si, Gn, ab, Gn), Cr[Gn + si] = !0;
          }
        }
        return k === o ? qn(ct) : oi(ct), ct;
      }
    }
    function Yn(k, ae, me) {
      return yt(k, ae, me, !0);
    }
    function ai(k, ae, me) {
      return yt(k, ae, me, !1);
    }
    var Kn = ai, bo = Yn;
    vo.Fragment = o, vo.jsx = Kn, vo.jsxs = bo;
  }()), vo;
}
process.env.NODE_ENV === "production" ? Mi.exports = db() : Mi.exports = pb();
var x = Mi.exports, hl = {}, Pp = { exports: {} };
(function(e) {
  function t(n) {
    return n && n.__esModule ? n : {
      default: n
    };
  }
  e.exports = t, e.exports.__esModule = !0, e.exports.default = e.exports;
})(Pp);
var Ln = Pp.exports, ii = {};
function Sn(e, t) {
  return process.env.NODE_ENV === "production" ? () => null : function(...o) {
    return e(...o) || t(...o);
  };
}
function b() {
  return b = Object.assign ? Object.assign.bind() : function(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = arguments[t];
      for (var o in n) ({}).hasOwnProperty.call(n, o) && (e[o] = n[o]);
    }
    return e;
  }, b.apply(null, arguments);
}
function Mn(e) {
  if (typeof e != "object" || e === null)
    return !1;
  const t = Object.getPrototypeOf(e);
  return (t === null || t === Object.prototype || Object.getPrototypeOf(t) === null) && !(Symbol.toStringTag in e) && !(Symbol.iterator in e);
}
function Rp(e) {
  if (!Mn(e))
    return e;
  const t = {};
  return Object.keys(e).forEach((n) => {
    t[n] = Rp(e[n]);
  }), t;
}
function kt(e, t, n = {
  clone: !0
}) {
  const o = n.clone ? b({}, e) : e;
  return Mn(e) && Mn(t) && Object.keys(t).forEach((a) => {
    Mn(t[a]) && // Avoid prototype pollution
    Object.prototype.hasOwnProperty.call(e, a) && Mn(e[a]) ? o[a] = kt(e[a], t[a], n) : n.clone ? o[a] = Mn(t[a]) ? Rp(t[a]) : t[a] : o[a] = t[a];
  }), o;
}
const fb = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  default: kt,
  isPlainObject: Mn
}, Symbol.toStringTag, { value: "Module" }));
var Ii = { exports: {} }, Ni = { exports: {} }, Le = {};
/** @license React v16.13.1
 * react-is.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Hc;
function mb() {
  if (Hc) return Le;
  Hc = 1;
  var e = typeof Symbol == "function" && Symbol.for, t = e ? Symbol.for("react.element") : 60103, n = e ? Symbol.for("react.portal") : 60106, o = e ? Symbol.for("react.fragment") : 60107, a = e ? Symbol.for("react.strict_mode") : 60108, s = e ? Symbol.for("react.profiler") : 60114, i = e ? Symbol.for("react.provider") : 60109, l = e ? Symbol.for("react.context") : 60110, c = e ? Symbol.for("react.async_mode") : 60111, u = e ? Symbol.for("react.concurrent_mode") : 60111, d = e ? Symbol.for("react.forward_ref") : 60112, f = e ? Symbol.for("react.suspense") : 60113, p = e ? Symbol.for("react.suspense_list") : 60120, m = e ? Symbol.for("react.memo") : 60115, v = e ? Symbol.for("react.lazy") : 60116, h = e ? Symbol.for("react.block") : 60121, y = e ? Symbol.for("react.fundamental") : 60117, w = e ? Symbol.for("react.responder") : 60118, C = e ? Symbol.for("react.scope") : 60119;
  function E(T) {
    if (typeof T == "object" && T !== null) {
      var P = T.$$typeof;
      switch (P) {
        case t:
          switch (T = T.type, T) {
            case c:
            case u:
            case o:
            case s:
            case a:
            case f:
              return T;
            default:
              switch (T = T && T.$$typeof, T) {
                case l:
                case d:
                case v:
                case m:
                case i:
                  return T;
                default:
                  return P;
              }
          }
        case n:
          return P;
      }
    }
  }
  function O(T) {
    return E(T) === u;
  }
  return Le.AsyncMode = c, Le.ConcurrentMode = u, Le.ContextConsumer = l, Le.ContextProvider = i, Le.Element = t, Le.ForwardRef = d, Le.Fragment = o, Le.Lazy = v, Le.Memo = m, Le.Portal = n, Le.Profiler = s, Le.StrictMode = a, Le.Suspense = f, Le.isAsyncMode = function(T) {
    return O(T) || E(T) === c;
  }, Le.isConcurrentMode = O, Le.isContextConsumer = function(T) {
    return E(T) === l;
  }, Le.isContextProvider = function(T) {
    return E(T) === i;
  }, Le.isElement = function(T) {
    return typeof T == "object" && T !== null && T.$$typeof === t;
  }, Le.isForwardRef = function(T) {
    return E(T) === d;
  }, Le.isFragment = function(T) {
    return E(T) === o;
  }, Le.isLazy = function(T) {
    return E(T) === v;
  }, Le.isMemo = function(T) {
    return E(T) === m;
  }, Le.isPortal = function(T) {
    return E(T) === n;
  }, Le.isProfiler = function(T) {
    return E(T) === s;
  }, Le.isStrictMode = function(T) {
    return E(T) === a;
  }, Le.isSuspense = function(T) {
    return E(T) === f;
  }, Le.isValidElementType = function(T) {
    return typeof T == "string" || typeof T == "function" || T === o || T === u || T === s || T === a || T === f || T === p || typeof T == "object" && T !== null && (T.$$typeof === v || T.$$typeof === m || T.$$typeof === i || T.$$typeof === l || T.$$typeof === d || T.$$typeof === y || T.$$typeof === w || T.$$typeof === C || T.$$typeof === h);
  }, Le.typeOf = E, Le;
}
var Be = {};
/** @license React v16.13.1
 * react-is.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var qc;
function hb() {
  return qc || (qc = 1, process.env.NODE_ENV !== "production" && function() {
    var e = typeof Symbol == "function" && Symbol.for, t = e ? Symbol.for("react.element") : 60103, n = e ? Symbol.for("react.portal") : 60106, o = e ? Symbol.for("react.fragment") : 60107, a = e ? Symbol.for("react.strict_mode") : 60108, s = e ? Symbol.for("react.profiler") : 60114, i = e ? Symbol.for("react.provider") : 60109, l = e ? Symbol.for("react.context") : 60110, c = e ? Symbol.for("react.async_mode") : 60111, u = e ? Symbol.for("react.concurrent_mode") : 60111, d = e ? Symbol.for("react.forward_ref") : 60112, f = e ? Symbol.for("react.suspense") : 60113, p = e ? Symbol.for("react.suspense_list") : 60120, m = e ? Symbol.for("react.memo") : 60115, v = e ? Symbol.for("react.lazy") : 60116, h = e ? Symbol.for("react.block") : 60121, y = e ? Symbol.for("react.fundamental") : 60117, w = e ? Symbol.for("react.responder") : 60118, C = e ? Symbol.for("react.scope") : 60119;
    function E(U) {
      return typeof U == "string" || typeof U == "function" || // Note: its typeof might be other than 'symbol' or 'number' if it's a polyfill.
      U === o || U === u || U === s || U === a || U === f || U === p || typeof U == "object" && U !== null && (U.$$typeof === v || U.$$typeof === m || U.$$typeof === i || U.$$typeof === l || U.$$typeof === d || U.$$typeof === y || U.$$typeof === w || U.$$typeof === C || U.$$typeof === h);
    }
    function O(U) {
      if (typeof U == "object" && U !== null) {
        var K = U.$$typeof;
        switch (K) {
          case t:
            var Y = U.type;
            switch (Y) {
              case c:
              case u:
              case o:
              case s:
              case a:
              case f:
                return Y;
              default:
                var he = Y && Y.$$typeof;
                switch (he) {
                  case l:
                  case d:
                  case v:
                  case m:
                  case i:
                    return he;
                  default:
                    return K;
                }
            }
          case n:
            return K;
        }
      }
    }
    var T = c, P = u, S = l, j = i, $ = t, V = d, _ = o, L = v, M = m, R = n, D = s, F = a, z = f, N = !1;
    function q(U) {
      return N || (N = !0, console.warn("The ReactIs.isAsyncMode() alias has been deprecated, and will be removed in React 17+. Update your code to use ReactIs.isConcurrentMode() instead. It has the exact same API.")), A(U) || O(U) === c;
    }
    function A(U) {
      return O(U) === u;
    }
    function H(U) {
      return O(U) === l;
    }
    function te(U) {
      return O(U) === i;
    }
    function re(U) {
      return typeof U == "object" && U !== null && U.$$typeof === t;
    }
    function B(U) {
      return O(U) === d;
    }
    function G(U) {
      return O(U) === o;
    }
    function ee(U) {
      return O(U) === v;
    }
    function W(U) {
      return O(U) === m;
    }
    function J(U) {
      return O(U) === n;
    }
    function se(U) {
      return O(U) === s;
    }
    function le(U) {
      return O(U) === a;
    }
    function X(U) {
      return O(U) === f;
    }
    Be.AsyncMode = T, Be.ConcurrentMode = P, Be.ContextConsumer = S, Be.ContextProvider = j, Be.Element = $, Be.ForwardRef = V, Be.Fragment = _, Be.Lazy = L, Be.Memo = M, Be.Portal = R, Be.Profiler = D, Be.StrictMode = F, Be.Suspense = z, Be.isAsyncMode = q, Be.isConcurrentMode = A, Be.isContextConsumer = H, Be.isContextProvider = te, Be.isElement = re, Be.isForwardRef = B, Be.isFragment = G, Be.isLazy = ee, Be.isMemo = W, Be.isPortal = J, Be.isProfiler = se, Be.isStrictMode = le, Be.isSuspense = X, Be.isValidElementType = E, Be.typeOf = O;
  }()), Be;
}
process.env.NODE_ENV === "production" ? Ni.exports = mb() : Ni.exports = hb();
var bl = Ni.exports;
/*
object-assign
(c) Sindre Sorhus
@license MIT
*/
var li, Yc;
function bb() {
  if (Yc) return li;
  Yc = 1;
  var e = Object.getOwnPropertySymbols, t = Object.prototype.hasOwnProperty, n = Object.prototype.propertyIsEnumerable;
  function o(s) {
    if (s == null)
      throw new TypeError("Object.assign cannot be called with null or undefined");
    return Object(s);
  }
  function a() {
    try {
      if (!Object.assign)
        return !1;
      var s = new String("abc");
      if (s[5] = "de", Object.getOwnPropertyNames(s)[0] === "5")
        return !1;
      for (var i = {}, l = 0; l < 10; l++)
        i["_" + String.fromCharCode(l)] = l;
      var c = Object.getOwnPropertyNames(i).map(function(d) {
        return i[d];
      });
      if (c.join("") !== "0123456789")
        return !1;
      var u = {};
      return "abcdefghijklmnopqrst".split("").forEach(function(d) {
        u[d] = d;
      }), Object.keys(Object.assign({}, u)).join("") === "abcdefghijklmnopqrst";
    } catch {
      return !1;
    }
  }
  return li = a() ? Object.assign : function(s, i) {
    for (var l, c = o(s), u, d = 1; d < arguments.length; d++) {
      l = Object(arguments[d]);
      for (var f in l)
        t.call(l, f) && (c[f] = l[f]);
      if (e) {
        u = e(l);
        for (var p = 0; p < u.length; p++)
          n.call(l, u[p]) && (c[u[p]] = l[u[p]]);
      }
    }
    return c;
  }, li;
}
var ci, Kc;
function gl() {
  if (Kc) return ci;
  Kc = 1;
  var e = "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED";
  return ci = e, ci;
}
var ui, Gc;
function Dp() {
  return Gc || (Gc = 1, ui = Function.call.bind(Object.prototype.hasOwnProperty)), ui;
}
var di, Xc;
function gb() {
  if (Xc) return di;
  Xc = 1;
  var e = function() {
  };
  if (process.env.NODE_ENV !== "production") {
    var t = gl(), n = {}, o = Dp();
    e = function(s) {
      var i = "Warning: " + s;
      typeof console < "u" && console.error(i);
      try {
        throw new Error(i);
      } catch {
      }
    };
  }
  function a(s, i, l, c, u) {
    if (process.env.NODE_ENV !== "production") {
      for (var d in s)
        if (o(s, d)) {
          var f;
          try {
            if (typeof s[d] != "function") {
              var p = Error(
                (c || "React class") + ": " + l + " type `" + d + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof s[d] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`."
              );
              throw p.name = "Invariant Violation", p;
            }
            f = s[d](i, d, c, l, null, t);
          } catch (v) {
            f = v;
          }
          if (f && !(f instanceof Error) && e(
            (c || "React class") + ": type specification of " + l + " `" + d + "` is invalid; the type checker function must return `null` or an `Error` but returned a " + typeof f + ". You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument)."
          ), f instanceof Error && !(f.message in n)) {
            n[f.message] = !0;
            var m = u ? u() : "";
            e(
              "Failed " + l + " type: " + f.message + (m ?? "")
            );
          }
        }
    }
  }
  return a.resetWarningCache = function() {
    process.env.NODE_ENV !== "production" && (n = {});
  }, di = a, di;
}
var pi, Zc;
function yb() {
  if (Zc) return pi;
  Zc = 1;
  var e = bl, t = bb(), n = gl(), o = Dp(), a = gb(), s = function() {
  };
  process.env.NODE_ENV !== "production" && (s = function(l) {
    var c = "Warning: " + l;
    typeof console < "u" && console.error(c);
    try {
      throw new Error(c);
    } catch {
    }
  });
  function i() {
    return null;
  }
  return pi = function(l, c) {
    var u = typeof Symbol == "function" && Symbol.iterator, d = "@@iterator";
    function f(A) {
      var H = A && (u && A[u] || A[d]);
      if (typeof H == "function")
        return H;
    }
    var p = "<<anonymous>>", m = {
      array: w("array"),
      bigint: w("bigint"),
      bool: w("boolean"),
      func: w("function"),
      number: w("number"),
      object: w("object"),
      string: w("string"),
      symbol: w("symbol"),
      any: C(),
      arrayOf: E,
      element: O(),
      elementType: T(),
      instanceOf: P,
      node: V(),
      objectOf: j,
      oneOf: S,
      oneOfType: $,
      shape: L,
      exact: M
    };
    function v(A, H) {
      return A === H ? A !== 0 || 1 / A === 1 / H : A !== A && H !== H;
    }
    function h(A, H) {
      this.message = A, this.data = H && typeof H == "object" ? H : {}, this.stack = "";
    }
    h.prototype = Error.prototype;
    function y(A) {
      if (process.env.NODE_ENV !== "production")
        var H = {}, te = 0;
      function re(G, ee, W, J, se, le, X) {
        if (J = J || p, le = le || W, X !== n) {
          if (c) {
            var U = new Error(
              "Calling PropTypes validators directly is not supported by the `prop-types` package. Use `PropTypes.checkPropTypes()` to call them. Read more at http://fb.me/use-check-prop-types"
            );
            throw U.name = "Invariant Violation", U;
          } else if (process.env.NODE_ENV !== "production" && typeof console < "u") {
            var K = J + ":" + W;
            !H[K] && // Avoid spamming the console because they are often not actionable except for lib authors
            te < 3 && (s(
              "You are manually calling a React.PropTypes validation function for the `" + le + "` prop on `" + J + "`. This is deprecated and will throw in the standalone `prop-types` package. You may be seeing this warning due to a third-party PropTypes library. See https://fb.me/react-warning-dont-call-proptypes for details."
            ), H[K] = !0, te++);
          }
        }
        return ee[W] == null ? G ? ee[W] === null ? new h("The " + se + " `" + le + "` is marked as required " + ("in `" + J + "`, but its value is `null`.")) : new h("The " + se + " `" + le + "` is marked as required in " + ("`" + J + "`, but its value is `undefined`.")) : null : A(ee, W, J, se, le);
      }
      var B = re.bind(null, !1);
      return B.isRequired = re.bind(null, !0), B;
    }
    function w(A) {
      function H(te, re, B, G, ee, W) {
        var J = te[re], se = F(J);
        if (se !== A) {
          var le = z(J);
          return new h(
            "Invalid " + G + " `" + ee + "` of type " + ("`" + le + "` supplied to `" + B + "`, expected ") + ("`" + A + "`."),
            { expectedType: A }
          );
        }
        return null;
      }
      return y(H);
    }
    function C() {
      return y(i);
    }
    function E(A) {
      function H(te, re, B, G, ee) {
        if (typeof A != "function")
          return new h("Property `" + ee + "` of component `" + B + "` has invalid PropType notation inside arrayOf.");
        var W = te[re];
        if (!Array.isArray(W)) {
          var J = F(W);
          return new h("Invalid " + G + " `" + ee + "` of type " + ("`" + J + "` supplied to `" + B + "`, expected an array."));
        }
        for (var se = 0; se < W.length; se++) {
          var le = A(W, se, B, G, ee + "[" + se + "]", n);
          if (le instanceof Error)
            return le;
        }
        return null;
      }
      return y(H);
    }
    function O() {
      function A(H, te, re, B, G) {
        var ee = H[te];
        if (!l(ee)) {
          var W = F(ee);
          return new h("Invalid " + B + " `" + G + "` of type " + ("`" + W + "` supplied to `" + re + "`, expected a single ReactElement."));
        }
        return null;
      }
      return y(A);
    }
    function T() {
      function A(H, te, re, B, G) {
        var ee = H[te];
        if (!e.isValidElementType(ee)) {
          var W = F(ee);
          return new h("Invalid " + B + " `" + G + "` of type " + ("`" + W + "` supplied to `" + re + "`, expected a single ReactElement type."));
        }
        return null;
      }
      return y(A);
    }
    function P(A) {
      function H(te, re, B, G, ee) {
        if (!(te[re] instanceof A)) {
          var W = A.name || p, J = q(te[re]);
          return new h("Invalid " + G + " `" + ee + "` of type " + ("`" + J + "` supplied to `" + B + "`, expected ") + ("instance of `" + W + "`."));
        }
        return null;
      }
      return y(H);
    }
    function S(A) {
      if (!Array.isArray(A))
        return process.env.NODE_ENV !== "production" && (arguments.length > 1 ? s(
          "Invalid arguments supplied to oneOf, expected an array, got " + arguments.length + " arguments. A common mistake is to write oneOf(x, y, z) instead of oneOf([x, y, z])."
        ) : s("Invalid argument supplied to oneOf, expected an array.")), i;
      function H(te, re, B, G, ee) {
        for (var W = te[re], J = 0; J < A.length; J++)
          if (v(W, A[J]))
            return null;
        var se = JSON.stringify(A, function(X, U) {
          var K = z(U);
          return K === "symbol" ? String(U) : U;
        });
        return new h("Invalid " + G + " `" + ee + "` of value `" + String(W) + "` " + ("supplied to `" + B + "`, expected one of " + se + "."));
      }
      return y(H);
    }
    function j(A) {
      function H(te, re, B, G, ee) {
        if (typeof A != "function")
          return new h("Property `" + ee + "` of component `" + B + "` has invalid PropType notation inside objectOf.");
        var W = te[re], J = F(W);
        if (J !== "object")
          return new h("Invalid " + G + " `" + ee + "` of type " + ("`" + J + "` supplied to `" + B + "`, expected an object."));
        for (var se in W)
          if (o(W, se)) {
            var le = A(W, se, B, G, ee + "." + se, n);
            if (le instanceof Error)
              return le;
          }
        return null;
      }
      return y(H);
    }
    function $(A) {
      if (!Array.isArray(A))
        return process.env.NODE_ENV !== "production" && s("Invalid argument supplied to oneOfType, expected an instance of array."), i;
      for (var H = 0; H < A.length; H++) {
        var te = A[H];
        if (typeof te != "function")
          return s(
            "Invalid argument supplied to oneOfType. Expected an array of check functions, but received " + N(te) + " at index " + H + "."
          ), i;
      }
      function re(B, G, ee, W, J) {
        for (var se = [], le = 0; le < A.length; le++) {
          var X = A[le], U = X(B, G, ee, W, J, n);
          if (U == null)
            return null;
          U.data && o(U.data, "expectedType") && se.push(U.data.expectedType);
        }
        var K = se.length > 0 ? ", expected one of type [" + se.join(", ") + "]" : "";
        return new h("Invalid " + W + " `" + J + "` supplied to " + ("`" + ee + "`" + K + "."));
      }
      return y(re);
    }
    function V() {
      function A(H, te, re, B, G) {
        return R(H[te]) ? null : new h("Invalid " + B + " `" + G + "` supplied to " + ("`" + re + "`, expected a ReactNode."));
      }
      return y(A);
    }
    function _(A, H, te, re, B) {
      return new h(
        (A || "React class") + ": " + H + " type `" + te + "." + re + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + B + "`."
      );
    }
    function L(A) {
      function H(te, re, B, G, ee) {
        var W = te[re], J = F(W);
        if (J !== "object")
          return new h("Invalid " + G + " `" + ee + "` of type `" + J + "` " + ("supplied to `" + B + "`, expected `object`."));
        for (var se in A) {
          var le = A[se];
          if (typeof le != "function")
            return _(B, G, ee, se, z(le));
          var X = le(W, se, B, G, ee + "." + se, n);
          if (X)
            return X;
        }
        return null;
      }
      return y(H);
    }
    function M(A) {
      function H(te, re, B, G, ee) {
        var W = te[re], J = F(W);
        if (J !== "object")
          return new h("Invalid " + G + " `" + ee + "` of type `" + J + "` " + ("supplied to `" + B + "`, expected `object`."));
        var se = t({}, te[re], A);
        for (var le in se) {
          var X = A[le];
          if (o(A, le) && typeof X != "function")
            return _(B, G, ee, le, z(X));
          if (!X)
            return new h(
              "Invalid " + G + " `" + ee + "` key `" + le + "` supplied to `" + B + "`.\nBad object: " + JSON.stringify(te[re], null, "  ") + `
Valid keys: ` + JSON.stringify(Object.keys(A), null, "  ")
            );
          var U = X(W, le, B, G, ee + "." + le, n);
          if (U)
            return U;
        }
        return null;
      }
      return y(H);
    }
    function R(A) {
      switch (typeof A) {
        case "number":
        case "string":
        case "undefined":
          return !0;
        case "boolean":
          return !A;
        case "object":
          if (Array.isArray(A))
            return A.every(R);
          if (A === null || l(A))
            return !0;
          var H = f(A);
          if (H) {
            var te = H.call(A), re;
            if (H !== A.entries) {
              for (; !(re = te.next()).done; )
                if (!R(re.value))
                  return !1;
            } else
              for (; !(re = te.next()).done; ) {
                var B = re.value;
                if (B && !R(B[1]))
                  return !1;
              }
          } else
            return !1;
          return !0;
        default:
          return !1;
      }
    }
    function D(A, H) {
      return A === "symbol" ? !0 : H ? H["@@toStringTag"] === "Symbol" || typeof Symbol == "function" && H instanceof Symbol : !1;
    }
    function F(A) {
      var H = typeof A;
      return Array.isArray(A) ? "array" : A instanceof RegExp ? "object" : D(H, A) ? "symbol" : H;
    }
    function z(A) {
      if (typeof A > "u" || A === null)
        return "" + A;
      var H = F(A);
      if (H === "object") {
        if (A instanceof Date)
          return "date";
        if (A instanceof RegExp)
          return "regexp";
      }
      return H;
    }
    function N(A) {
      var H = z(A);
      switch (H) {
        case "array":
        case "object":
          return "an " + H;
        case "boolean":
        case "date":
        case "regexp":
          return "a " + H;
        default:
          return H;
      }
    }
    function q(A) {
      return !A.constructor || !A.constructor.name ? p : A.constructor.name;
    }
    return m.checkPropTypes = a, m.resetWarningCache = a.resetWarningCache, m.PropTypes = m, m;
  }, pi;
}
var fi, Jc;
function vb() {
  if (Jc) return fi;
  Jc = 1;
  var e = gl();
  function t() {
  }
  function n() {
  }
  return n.resetWarningCache = t, fi = function() {
    function o(i, l, c, u, d, f) {
      if (f !== e) {
        var p = new Error(
          "Calling PropTypes validators directly is not supported by the `prop-types` package. Use PropTypes.checkPropTypes() to call them. Read more at http://fb.me/use-check-prop-types"
        );
        throw p.name = "Invariant Violation", p;
      }
    }
    o.isRequired = o;
    function a() {
      return o;
    }
    var s = {
      array: o,
      bigint: o,
      bool: o,
      func: o,
      number: o,
      object: o,
      string: o,
      symbol: o,
      any: o,
      arrayOf: a,
      element: o,
      elementType: o,
      instanceOf: a,
      node: o,
      objectOf: a,
      oneOf: a,
      oneOfType: a,
      shape: a,
      exact: a,
      checkPropTypes: n,
      resetWarningCache: t
    };
    return s.PropTypes = s, s;
  }, fi;
}
if (process.env.NODE_ENV !== "production") {
  var xb = bl, Tb = !0;
  Ii.exports = yb()(xb.isElement, Tb);
} else
  Ii.exports = vb()();
var wb = Ii.exports;
const r = /* @__PURE__ */ ms(wb);
function Eb(e) {
  const {
    prototype: t = {}
  } = e;
  return !!t.isReactComponent;
}
function $p(e, t, n, o, a) {
  const s = e[t], i = a || t;
  if (s == null || // When server-side rendering React doesn't warn either.
  // This is not an accurate check for SSR.
  // This is only in place for Emotion compat.
  // TODO: Revisit once https://github.com/facebook/react/issues/20047 is resolved.
  typeof window > "u")
    return null;
  let l;
  const c = s.type;
  return typeof c == "function" && !Eb(c) && (l = "Did you accidentally use a plain function component for an element instead?"), l !== void 0 ? new Error(`Invalid ${o} \`${i}\` supplied to \`${n}\`. Expected an element that can hold a ref. ${l} For more information see https://mui.com/r/caveat-with-refs-guide`) : null;
}
const ao = Sn(r.element, $p);
ao.isRequired = Sn(r.element.isRequired, $p);
function Cb(e) {
  const {
    prototype: t = {}
  } = e;
  return !!t.isReactComponent;
}
function Ob(e, t, n, o, a) {
  const s = e[t], i = a || t;
  if (s == null || // When server-side rendering React doesn't warn either.
  // This is not an accurate check for SSR.
  // This is only in place for emotion compat.
  // TODO: Revisit once https://github.com/facebook/react/issues/20047 is resolved.
  typeof window > "u")
    return null;
  let l;
  return typeof s == "function" && !Cb(s) && (l = "Did you accidentally provide a plain function component instead?"), l !== void 0 ? new Error(`Invalid ${o} \`${i}\` supplied to \`${n}\`. Expected an element type that can hold a ref. ${l} For more information see https://mui.com/r/caveat-with-refs-guide`) : null;
}
const hs = Sn(r.elementType, Ob), Sb = "exact-prop: ​";
function kp(e) {
  return process.env.NODE_ENV === "production" ? e : b({}, e, {
    [Sb]: (t) => {
      const n = Object.keys(t).filter((o) => !e.hasOwnProperty(o));
      return n.length > 0 ? new Error(`The following props are not supported: ${n.map((o) => `\`${o}\``).join(", ")}. Please remove them.`) : null;
    }
  });
}
function xn(e) {
  let t = "https://mui.com/production-error/?code=" + e;
  for (let n = 1; n < arguments.length; n += 1)
    t += "&args[]=" + encodeURIComponent(arguments[n]);
  return "Minified MUI error #" + e + "; visit " + t + " for the full message.";
}
const Pb = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  default: xn
}, Symbol.toStringTag, { value: "Module" }));
var ji = { exports: {} }, ze = {};
/**
 * @license React
 * react-is.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Qc;
function Rb() {
  if (Qc) return ze;
  Qc = 1;
  var e = Symbol.for("react.element"), t = Symbol.for("react.portal"), n = Symbol.for("react.fragment"), o = Symbol.for("react.strict_mode"), a = Symbol.for("react.profiler"), s = Symbol.for("react.provider"), i = Symbol.for("react.context"), l = Symbol.for("react.server_context"), c = Symbol.for("react.forward_ref"), u = Symbol.for("react.suspense"), d = Symbol.for("react.suspense_list"), f = Symbol.for("react.memo"), p = Symbol.for("react.lazy"), m = Symbol.for("react.offscreen"), v;
  v = Symbol.for("react.module.reference");
  function h(y) {
    if (typeof y == "object" && y !== null) {
      var w = y.$$typeof;
      switch (w) {
        case e:
          switch (y = y.type, y) {
            case n:
            case a:
            case o:
            case u:
            case d:
              return y;
            default:
              switch (y = y && y.$$typeof, y) {
                case l:
                case i:
                case c:
                case p:
                case f:
                case s:
                  return y;
                default:
                  return w;
              }
          }
        case t:
          return w;
      }
    }
  }
  return ze.ContextConsumer = i, ze.ContextProvider = s, ze.Element = e, ze.ForwardRef = c, ze.Fragment = n, ze.Lazy = p, ze.Memo = f, ze.Portal = t, ze.Profiler = a, ze.StrictMode = o, ze.Suspense = u, ze.SuspenseList = d, ze.isAsyncMode = function() {
    return !1;
  }, ze.isConcurrentMode = function() {
    return !1;
  }, ze.isContextConsumer = function(y) {
    return h(y) === i;
  }, ze.isContextProvider = function(y) {
    return h(y) === s;
  }, ze.isElement = function(y) {
    return typeof y == "object" && y !== null && y.$$typeof === e;
  }, ze.isForwardRef = function(y) {
    return h(y) === c;
  }, ze.isFragment = function(y) {
    return h(y) === n;
  }, ze.isLazy = function(y) {
    return h(y) === p;
  }, ze.isMemo = function(y) {
    return h(y) === f;
  }, ze.isPortal = function(y) {
    return h(y) === t;
  }, ze.isProfiler = function(y) {
    return h(y) === a;
  }, ze.isStrictMode = function(y) {
    return h(y) === o;
  }, ze.isSuspense = function(y) {
    return h(y) === u;
  }, ze.isSuspenseList = function(y) {
    return h(y) === d;
  }, ze.isValidElementType = function(y) {
    return typeof y == "string" || typeof y == "function" || y === n || y === a || y === o || y === u || y === d || y === m || typeof y == "object" && y !== null && (y.$$typeof === p || y.$$typeof === f || y.$$typeof === s || y.$$typeof === i || y.$$typeof === c || y.$$typeof === v || y.getModuleId !== void 0);
  }, ze.typeOf = h, ze;
}
var We = {};
/**
 * @license React
 * react-is.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var eu;
function Db() {
  return eu || (eu = 1, process.env.NODE_ENV !== "production" && function() {
    var e = Symbol.for("react.element"), t = Symbol.for("react.portal"), n = Symbol.for("react.fragment"), o = Symbol.for("react.strict_mode"), a = Symbol.for("react.profiler"), s = Symbol.for("react.provider"), i = Symbol.for("react.context"), l = Symbol.for("react.server_context"), c = Symbol.for("react.forward_ref"), u = Symbol.for("react.suspense"), d = Symbol.for("react.suspense_list"), f = Symbol.for("react.memo"), p = Symbol.for("react.lazy"), m = Symbol.for("react.offscreen"), v = !1, h = !1, y = !1, w = !1, C = !1, E;
    E = Symbol.for("react.module.reference");
    function O(Y) {
      return !!(typeof Y == "string" || typeof Y == "function" || Y === n || Y === a || C || Y === o || Y === u || Y === d || w || Y === m || v || h || y || typeof Y == "object" && Y !== null && (Y.$$typeof === p || Y.$$typeof === f || Y.$$typeof === s || Y.$$typeof === i || Y.$$typeof === c || // This needs to include all possible module reference object
      // types supported by any Flight configuration anywhere since
      // we don't know which Flight build this will end up being used
      // with.
      Y.$$typeof === E || Y.getModuleId !== void 0));
    }
    function T(Y) {
      if (typeof Y == "object" && Y !== null) {
        var he = Y.$$typeof;
        switch (he) {
          case e:
            var Oe = Y.type;
            switch (Oe) {
              case n:
              case a:
              case o:
              case u:
              case d:
                return Oe;
              default:
                var Ne = Oe && Oe.$$typeof;
                switch (Ne) {
                  case l:
                  case i:
                  case c:
                  case p:
                  case f:
                  case s:
                    return Ne;
                  default:
                    return he;
                }
            }
          case t:
            return he;
        }
      }
    }
    var P = i, S = s, j = e, $ = c, V = n, _ = p, L = f, M = t, R = a, D = o, F = u, z = d, N = !1, q = !1;
    function A(Y) {
      return N || (N = !0, console.warn("The ReactIs.isAsyncMode() alias has been deprecated, and will be removed in React 18+.")), !1;
    }
    function H(Y) {
      return q || (q = !0, console.warn("The ReactIs.isConcurrentMode() alias has been deprecated, and will be removed in React 18+.")), !1;
    }
    function te(Y) {
      return T(Y) === i;
    }
    function re(Y) {
      return T(Y) === s;
    }
    function B(Y) {
      return typeof Y == "object" && Y !== null && Y.$$typeof === e;
    }
    function G(Y) {
      return T(Y) === c;
    }
    function ee(Y) {
      return T(Y) === n;
    }
    function W(Y) {
      return T(Y) === p;
    }
    function J(Y) {
      return T(Y) === f;
    }
    function se(Y) {
      return T(Y) === t;
    }
    function le(Y) {
      return T(Y) === a;
    }
    function X(Y) {
      return T(Y) === o;
    }
    function U(Y) {
      return T(Y) === u;
    }
    function K(Y) {
      return T(Y) === d;
    }
    We.ContextConsumer = P, We.ContextProvider = S, We.Element = j, We.ForwardRef = $, We.Fragment = V, We.Lazy = _, We.Memo = L, We.Portal = M, We.Profiler = R, We.StrictMode = D, We.Suspense = F, We.SuspenseList = z, We.isAsyncMode = A, We.isConcurrentMode = H, We.isContextConsumer = te, We.isContextProvider = re, We.isElement = B, We.isForwardRef = G, We.isFragment = ee, We.isLazy = W, We.isMemo = J, We.isPortal = se, We.isProfiler = le, We.isStrictMode = X, We.isSuspense = U, We.isSuspenseList = K, We.isValidElementType = O, We.typeOf = T;
  }()), We;
}
process.env.NODE_ENV === "production" ? ji.exports = Rb() : ji.exports = Db();
var zo = ji.exports;
const $b = /^\s*function(?:\s|\s*\/\*.*\*\/\s*)+([^(\s/]*)\s*/;
function _p(e) {
  const t = `${e}`.match($b);
  return t && t[1] || "";
}
function Mp(e, t = "") {
  return e.displayName || e.name || _p(e) || t;
}
function tu(e, t, n) {
  const o = Mp(t);
  return e.displayName || (o !== "" ? `${n}(${o})` : n);
}
function kb(e) {
  if (e != null) {
    if (typeof e == "string")
      return e;
    if (typeof e == "function")
      return Mp(e, "Component");
    if (typeof e == "object")
      switch (e.$$typeof) {
        case zo.ForwardRef:
          return tu(e, e.render, "ForwardRef");
        case zo.Memo:
          return tu(e, e.type, "memo");
        default:
          return;
      }
  }
}
const _b = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  default: kb,
  getFunctionName: _p
}, Symbol.toStringTag, { value: "Module" }));
function Tn(e, t, n, o, a) {
  if (process.env.NODE_ENV === "production")
    return null;
  const s = e[t], i = a || t;
  return s == null ? null : s && s.nodeType !== 1 ? new Error(`Invalid ${o} \`${i}\` supplied to \`${n}\`. Expected an HTMLElement.`) : null;
}
const vt = r.oneOfType([r.func, r.object]);
function de(e) {
  if (typeof e != "string")
    throw new Error(process.env.NODE_ENV !== "production" ? "MUI: `capitalize(string)` expects a string argument." : xn(7));
  return e.charAt(0).toUpperCase() + e.slice(1);
}
const Mb = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  default: de
}, Symbol.toStringTag, { value: "Module" }));
function Ai(...e) {
  return e.reduce((t, n) => n == null ? t : function(...a) {
    t.apply(this, a), n.apply(this, a);
  }, () => {
  });
}
function yl(e, t = 166) {
  let n;
  function o(...a) {
    const s = () => {
      e.apply(this, a);
    };
    clearTimeout(n), n = setTimeout(s, t);
  }
  return o.clear = () => {
    clearTimeout(n);
  }, o;
}
function Ib(e, t) {
  return process.env.NODE_ENV === "production" ? () => null : (n, o, a, s, i) => {
    const l = a || "<<anonymous>>", c = i || o;
    return typeof n[o] < "u" ? new Error(`The ${s} \`${c}\` of \`${l}\` is deprecated. ${t}`) : null;
  };
}
function Br(e, t) {
  var n, o;
  return /* @__PURE__ */ g.isValidElement(e) && t.indexOf(
    // For server components `muiName` is avaialble in element.type._payload.value.muiName
    // relevant info - https://github.com/facebook/react/blob/2807d781a08db8e9873687fccc25c0f12b4fb3d4/packages/react/src/ReactLazy.js#L45
    // eslint-disable-next-line no-underscore-dangle
    (n = e.type.muiName) != null ? n : (o = e.type) == null || (o = o._payload) == null || (o = o.value) == null ? void 0 : o.muiName
  ) !== -1;
}
function dt(e) {
  return e && e.ownerDocument || document;
}
function Fn(e) {
  return dt(e).defaultView || window;
}
function Nb(e, t) {
  if (process.env.NODE_ENV === "production")
    return () => null;
  const n = t ? b({}, t.propTypes) : null;
  return (a) => (s, i, l, c, u, ...d) => {
    const f = u || i, p = n == null ? void 0 : n[f];
    if (p) {
      const m = p(s, i, l, c, u, ...d);
      if (m)
        return m;
    }
    return typeof s[i] < "u" && !s[a] ? new Error(`The prop \`${f}\` of \`${e}\` can only be used together with the \`${a}\` prop.`) : null;
  };
}
function Ka(e, t) {
  typeof e == "function" ? e(t) : e && (e.current = t);
}
const ft = typeof window < "u" ? g.useLayoutEffect : g.useEffect;
let nu = 0;
function jb(e) {
  const [t, n] = g.useState(e), o = e || t;
  return g.useEffect(() => {
    t == null && (nu += 1, n(`mui-${nu}`));
  }, [t]), o;
}
const ru = g.useId;
function Bn(e) {
  if (ru !== void 0) {
    const t = ru();
    return e ?? t;
  }
  return jb(e);
}
function Ip(e, t, n, o, a) {
  if (process.env.NODE_ENV === "production")
    return null;
  const s = a || t;
  return typeof e[t] < "u" ? new Error(`The prop \`${s}\` is not supported. Please remove it.`) : null;
}
function Ht({
  controlled: e,
  default: t,
  name: n,
  state: o = "value"
}) {
  const {
    current: a
  } = g.useRef(e !== void 0), [s, i] = g.useState(t), l = a ? e : s;
  if (process.env.NODE_ENV !== "production") {
    g.useEffect(() => {
      a !== (e !== void 0) && console.error([`MUI: A component is changing the ${a ? "" : "un"}controlled ${o} state of ${n} to be ${a ? "un" : ""}controlled.`, "Elements should not switch from uncontrolled to controlled (or vice versa).", `Decide between using a controlled or uncontrolled ${n} element for the lifetime of the component.`, "The nature of the state is determined during the first render. It's considered controlled if the value is not `undefined`.", "More info: https://fb.me/react-controlled-components"].join(`
`));
    }, [o, n, e]);
    const {
      current: u
    } = g.useRef(t);
    g.useEffect(() => {
      !a && !Object.is(u, t) && console.error([`MUI: A component is changing the default ${o} state of an uncontrolled ${n} after being initialized. To suppress this warning opt to use a controlled ${n}.`].join(`
`));
    }, [JSON.stringify(t)]);
  }
  const c = g.useCallback((u) => {
    a || i(u);
  }, []);
  return [l, c];
}
function we(e) {
  const t = g.useRef(e);
  return ft(() => {
    t.current = e;
  }), g.useRef((...n) => (
    // @ts-expect-error hide `this`
    (0, t.current)(...n)
  )).current;
}
function Ke(...e) {
  return g.useMemo(() => e.every((t) => t == null) ? null : (t) => {
    e.forEach((n) => {
      Ka(n, t);
    });
  }, e);
}
const ou = {};
function Ab(e, t) {
  const n = g.useRef(ou);
  return n.current === ou && (n.current = e(t)), n;
}
const Fb = [];
function Vb(e) {
  g.useEffect(e, Fb);
}
class ra {
  constructor() {
    this.currentId = null, this.clear = () => {
      this.currentId !== null && (clearTimeout(this.currentId), this.currentId = null);
    }, this.disposeEffect = () => this.clear;
  }
  static create() {
    return new ra();
  }
  /**
   * Executes `fn` after `delay`, clearing any previously scheduled call.
   */
  start(t, n) {
    this.clear(), this.currentId = setTimeout(() => {
      this.currentId = null, n();
    }, t);
  }
}
function jr() {
  const e = Ab(ra.create).current;
  return Vb(e.disposeEffect), e;
}
let bs = !0, Fi = !1;
const Lb = new ra(), Bb = {
  text: !0,
  search: !0,
  url: !0,
  tel: !0,
  email: !0,
  password: !0,
  number: !0,
  date: !0,
  month: !0,
  week: !0,
  time: !0,
  datetime: !0,
  "datetime-local": !0
};
function zb(e) {
  const {
    type: t,
    tagName: n
  } = e;
  return !!(n === "INPUT" && Bb[t] && !e.readOnly || n === "TEXTAREA" && !e.readOnly || e.isContentEditable);
}
function Wb(e) {
  e.metaKey || e.altKey || e.ctrlKey || (bs = !0);
}
function mi() {
  bs = !1;
}
function Ub() {
  this.visibilityState === "hidden" && Fi && (bs = !0);
}
function Hb(e) {
  e.addEventListener("keydown", Wb, !0), e.addEventListener("mousedown", mi, !0), e.addEventListener("pointerdown", mi, !0), e.addEventListener("touchstart", mi, !0), e.addEventListener("visibilitychange", Ub, !0);
}
function qb(e) {
  const {
    target: t
  } = e;
  try {
    return t.matches(":focus-visible");
  } catch {
  }
  return bs || zb(t);
}
function vl() {
  const e = g.useCallback((a) => {
    a != null && Hb(a.ownerDocument);
  }, []), t = g.useRef(!1);
  function n() {
    return t.current ? (Fi = !0, Lb.start(100, () => {
      Fi = !1;
    }), t.current = !1, !0) : !1;
  }
  function o(a) {
    return qb(a) ? (t.current = !0, !0) : !1;
  }
  return {
    isFocusVisibleRef: t,
    onFocus: o,
    onBlur: n,
    ref: e
  };
}
function Np(e) {
  const t = e.documentElement.clientWidth;
  return Math.abs(window.innerWidth - t);
}
function Yb(e) {
  const t = typeof e;
  switch (t) {
    case "number":
      return Number.isNaN(e) ? "NaN" : Number.isFinite(e) ? e !== Math.floor(e) ? "float" : "number" : "Infinity";
    case "object":
      return e === null ? "null" : e.constructor.name;
    default:
      return t;
  }
}
function Kb(e) {
  return typeof e == "number" && isFinite(e) && Math.floor(e) === e;
}
const Gb = Number.isInteger || Kb;
function jp(e, t, n, o) {
  const a = e[t];
  if (a == null || !Gb(a)) {
    const s = Yb(a);
    return new RangeError(`Invalid ${o} \`${t}\` of type \`${s}\` supplied to \`${n}\`, expected \`integer\`.`);
  }
  return null;
}
function Ap(e, t, ...n) {
  return e[t] === void 0 ? null : jp(e, t, ...n);
}
function Vi() {
  return null;
}
Ap.isRequired = jp;
Vi.isRequired = Vi;
const Fp = process.env.NODE_ENV === "production" ? Vi : Ap;
function xl(e, t) {
  const n = b({}, t);
  return Object.keys(e).forEach((o) => {
    if (o.toString().match(/^(components|slots)$/))
      n[o] = b({}, e[o], n[o]);
    else if (o.toString().match(/^(componentsProps|slotProps)$/)) {
      const a = e[o] || {}, s = t[o];
      n[o] = {}, !s || !Object.keys(s) ? n[o] = a : !a || !Object.keys(a) ? n[o] = s : (n[o] = b({}, s), Object.keys(a).forEach((i) => {
        n[o][i] = xl(a[i], s[i]);
      }));
    } else n[o] === void 0 && (n[o] = e[o]);
  }), n;
}
function Se(e, t, n = void 0) {
  const o = {};
  return Object.keys(e).forEach(
    // `Object.keys(slots)` can't be wider than `T` because we infer `T` from `slots`.
    // @ts-expect-error https://github.com/microsoft/TypeScript/pull/12253#issuecomment-263132208
    (a) => {
      o[a] = e[a].reduce((s, i) => {
        if (i) {
          const l = t(i);
          l !== "" && s.push(l), n && n[i] && s.push(n[i]);
        }
        return s;
      }, []).join(" ");
    }
  ), o;
}
const au = (e) => e, Xb = () => {
  let e = au;
  return {
    configure(t) {
      e = t;
    },
    generate(t) {
      return e(t);
    },
    reset() {
      e = au;
    }
  };
}, Tl = Xb(), Vp = {
  active: "active",
  checked: "checked",
  completed: "completed",
  disabled: "disabled",
  error: "error",
  expanded: "expanded",
  focused: "focused",
  focusVisible: "focusVisible",
  open: "open",
  readOnly: "readOnly",
  required: "required",
  selected: "selected"
};
function Pe(e, t, n = "Mui") {
  const o = Vp[t];
  return o ? `${n}-${o}` : `${Tl.generate(e)}-${t}`;
}
function Ce(e, t, n = "Mui") {
  const o = {};
  return t.forEach((a) => {
    o[a] = Pe(e, a, n);
  }), o;
}
function Lp(e, t = Number.MIN_SAFE_INTEGER, n = Number.MAX_SAFE_INTEGER) {
  return Math.max(t, Math.min(e, n));
}
const Zb = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  default: Lp
}, Symbol.toStringTag, { value: "Module" }));
function ie(e, t) {
  if (e == null) return {};
  var n = {};
  for (var o in e) if ({}.hasOwnProperty.call(e, o)) {
    if (t.indexOf(o) >= 0) continue;
    n[o] = e[o];
  }
  return n;
}
function Bp(e) {
  var t, n, o = "";
  if (typeof e == "string" || typeof e == "number") o += e;
  else if (typeof e == "object") if (Array.isArray(e)) {
    var a = e.length;
    for (t = 0; t < a; t++) e[t] && (n = Bp(e[t])) && (o && (o += " "), o += n);
  } else for (n in e) e[n] && (o && (o += " "), o += n);
  return o;
}
function pe() {
  for (var e, t, n = 0, o = "", a = arguments.length; n < a; n++) (e = arguments[n]) && (t = Bp(e)) && (o && (o += " "), o += t);
  return o;
}
function zp(e) {
  const {
    theme: t,
    name: n,
    props: o
  } = e;
  return !t || !t.components || !t.components[n] || !t.components[n].defaultProps ? o : xl(t.components[n].defaultProps, o);
}
const Jb = ["values", "unit", "step"], Qb = (e) => {
  const t = Object.keys(e).map((n) => ({
    key: n,
    val: e[n]
  })) || [];
  return t.sort((n, o) => n.val - o.val), t.reduce((n, o) => b({}, n, {
    [o.key]: o.val
  }), {});
};
function Wp(e) {
  const {
    // The breakpoint **start** at this value.
    // For instance with the first breakpoint xs: [xs, sm).
    values: t = {
      xs: 0,
      // phone
      sm: 600,
      // tablet
      md: 900,
      // small laptop
      lg: 1200,
      // desktop
      xl: 1536
      // large screen
    },
    unit: n = "px",
    step: o = 5
  } = e, a = ie(e, Jb), s = Qb(t), i = Object.keys(s);
  function l(p) {
    return `@media (min-width:${typeof t[p] == "number" ? t[p] : p}${n})`;
  }
  function c(p) {
    return `@media (max-width:${(typeof t[p] == "number" ? t[p] : p) - o / 100}${n})`;
  }
  function u(p, m) {
    const v = i.indexOf(m);
    return `@media (min-width:${typeof t[p] == "number" ? t[p] : p}${n}) and (max-width:${(v !== -1 && typeof t[i[v]] == "number" ? t[i[v]] : m) - o / 100}${n})`;
  }
  function d(p) {
    return i.indexOf(p) + 1 < i.length ? u(p, i[i.indexOf(p) + 1]) : l(p);
  }
  function f(p) {
    const m = i.indexOf(p);
    return m === 0 ? l(i[1]) : m === i.length - 1 ? c(i[m]) : u(p, i[i.indexOf(p) + 1]).replace("@media", "@media not all and");
  }
  return b({
    keys: i,
    values: s,
    up: l,
    down: c,
    between: u,
    only: d,
    not: f,
    unit: n
  }, a);
}
const eg = {
  borderRadius: 4
}, zn = process.env.NODE_ENV !== "production" ? r.oneOfType([r.number, r.string, r.object, r.array]) : {};
function Mo(e, t) {
  return t ? kt(e, t, {
    clone: !1
    // No need to clone deep, it's way faster.
  }) : e;
}
const wl = {
  xs: 0,
  // phone
  sm: 600,
  // tablet
  md: 900,
  // small laptop
  lg: 1200,
  // desktop
  xl: 1536
  // large screen
}, su = {
  // Sorted ASC by size. That's important.
  // It can't be configured as it's used statically for propTypes.
  keys: ["xs", "sm", "md", "lg", "xl"],
  up: (e) => `@media (min-width:${wl[e]}px)`
};
function wn(e, t, n) {
  const o = e.theme || {};
  if (Array.isArray(t)) {
    const s = o.breakpoints || su;
    return t.reduce((i, l, c) => (i[s.up(s.keys[c])] = n(t[c]), i), {});
  }
  if (typeof t == "object") {
    const s = o.breakpoints || su;
    return Object.keys(t).reduce((i, l) => {
      if (Object.keys(s.values || wl).indexOf(l) !== -1) {
        const c = s.up(l);
        i[c] = n(t[l], l);
      } else {
        const c = l;
        i[c] = t[c];
      }
      return i;
    }, {});
  }
  return n(t);
}
function tg(e = {}) {
  var t;
  return ((t = e.keys) == null ? void 0 : t.reduce((o, a) => {
    const s = e.up(a);
    return o[s] = {}, o;
  }, {})) || {};
}
function ng(e, t) {
  return e.reduce((n, o) => {
    const a = n[o];
    return (!a || Object.keys(a).length === 0) && delete n[o], n;
  }, t);
}
function gs(e, t, n = !0) {
  if (!t || typeof t != "string")
    return null;
  if (e && e.vars && n) {
    const o = `vars.${t}`.split(".").reduce((a, s) => a && a[s] ? a[s] : null, e);
    if (o != null)
      return o;
  }
  return t.split(".").reduce((o, a) => o && o[a] != null ? o[a] : null, e);
}
function Ga(e, t, n, o = n) {
  let a;
  return typeof e == "function" ? a = e(n) : Array.isArray(e) ? a = e[n] || o : a = gs(e, n) || o, t && (a = t(a, o, e)), a;
}
function it(e) {
  const {
    prop: t,
    cssProperty: n = e.prop,
    themeKey: o,
    transform: a
  } = e, s = (i) => {
    if (i[t] == null)
      return null;
    const l = i[t], c = i.theme, u = gs(c, o) || {};
    return wn(i, l, (f) => {
      let p = Ga(u, a, f);
      return f === p && typeof f == "string" && (p = Ga(u, a, `${t}${f === "default" ? "" : de(f)}`, f)), n === !1 ? p : {
        [n]: p
      };
    });
  };
  return s.propTypes = process.env.NODE_ENV !== "production" ? {
    [t]: zn
  } : {}, s.filterProps = [t], s;
}
function rg(e) {
  const t = {};
  return (n) => (t[n] === void 0 && (t[n] = e(n)), t[n]);
}
const og = {
  m: "margin",
  p: "padding"
}, ag = {
  t: "Top",
  r: "Right",
  b: "Bottom",
  l: "Left",
  x: ["Left", "Right"],
  y: ["Top", "Bottom"]
}, iu = {
  marginX: "mx",
  marginY: "my",
  paddingX: "px",
  paddingY: "py"
}, sg = rg((e) => {
  if (e.length > 2)
    if (iu[e])
      e = iu[e];
    else
      return [e];
  const [t, n] = e.split(""), o = og[t], a = ag[n] || "";
  return Array.isArray(a) ? a.map((s) => o + s) : [o + a];
}), ys = ["m", "mt", "mr", "mb", "ml", "mx", "my", "margin", "marginTop", "marginRight", "marginBottom", "marginLeft", "marginX", "marginY", "marginInline", "marginInlineStart", "marginInlineEnd", "marginBlock", "marginBlockStart", "marginBlockEnd"], vs = ["p", "pt", "pr", "pb", "pl", "px", "py", "padding", "paddingTop", "paddingRight", "paddingBottom", "paddingLeft", "paddingX", "paddingY", "paddingInline", "paddingInlineStart", "paddingInlineEnd", "paddingBlock", "paddingBlockStart", "paddingBlockEnd"], ig = [...ys, ...vs];
function oa(e, t, n, o) {
  var a;
  const s = (a = gs(e, t, !1)) != null ? a : n;
  return typeof s == "number" ? (i) => typeof i == "string" ? i : (process.env.NODE_ENV !== "production" && typeof i != "number" && console.error(`MUI: Expected ${o} argument to be a number or a string, got ${i}.`), s * i) : Array.isArray(s) ? (i) => typeof i == "string" ? i : (process.env.NODE_ENV !== "production" && (Number.isInteger(i) ? i > s.length - 1 && console.error([`MUI: The value provided (${i}) overflows.`, `The supported values are: ${JSON.stringify(s)}.`, `${i} > ${s.length - 1}, you need to add the missing values.`].join(`
`)) : console.error([`MUI: The \`theme.${t}\` array type cannot be combined with non integer values.You should either use an integer value that can be used as index, or define the \`theme.${t}\` as a number.`].join(`
`))), s[i]) : typeof s == "function" ? s : (process.env.NODE_ENV !== "production" && console.error([`MUI: The \`theme.${t}\` value (${s}) is invalid.`, "It should be a number, an array or a function."].join(`
`)), () => {
  });
}
function Up(e) {
  return oa(e, "spacing", 8, "spacing");
}
function aa(e, t) {
  if (typeof t == "string" || t == null)
    return t;
  const n = Math.abs(t), o = e(n);
  return t >= 0 ? o : typeof o == "number" ? -o : `-${o}`;
}
function lg(e, t) {
  return (n) => e.reduce((o, a) => (o[a] = aa(t, n), o), {});
}
function cg(e, t, n, o) {
  if (t.indexOf(n) === -1)
    return null;
  const a = sg(n), s = lg(a, o), i = e[n];
  return wn(e, i, s);
}
function Hp(e, t) {
  const n = Up(e.theme);
  return Object.keys(e).map((o) => cg(e, t, o, n)).reduce(Mo, {});
}
function ot(e) {
  return Hp(e, ys);
}
ot.propTypes = process.env.NODE_ENV !== "production" ? ys.reduce((e, t) => (e[t] = zn, e), {}) : {};
ot.filterProps = ys;
function at(e) {
  return Hp(e, vs);
}
at.propTypes = process.env.NODE_ENV !== "production" ? vs.reduce((e, t) => (e[t] = zn, e), {}) : {};
at.filterProps = vs;
process.env.NODE_ENV !== "production" && ig.reduce((e, t) => (e[t] = zn, e), {});
function ug(e = 8) {
  if (e.mui)
    return e;
  const t = Up({
    spacing: e
  }), n = (...o) => (process.env.NODE_ENV !== "production" && (o.length <= 4 || console.error(`MUI: Too many arguments provided, expected between 0 and 4, got ${o.length}`)), (o.length === 0 ? [1] : o).map((s) => {
    const i = t(s);
    return typeof i == "number" ? `${i}px` : i;
  }).join(" "));
  return n.mui = !0, n;
}
function xs(...e) {
  const t = e.reduce((o, a) => (a.filterProps.forEach((s) => {
    o[s] = a;
  }), o), {}), n = (o) => Object.keys(o).reduce((a, s) => t[s] ? Mo(a, t[s](o)) : a, {});
  return n.propTypes = process.env.NODE_ENV !== "production" ? e.reduce((o, a) => Object.assign(o, a.propTypes), {}) : {}, n.filterProps = e.reduce((o, a) => o.concat(a.filterProps), []), n;
}
function Wt(e) {
  return typeof e != "number" ? e : `${e}px solid`;
}
function Xt(e, t) {
  return it({
    prop: e,
    themeKey: "borders",
    transform: t
  });
}
const dg = Xt("border", Wt), pg = Xt("borderTop", Wt), fg = Xt("borderRight", Wt), mg = Xt("borderBottom", Wt), hg = Xt("borderLeft", Wt), bg = Xt("borderColor"), gg = Xt("borderTopColor"), yg = Xt("borderRightColor"), vg = Xt("borderBottomColor"), xg = Xt("borderLeftColor"), Tg = Xt("outline", Wt), wg = Xt("outlineColor"), Ts = (e) => {
  if (e.borderRadius !== void 0 && e.borderRadius !== null) {
    const t = oa(e.theme, "shape.borderRadius", 4, "borderRadius"), n = (o) => ({
      borderRadius: aa(t, o)
    });
    return wn(e, e.borderRadius, n);
  }
  return null;
};
Ts.propTypes = process.env.NODE_ENV !== "production" ? {
  borderRadius: zn
} : {};
Ts.filterProps = ["borderRadius"];
xs(dg, pg, fg, mg, hg, bg, gg, yg, vg, xg, Ts, Tg, wg);
const ws = (e) => {
  if (e.gap !== void 0 && e.gap !== null) {
    const t = oa(e.theme, "spacing", 8, "gap"), n = (o) => ({
      gap: aa(t, o)
    });
    return wn(e, e.gap, n);
  }
  return null;
};
ws.propTypes = process.env.NODE_ENV !== "production" ? {
  gap: zn
} : {};
ws.filterProps = ["gap"];
const Es = (e) => {
  if (e.columnGap !== void 0 && e.columnGap !== null) {
    const t = oa(e.theme, "spacing", 8, "columnGap"), n = (o) => ({
      columnGap: aa(t, o)
    });
    return wn(e, e.columnGap, n);
  }
  return null;
};
Es.propTypes = process.env.NODE_ENV !== "production" ? {
  columnGap: zn
} : {};
Es.filterProps = ["columnGap"];
const Cs = (e) => {
  if (e.rowGap !== void 0 && e.rowGap !== null) {
    const t = oa(e.theme, "spacing", 8, "rowGap"), n = (o) => ({
      rowGap: aa(t, o)
    });
    return wn(e, e.rowGap, n);
  }
  return null;
};
Cs.propTypes = process.env.NODE_ENV !== "production" ? {
  rowGap: zn
} : {};
Cs.filterProps = ["rowGap"];
const Eg = it({
  prop: "gridColumn"
}), Cg = it({
  prop: "gridRow"
}), Og = it({
  prop: "gridAutoFlow"
}), Sg = it({
  prop: "gridAutoColumns"
}), Pg = it({
  prop: "gridAutoRows"
}), Rg = it({
  prop: "gridTemplateColumns"
}), Dg = it({
  prop: "gridTemplateRows"
}), $g = it({
  prop: "gridTemplateAreas"
}), kg = it({
  prop: "gridArea"
});
xs(ws, Es, Cs, Eg, Cg, Og, Sg, Pg, Rg, Dg, $g, kg);
function zr(e, t) {
  return t === "grey" ? t : e;
}
const _g = it({
  prop: "color",
  themeKey: "palette",
  transform: zr
}), Mg = it({
  prop: "bgcolor",
  cssProperty: "backgroundColor",
  themeKey: "palette",
  transform: zr
}), Ig = it({
  prop: "backgroundColor",
  themeKey: "palette",
  transform: zr
});
xs(_g, Mg, Ig);
function $t(e) {
  return e <= 1 && e !== 0 ? `${e * 100}%` : e;
}
const Ng = it({
  prop: "width",
  transform: $t
}), El = (e) => {
  if (e.maxWidth !== void 0 && e.maxWidth !== null) {
    const t = (n) => {
      var o, a;
      const s = ((o = e.theme) == null || (o = o.breakpoints) == null || (o = o.values) == null ? void 0 : o[n]) || wl[n];
      return s ? ((a = e.theme) == null || (a = a.breakpoints) == null ? void 0 : a.unit) !== "px" ? {
        maxWidth: `${s}${e.theme.breakpoints.unit}`
      } : {
        maxWidth: s
      } : {
        maxWidth: $t(n)
      };
    };
    return wn(e, e.maxWidth, t);
  }
  return null;
};
El.filterProps = ["maxWidth"];
const jg = it({
  prop: "minWidth",
  transform: $t
}), Ag = it({
  prop: "height",
  transform: $t
}), Fg = it({
  prop: "maxHeight",
  transform: $t
}), Vg = it({
  prop: "minHeight",
  transform: $t
});
it({
  prop: "size",
  cssProperty: "width",
  transform: $t
});
it({
  prop: "size",
  cssProperty: "height",
  transform: $t
});
const Lg = it({
  prop: "boxSizing"
});
xs(Ng, El, jg, Ag, Fg, Vg, Lg);
const sa = {
  // borders
  border: {
    themeKey: "borders",
    transform: Wt
  },
  borderTop: {
    themeKey: "borders",
    transform: Wt
  },
  borderRight: {
    themeKey: "borders",
    transform: Wt
  },
  borderBottom: {
    themeKey: "borders",
    transform: Wt
  },
  borderLeft: {
    themeKey: "borders",
    transform: Wt
  },
  borderColor: {
    themeKey: "palette"
  },
  borderTopColor: {
    themeKey: "palette"
  },
  borderRightColor: {
    themeKey: "palette"
  },
  borderBottomColor: {
    themeKey: "palette"
  },
  borderLeftColor: {
    themeKey: "palette"
  },
  outline: {
    themeKey: "borders",
    transform: Wt
  },
  outlineColor: {
    themeKey: "palette"
  },
  borderRadius: {
    themeKey: "shape.borderRadius",
    style: Ts
  },
  // palette
  color: {
    themeKey: "palette",
    transform: zr
  },
  bgcolor: {
    themeKey: "palette",
    cssProperty: "backgroundColor",
    transform: zr
  },
  backgroundColor: {
    themeKey: "palette",
    transform: zr
  },
  // spacing
  p: {
    style: at
  },
  pt: {
    style: at
  },
  pr: {
    style: at
  },
  pb: {
    style: at
  },
  pl: {
    style: at
  },
  px: {
    style: at
  },
  py: {
    style: at
  },
  padding: {
    style: at
  },
  paddingTop: {
    style: at
  },
  paddingRight: {
    style: at
  },
  paddingBottom: {
    style: at
  },
  paddingLeft: {
    style: at
  },
  paddingX: {
    style: at
  },
  paddingY: {
    style: at
  },
  paddingInline: {
    style: at
  },
  paddingInlineStart: {
    style: at
  },
  paddingInlineEnd: {
    style: at
  },
  paddingBlock: {
    style: at
  },
  paddingBlockStart: {
    style: at
  },
  paddingBlockEnd: {
    style: at
  },
  m: {
    style: ot
  },
  mt: {
    style: ot
  },
  mr: {
    style: ot
  },
  mb: {
    style: ot
  },
  ml: {
    style: ot
  },
  mx: {
    style: ot
  },
  my: {
    style: ot
  },
  margin: {
    style: ot
  },
  marginTop: {
    style: ot
  },
  marginRight: {
    style: ot
  },
  marginBottom: {
    style: ot
  },
  marginLeft: {
    style: ot
  },
  marginX: {
    style: ot
  },
  marginY: {
    style: ot
  },
  marginInline: {
    style: ot
  },
  marginInlineStart: {
    style: ot
  },
  marginInlineEnd: {
    style: ot
  },
  marginBlock: {
    style: ot
  },
  marginBlockStart: {
    style: ot
  },
  marginBlockEnd: {
    style: ot
  },
  // display
  displayPrint: {
    cssProperty: !1,
    transform: (e) => ({
      "@media print": {
        display: e
      }
    })
  },
  display: {},
  overflow: {},
  textOverflow: {},
  visibility: {},
  whiteSpace: {},
  // flexbox
  flexBasis: {},
  flexDirection: {},
  flexWrap: {},
  justifyContent: {},
  alignItems: {},
  alignContent: {},
  order: {},
  flex: {},
  flexGrow: {},
  flexShrink: {},
  alignSelf: {},
  justifyItems: {},
  justifySelf: {},
  // grid
  gap: {
    style: ws
  },
  rowGap: {
    style: Cs
  },
  columnGap: {
    style: Es
  },
  gridColumn: {},
  gridRow: {},
  gridAutoFlow: {},
  gridAutoColumns: {},
  gridAutoRows: {},
  gridTemplateColumns: {},
  gridTemplateRows: {},
  gridTemplateAreas: {},
  gridArea: {},
  // positions
  position: {},
  zIndex: {
    themeKey: "zIndex"
  },
  top: {},
  right: {},
  bottom: {},
  left: {},
  // shadows
  boxShadow: {
    themeKey: "shadows"
  },
  // sizing
  width: {
    transform: $t
  },
  maxWidth: {
    style: El
  },
  minWidth: {
    transform: $t
  },
  height: {
    transform: $t
  },
  maxHeight: {
    transform: $t
  },
  minHeight: {
    transform: $t
  },
  boxSizing: {},
  // typography
  fontFamily: {
    themeKey: "typography"
  },
  fontSize: {
    themeKey: "typography"
  },
  fontStyle: {
    themeKey: "typography"
  },
  fontWeight: {
    themeKey: "typography"
  },
  letterSpacing: {},
  textTransform: {},
  lineHeight: {},
  textAlign: {},
  typography: {
    cssProperty: !1,
    themeKey: "typography"
  }
};
function Bg(...e) {
  const t = e.reduce((o, a) => o.concat(Object.keys(a)), []), n = new Set(t);
  return e.every((o) => n.size === Object.keys(o).length);
}
function zg(e, t) {
  return typeof e == "function" ? e(t) : e;
}
function qp() {
  function e(n, o, a, s) {
    const i = {
      [n]: o,
      theme: a
    }, l = s[n];
    if (!l)
      return {
        [n]: o
      };
    const {
      cssProperty: c = n,
      themeKey: u,
      transform: d,
      style: f
    } = l;
    if (o == null)
      return null;
    if (u === "typography" && o === "inherit")
      return {
        [n]: o
      };
    const p = gs(a, u) || {};
    return f ? f(i) : wn(i, o, (v) => {
      let h = Ga(p, d, v);
      return v === h && typeof v == "string" && (h = Ga(p, d, `${n}${v === "default" ? "" : de(v)}`, v)), c === !1 ? h : {
        [c]: h
      };
    });
  }
  function t(n) {
    var o;
    const {
      sx: a,
      theme: s = {}
    } = n || {};
    if (!a)
      return null;
    const i = (o = s.unstable_sxConfig) != null ? o : sa;
    function l(c) {
      let u = c;
      if (typeof c == "function")
        u = c(s);
      else if (typeof c != "object")
        return c;
      if (!u)
        return null;
      const d = tg(s.breakpoints), f = Object.keys(d);
      let p = d;
      return Object.keys(u).forEach((m) => {
        const v = zg(u[m], s);
        if (v != null)
          if (typeof v == "object")
            if (i[m])
              p = Mo(p, e(m, v, s, i));
            else {
              const h = wn({
                theme: s
              }, v, (y) => ({
                [m]: y
              }));
              Bg(h, v) ? p[m] = t({
                sx: v,
                theme: s
              }) : p = Mo(p, h);
            }
          else
            p = Mo(p, e(m, v, s, i));
      }), ng(f, p);
    }
    return Array.isArray(a) ? a.map(l) : l(a);
  }
  return t;
}
const ia = qp();
ia.filterProps = ["sx"];
function Yp(e, t) {
  const n = this;
  return n.vars && typeof n.getColorSchemeSelector == "function" ? {
    [n.getColorSchemeSelector(e).replace(/(\[[^\]]+\])/, "*:where($1)")]: t
  } : n.palette.mode === e ? t : {};
}
const Wg = ["breakpoints", "palette", "spacing", "shape"];
function Cl(e = {}, ...t) {
  const {
    breakpoints: n = {},
    palette: o = {},
    spacing: a,
    shape: s = {}
  } = e, i = ie(e, Wg), l = Wp(n), c = ug(a);
  let u = kt({
    breakpoints: l,
    direction: "ltr",
    components: {},
    // Inject component definitions.
    palette: b({
      mode: "light"
    }, o),
    spacing: c,
    shape: b({}, eg, s)
  }, i);
  return u.applyStyles = Yp, u = t.reduce((d, f) => kt(d, f), u), u.unstable_sxConfig = b({}, sa, i == null ? void 0 : i.unstable_sxConfig), u.unstable_sx = function(f) {
    return ia({
      sx: f,
      theme: this
    });
  }, u;
}
const Ug = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  default: Cl,
  private_createBreakpoints: Wp,
  unstable_applyStyles: Yp
}, Symbol.toStringTag, { value: "Module" }));
function Kp(e) {
  var t = /* @__PURE__ */ Object.create(null);
  return function(n) {
    return t[n] === void 0 && (t[n] = e(n)), t[n];
  };
}
var Hg = /^((children|dangerouslySetInnerHTML|key|ref|autoFocus|defaultValue|defaultChecked|innerHTML|suppressContentEditableWarning|suppressHydrationWarning|valueLink|abbr|accept|acceptCharset|accessKey|action|allow|allowUserMedia|allowPaymentRequest|allowFullScreen|allowTransparency|alt|async|autoComplete|autoPlay|capture|cellPadding|cellSpacing|challenge|charSet|checked|cite|classID|className|cols|colSpan|content|contentEditable|contextMenu|controls|controlsList|coords|crossOrigin|data|dateTime|decoding|default|defer|dir|disabled|disablePictureInPicture|disableRemotePlayback|download|draggable|encType|enterKeyHint|form|formAction|formEncType|formMethod|formNoValidate|formTarget|frameBorder|headers|height|hidden|high|href|hrefLang|htmlFor|httpEquiv|id|inputMode|integrity|is|keyParams|keyType|kind|label|lang|list|loading|loop|low|marginHeight|marginWidth|max|maxLength|media|mediaGroup|method|min|minLength|multiple|muted|name|nonce|noValidate|open|optimum|pattern|placeholder|playsInline|poster|preload|profile|radioGroup|readOnly|referrerPolicy|rel|required|reversed|role|rows|rowSpan|sandbox|scope|scoped|scrolling|seamless|selected|shape|size|sizes|slot|span|spellCheck|src|srcDoc|srcLang|srcSet|start|step|style|summary|tabIndex|target|title|translate|type|useMap|value|width|wmode|wrap|about|datatype|inlist|prefix|property|resource|typeof|vocab|autoCapitalize|autoCorrect|autoSave|color|incremental|fallback|inert|itemProp|itemScope|itemType|itemID|itemRef|on|option|results|security|unselectable|accentHeight|accumulate|additive|alignmentBaseline|allowReorder|alphabetic|amplitude|arabicForm|ascent|attributeName|attributeType|autoReverse|azimuth|baseFrequency|baselineShift|baseProfile|bbox|begin|bias|by|calcMode|capHeight|clip|clipPathUnits|clipPath|clipRule|colorInterpolation|colorInterpolationFilters|colorProfile|colorRendering|contentScriptType|contentStyleType|cursor|cx|cy|d|decelerate|descent|diffuseConstant|direction|display|divisor|dominantBaseline|dur|dx|dy|edgeMode|elevation|enableBackground|end|exponent|externalResourcesRequired|fill|fillOpacity|fillRule|filter|filterRes|filterUnits|floodColor|floodOpacity|focusable|fontFamily|fontSize|fontSizeAdjust|fontStretch|fontStyle|fontVariant|fontWeight|format|from|fr|fx|fy|g1|g2|glyphName|glyphOrientationHorizontal|glyphOrientationVertical|glyphRef|gradientTransform|gradientUnits|hanging|horizAdvX|horizOriginX|ideographic|imageRendering|in|in2|intercept|k|k1|k2|k3|k4|kernelMatrix|kernelUnitLength|kerning|keyPoints|keySplines|keyTimes|lengthAdjust|letterSpacing|lightingColor|limitingConeAngle|local|markerEnd|markerMid|markerStart|markerHeight|markerUnits|markerWidth|mask|maskContentUnits|maskUnits|mathematical|mode|numOctaves|offset|opacity|operator|order|orient|orientation|origin|overflow|overlinePosition|overlineThickness|panose1|paintOrder|pathLength|patternContentUnits|patternTransform|patternUnits|pointerEvents|points|pointsAtX|pointsAtY|pointsAtZ|preserveAlpha|preserveAspectRatio|primitiveUnits|r|radius|refX|refY|renderingIntent|repeatCount|repeatDur|requiredExtensions|requiredFeatures|restart|result|rotate|rx|ry|scale|seed|shapeRendering|slope|spacing|specularConstant|specularExponent|speed|spreadMethod|startOffset|stdDeviation|stemh|stemv|stitchTiles|stopColor|stopOpacity|strikethroughPosition|strikethroughThickness|string|stroke|strokeDasharray|strokeDashoffset|strokeLinecap|strokeLinejoin|strokeMiterlimit|strokeOpacity|strokeWidth|surfaceScale|systemLanguage|tableValues|targetX|targetY|textAnchor|textDecoration|textRendering|textLength|to|transform|u1|u2|underlinePosition|underlineThickness|unicode|unicodeBidi|unicodeRange|unitsPerEm|vAlphabetic|vHanging|vIdeographic|vMathematical|values|vectorEffect|version|vertAdvY|vertOriginX|vertOriginY|viewBox|viewTarget|visibility|widths|wordSpacing|writingMode|x|xHeight|x1|x2|xChannelSelector|xlinkActuate|xlinkArcrole|xlinkHref|xlinkRole|xlinkShow|xlinkTitle|xlinkType|xmlBase|xmlns|xmlnsXlink|xmlLang|xmlSpace|y|y1|y2|yChannelSelector|z|zoomAndPan|for|class|autofocus)|(([Dd][Aa][Tt][Aa]|[Aa][Rr][Ii][Aa]|x)-.*))$/, qg = /* @__PURE__ */ Kp(
  function(e) {
    return Hg.test(e) || e.charCodeAt(0) === 111 && e.charCodeAt(1) === 110 && e.charCodeAt(2) < 91;
  }
  /* Z+1 */
);
function Yg(e) {
  if (e.sheet)
    return e.sheet;
  for (var t = 0; t < document.styleSheets.length; t++)
    if (document.styleSheets[t].ownerNode === e)
      return document.styleSheets[t];
}
function Kg(e) {
  var t = document.createElement("style");
  return t.setAttribute("data-emotion", e.key), e.nonce !== void 0 && t.setAttribute("nonce", e.nonce), t.appendChild(document.createTextNode("")), t.setAttribute("data-s", ""), t;
}
var Gg = /* @__PURE__ */ function() {
  function e(n) {
    var o = this;
    this._insertTag = function(a) {
      var s;
      o.tags.length === 0 ? o.insertionPoint ? s = o.insertionPoint.nextSibling : o.prepend ? s = o.container.firstChild : s = o.before : s = o.tags[o.tags.length - 1].nextSibling, o.container.insertBefore(a, s), o.tags.push(a);
    }, this.isSpeedy = n.speedy === void 0 ? process.env.NODE_ENV === "production" : n.speedy, this.tags = [], this.ctr = 0, this.nonce = n.nonce, this.key = n.key, this.container = n.container, this.prepend = n.prepend, this.insertionPoint = n.insertionPoint, this.before = null;
  }
  var t = e.prototype;
  return t.hydrate = function(o) {
    o.forEach(this._insertTag);
  }, t.insert = function(o) {
    this.ctr % (this.isSpeedy ? 65e3 : 1) === 0 && this._insertTag(Kg(this));
    var a = this.tags[this.tags.length - 1];
    if (process.env.NODE_ENV !== "production") {
      var s = o.charCodeAt(0) === 64 && o.charCodeAt(1) === 105;
      s && this._alreadyInsertedOrderInsensitiveRule && console.error(`You're attempting to insert the following rule:
` + o + "\n\n`@import` rules must be before all other types of rules in a stylesheet but other rules have already been inserted. Please ensure that `@import` rules are before all other rules."), this._alreadyInsertedOrderInsensitiveRule = this._alreadyInsertedOrderInsensitiveRule || !s;
    }
    if (this.isSpeedy) {
      var i = Yg(a);
      try {
        i.insertRule(o, i.cssRules.length);
      } catch (l) {
        process.env.NODE_ENV !== "production" && !/:(-moz-placeholder|-moz-focus-inner|-moz-focusring|-ms-input-placeholder|-moz-read-write|-moz-read-only|-ms-clear|-ms-expand|-ms-reveal){/.test(o) && console.error('There was a problem inserting the following rule: "' + o + '"', l);
      }
    } else
      a.appendChild(document.createTextNode(o));
    this.ctr++;
  }, t.flush = function() {
    this.tags.forEach(function(o) {
      return o.parentNode && o.parentNode.removeChild(o);
    }), this.tags = [], this.ctr = 0, process.env.NODE_ENV !== "production" && (this._alreadyInsertedOrderInsensitiveRule = !1);
  }, e;
}(), ht = "-ms-", Xa = "-moz-", Fe = "-webkit-", Ol = "comm", Sl = "rule", Pl = "decl", Xg = "@import", Gp = "@keyframes", Zg = "@layer", Jg = Math.abs, Os = String.fromCharCode, Qg = Object.assign;
function ey(e, t) {
  return mt(e, 0) ^ 45 ? (((t << 2 ^ mt(e, 0)) << 2 ^ mt(e, 1)) << 2 ^ mt(e, 2)) << 2 ^ mt(e, 3) : 0;
}
function Xp(e) {
  return e.trim();
}
function ty(e, t) {
  return (e = t.exec(e)) ? e[0] : e;
}
function Ve(e, t, n) {
  return e.replace(t, n);
}
function Li(e, t) {
  return e.indexOf(t);
}
function mt(e, t) {
  return e.charCodeAt(t) | 0;
}
function Wo(e, t, n) {
  return e.slice(t, n);
}
function ln(e) {
  return e.length;
}
function Rl(e) {
  return e.length;
}
function Ca(e, t) {
  return t.push(e), e;
}
function ny(e, t) {
  return e.map(t).join("");
}
var Ss = 1, Kr = 1, Zp = 0, Pt = 0, ut = 0, so = "";
function Ps(e, t, n, o, a, s, i) {
  return { value: e, root: t, parent: n, type: o, props: a, children: s, line: Ss, column: Kr, length: i, return: "" };
}
function xo(e, t) {
  return Qg(Ps("", null, null, "", null, null, 0), e, { length: -e.length }, t);
}
function ry() {
  return ut;
}
function oy() {
  return ut = Pt > 0 ? mt(so, --Pt) : 0, Kr--, ut === 10 && (Kr = 1, Ss--), ut;
}
function _t() {
  return ut = Pt < Zp ? mt(so, Pt++) : 0, Kr++, ut === 10 && (Kr = 1, Ss++), ut;
}
function un() {
  return mt(so, Pt);
}
function Va() {
  return Pt;
}
function la(e, t) {
  return Wo(so, e, t);
}
function Uo(e) {
  switch (e) {
    case 0:
    case 9:
    case 10:
    case 13:
    case 32:
      return 5;
    case 33:
    case 43:
    case 44:
    case 47:
    case 62:
    case 64:
    case 126:
    case 59:
    case 123:
    case 125:
      return 4;
    case 58:
      return 3;
    case 34:
    case 39:
    case 40:
    case 91:
      return 2;
    case 41:
    case 93:
      return 1;
  }
  return 0;
}
function Jp(e) {
  return Ss = Kr = 1, Zp = ln(so = e), Pt = 0, [];
}
function Qp(e) {
  return so = "", e;
}
function La(e) {
  return Xp(la(Pt - 1, Bi(e === 91 ? e + 2 : e === 40 ? e + 1 : e)));
}
function ay(e) {
  for (; (ut = un()) && ut < 33; )
    _t();
  return Uo(e) > 2 || Uo(ut) > 3 ? "" : " ";
}
function sy(e, t) {
  for (; --t && _t() && !(ut < 48 || ut > 102 || ut > 57 && ut < 65 || ut > 70 && ut < 97); )
    ;
  return la(e, Va() + (t < 6 && un() == 32 && _t() == 32));
}
function Bi(e) {
  for (; _t(); )
    switch (ut) {
      case e:
        return Pt;
      case 34:
      case 39:
        e !== 34 && e !== 39 && Bi(ut);
        break;
      case 40:
        e === 41 && Bi(e);
        break;
      case 92:
        _t();
        break;
    }
  return Pt;
}
function iy(e, t) {
  for (; _t() && e + ut !== 57; )
    if (e + ut === 84 && un() === 47)
      break;
  return "/*" + la(t, Pt - 1) + "*" + Os(e === 47 ? e : _t());
}
function ly(e) {
  for (; !Uo(un()); )
    _t();
  return la(e, Pt);
}
function cy(e) {
  return Qp(Ba("", null, null, null, [""], e = Jp(e), 0, [0], e));
}
function Ba(e, t, n, o, a, s, i, l, c) {
  for (var u = 0, d = 0, f = i, p = 0, m = 0, v = 0, h = 1, y = 1, w = 1, C = 0, E = "", O = a, T = s, P = o, S = E; y; )
    switch (v = C, C = _t()) {
      case 40:
        if (v != 108 && mt(S, f - 1) == 58) {
          Li(S += Ve(La(C), "&", "&\f"), "&\f") != -1 && (w = -1);
          break;
        }
      case 34:
      case 39:
      case 91:
        S += La(C);
        break;
      case 9:
      case 10:
      case 13:
      case 32:
        S += ay(v);
        break;
      case 92:
        S += sy(Va() - 1, 7);
        continue;
      case 47:
        switch (un()) {
          case 42:
          case 47:
            Ca(uy(iy(_t(), Va()), t, n), c);
            break;
          default:
            S += "/";
        }
        break;
      case 123 * h:
        l[u++] = ln(S) * w;
      case 125 * h:
      case 59:
      case 0:
        switch (C) {
          case 0:
          case 125:
            y = 0;
          case 59 + d:
            w == -1 && (S = Ve(S, /\f/g, "")), m > 0 && ln(S) - f && Ca(m > 32 ? cu(S + ";", o, n, f - 1) : cu(Ve(S, " ", "") + ";", o, n, f - 2), c);
            break;
          case 59:
            S += ";";
          default:
            if (Ca(P = lu(S, t, n, u, d, a, l, E, O = [], T = [], f), s), C === 123)
              if (d === 0)
                Ba(S, t, P, P, O, s, f, l, T);
              else
                switch (p === 99 && mt(S, 3) === 110 ? 100 : p) {
                  case 100:
                  case 108:
                  case 109:
                  case 115:
                    Ba(e, P, P, o && Ca(lu(e, P, P, 0, 0, a, l, E, a, O = [], f), T), a, T, f, l, o ? O : T);
                    break;
                  default:
                    Ba(S, P, P, P, [""], T, 0, l, T);
                }
        }
        u = d = m = 0, h = w = 1, E = S = "", f = i;
        break;
      case 58:
        f = 1 + ln(S), m = v;
      default:
        if (h < 1) {
          if (C == 123)
            --h;
          else if (C == 125 && h++ == 0 && oy() == 125)
            continue;
        }
        switch (S += Os(C), C * h) {
          case 38:
            w = d > 0 ? 1 : (S += "\f", -1);
            break;
          case 44:
            l[u++] = (ln(S) - 1) * w, w = 1;
            break;
          case 64:
            un() === 45 && (S += La(_t())), p = un(), d = f = ln(E = S += ly(Va())), C++;
            break;
          case 45:
            v === 45 && ln(S) == 2 && (h = 0);
        }
    }
  return s;
}
function lu(e, t, n, o, a, s, i, l, c, u, d) {
  for (var f = a - 1, p = a === 0 ? s : [""], m = Rl(p), v = 0, h = 0, y = 0; v < o; ++v)
    for (var w = 0, C = Wo(e, f + 1, f = Jg(h = i[v])), E = e; w < m; ++w)
      (E = Xp(h > 0 ? p[w] + " " + C : Ve(C, /&\f/g, p[w]))) && (c[y++] = E);
  return Ps(e, t, n, a === 0 ? Sl : l, c, u, d);
}
function uy(e, t, n) {
  return Ps(e, t, n, Ol, Os(ry()), Wo(e, 2, -2), 0);
}
function cu(e, t, n, o) {
  return Ps(e, t, n, Pl, Wo(e, 0, o), Wo(e, o + 1, -1), o);
}
function Wr(e, t) {
  for (var n = "", o = Rl(e), a = 0; a < o; a++)
    n += t(e[a], a, e, t) || "";
  return n;
}
function dy(e, t, n, o) {
  switch (e.type) {
    case Zg:
      if (e.children.length) break;
    case Xg:
    case Pl:
      return e.return = e.return || e.value;
    case Ol:
      return "";
    case Gp:
      return e.return = e.value + "{" + Wr(e.children, o) + "}";
    case Sl:
      e.value = e.props.join(",");
  }
  return ln(n = Wr(e.children, o)) ? e.return = e.value + "{" + n + "}" : "";
}
function py(e) {
  var t = Rl(e);
  return function(n, o, a, s) {
    for (var i = "", l = 0; l < t; l++)
      i += e[l](n, o, a, s) || "";
    return i;
  };
}
function fy(e) {
  return function(t) {
    t.root || (t = t.return) && e(t);
  };
}
var my = function(t, n, o) {
  for (var a = 0, s = 0; a = s, s = un(), a === 38 && s === 12 && (n[o] = 1), !Uo(s); )
    _t();
  return la(t, Pt);
}, hy = function(t, n) {
  var o = -1, a = 44;
  do
    switch (Uo(a)) {
      case 0:
        a === 38 && un() === 12 && (n[o] = 1), t[o] += my(Pt - 1, n, o);
        break;
      case 2:
        t[o] += La(a);
        break;
      case 4:
        if (a === 44) {
          t[++o] = un() === 58 ? "&\f" : "", n[o] = t[o].length;
          break;
        }
      default:
        t[o] += Os(a);
    }
  while (a = _t());
  return t;
}, by = function(t, n) {
  return Qp(hy(Jp(t), n));
}, uu = /* @__PURE__ */ new WeakMap(), gy = function(t) {
  if (!(t.type !== "rule" || !t.parent || // positive .length indicates that this rule contains pseudo
  // negative .length indicates that this rule has been already prefixed
  t.length < 1)) {
    for (var n = t.value, o = t.parent, a = t.column === o.column && t.line === o.line; o.type !== "rule"; )
      if (o = o.parent, !o) return;
    if (!(t.props.length === 1 && n.charCodeAt(0) !== 58 && !uu.get(o)) && !a) {
      uu.set(t, !0);
      for (var s = [], i = by(n, s), l = o.props, c = 0, u = 0; c < i.length; c++)
        for (var d = 0; d < l.length; d++, u++)
          t.props[u] = s[c] ? i[c].replace(/&\f/g, l[d]) : l[d] + " " + i[c];
    }
  }
}, yy = function(t) {
  if (t.type === "decl") {
    var n = t.value;
    // charcode for l
    n.charCodeAt(0) === 108 && // charcode for b
    n.charCodeAt(2) === 98 && (t.return = "", t.value = "");
  }
}, vy = "emotion-disable-server-rendering-unsafe-selector-warning-please-do-not-use-this-the-warning-exists-for-a-reason", xy = function(t) {
  return t.type === "comm" && t.children.indexOf(vy) > -1;
}, Ty = function(t) {
  return function(n, o, a) {
    if (!(n.type !== "rule" || t.compat)) {
      var s = n.value.match(/(:first|:nth|:nth-last)-child/g);
      if (s) {
        for (var i = !!n.parent, l = i ? n.parent.children : (
          // global rule at the root level
          a
        ), c = l.length - 1; c >= 0; c--) {
          var u = l[c];
          if (u.line < n.line)
            break;
          if (u.column < n.column) {
            if (xy(u))
              return;
            break;
          }
        }
        s.forEach(function(d) {
          console.error('The pseudo class "' + d + '" is potentially unsafe when doing server-side rendering. Try changing it to "' + d.split("-child")[0] + '-of-type".');
        });
      }
    }
  };
}, ef = function(t) {
  return t.type.charCodeAt(1) === 105 && t.type.charCodeAt(0) === 64;
}, wy = function(t, n) {
  for (var o = t - 1; o >= 0; o--)
    if (!ef(n[o]))
      return !0;
  return !1;
}, du = function(t) {
  t.type = "", t.value = "", t.return = "", t.children = "", t.props = "";
}, Ey = function(t, n, o) {
  ef(t) && (t.parent ? (console.error("`@import` rules can't be nested inside other rules. Please move it to the top level and put it before regular rules. Keep in mind that they can only be used within global styles."), du(t)) : wy(n, o) && (console.error("`@import` rules can't be after other rules. Please put your `@import` rules before your other rules."), du(t)));
};
function tf(e, t) {
  switch (ey(e, t)) {
    case 5103:
      return Fe + "print-" + e + e;
    case 5737:
    case 4201:
    case 3177:
    case 3433:
    case 1641:
    case 4457:
    case 2921:
    case 5572:
    case 6356:
    case 5844:
    case 3191:
    case 6645:
    case 3005:
    case 6391:
    case 5879:
    case 5623:
    case 6135:
    case 4599:
    case 4855:
    case 4215:
    case 6389:
    case 5109:
    case 5365:
    case 5621:
    case 3829:
      return Fe + e + e;
    case 5349:
    case 4246:
    case 4810:
    case 6968:
    case 2756:
      return Fe + e + Xa + e + ht + e + e;
    case 6828:
    case 4268:
      return Fe + e + ht + e + e;
    case 6165:
      return Fe + e + ht + "flex-" + e + e;
    case 5187:
      return Fe + e + Ve(e, /(\w+).+(:[^]+)/, Fe + "box-$1$2" + ht + "flex-$1$2") + e;
    case 5443:
      return Fe + e + ht + "flex-item-" + Ve(e, /flex-|-self/, "") + e;
    case 4675:
      return Fe + e + ht + "flex-line-pack" + Ve(e, /align-content|flex-|-self/, "") + e;
    case 5548:
      return Fe + e + ht + Ve(e, "shrink", "negative") + e;
    case 5292:
      return Fe + e + ht + Ve(e, "basis", "preferred-size") + e;
    case 6060:
      return Fe + "box-" + Ve(e, "-grow", "") + Fe + e + ht + Ve(e, "grow", "positive") + e;
    case 4554:
      return Fe + Ve(e, /([^-])(transform)/g, "$1" + Fe + "$2") + e;
    case 6187:
      return Ve(Ve(Ve(e, /(zoom-|grab)/, Fe + "$1"), /(image-set)/, Fe + "$1"), e, "") + e;
    case 5495:
    case 3959:
      return Ve(e, /(image-set\([^]*)/, Fe + "$1$`$1");
    case 4968:
      return Ve(Ve(e, /(.+:)(flex-)?(.*)/, Fe + "box-pack:$3" + ht + "flex-pack:$3"), /s.+-b[^;]+/, "justify") + Fe + e + e;
    case 4095:
    case 3583:
    case 4068:
    case 2532:
      return Ve(e, /(.+)-inline(.+)/, Fe + "$1$2") + e;
    case 8116:
    case 7059:
    case 5753:
    case 5535:
    case 5445:
    case 5701:
    case 4933:
    case 4677:
    case 5533:
    case 5789:
    case 5021:
    case 4765:
      if (ln(e) - 1 - t > 6) switch (mt(e, t + 1)) {
        case 109:
          if (mt(e, t + 4) !== 45) break;
        case 102:
          return Ve(e, /(.+:)(.+)-([^]+)/, "$1" + Fe + "$2-$3$1" + Xa + (mt(e, t + 3) == 108 ? "$3" : "$2-$3")) + e;
        case 115:
          return ~Li(e, "stretch") ? tf(Ve(e, "stretch", "fill-available"), t) + e : e;
      }
      break;
    case 4949:
      if (mt(e, t + 1) !== 115) break;
    case 6444:
      switch (mt(e, ln(e) - 3 - (~Li(e, "!important") && 10))) {
        case 107:
          return Ve(e, ":", ":" + Fe) + e;
        case 101:
          return Ve(e, /(.+:)([^;!]+)(;|!.+)?/, "$1" + Fe + (mt(e, 14) === 45 ? "inline-" : "") + "box$3$1" + Fe + "$2$3$1" + ht + "$2box$3") + e;
      }
      break;
    case 5936:
      switch (mt(e, t + 11)) {
        case 114:
          return Fe + e + ht + Ve(e, /[svh]\w+-[tblr]{2}/, "tb") + e;
        case 108:
          return Fe + e + ht + Ve(e, /[svh]\w+-[tblr]{2}/, "tb-rl") + e;
        case 45:
          return Fe + e + ht + Ve(e, /[svh]\w+-[tblr]{2}/, "lr") + e;
      }
      return Fe + e + ht + e + e;
  }
  return e;
}
var Cy = function(t, n, o, a) {
  if (t.length > -1 && !t.return) switch (t.type) {
    case Pl:
      t.return = tf(t.value, t.length);
      break;
    case Gp:
      return Wr([xo(t, {
        value: Ve(t.value, "@", "@" + Fe)
      })], a);
    case Sl:
      if (t.length) return ny(t.props, function(s) {
        switch (ty(s, /(::plac\w+|:read-\w+)/)) {
          case ":read-only":
          case ":read-write":
            return Wr([xo(t, {
              props: [Ve(s, /:(read-\w+)/, ":" + Xa + "$1")]
            })], a);
          case "::placeholder":
            return Wr([xo(t, {
              props: [Ve(s, /:(plac\w+)/, ":" + Fe + "input-$1")]
            }), xo(t, {
              props: [Ve(s, /:(plac\w+)/, ":" + Xa + "$1")]
            }), xo(t, {
              props: [Ve(s, /:(plac\w+)/, ht + "input-$1")]
            })], a);
        }
        return "";
      });
  }
}, Oy = [Cy], nf = function(t) {
  var n = t.key;
  if (process.env.NODE_ENV !== "production" && !n)
    throw new Error(`You have to configure \`key\` for your cache. Please make sure it's unique (and not equal to 'css') as it's used for linking styles to your cache.
If multiple caches share the same key they might "fight" for each other's style elements.`);
  if (n === "css") {
    var o = document.querySelectorAll("style[data-emotion]:not([data-s])");
    Array.prototype.forEach.call(o, function(h) {
      var y = h.getAttribute("data-emotion");
      y.indexOf(" ") !== -1 && (document.head.appendChild(h), h.setAttribute("data-s", ""));
    });
  }
  var a = t.stylisPlugins || Oy;
  if (process.env.NODE_ENV !== "production" && /[^a-z-]/.test(n))
    throw new Error('Emotion key must only contain lower case alphabetical characters and - but "' + n + '" was passed');
  var s = {}, i, l = [];
  i = t.container || document.head, Array.prototype.forEach.call(
    // this means we will ignore elements which don't have a space in them which
    // means that the style elements we're looking at are only Emotion 11 server-rendered style elements
    document.querySelectorAll('style[data-emotion^="' + n + ' "]'),
    function(h) {
      for (var y = h.getAttribute("data-emotion").split(" "), w = 1; w < y.length; w++)
        s[y[w]] = !0;
      l.push(h);
    }
  );
  var c, u = [gy, yy];
  process.env.NODE_ENV !== "production" && u.push(Ty({
    get compat() {
      return v.compat;
    }
  }), Ey);
  {
    var d, f = [dy, process.env.NODE_ENV !== "production" ? function(h) {
      h.root || (h.return ? d.insert(h.return) : h.value && h.type !== Ol && d.insert(h.value + "{}"));
    } : fy(function(h) {
      d.insert(h);
    })], p = py(u.concat(a, f)), m = function(y) {
      return Wr(cy(y), p);
    };
    c = function(y, w, C, E) {
      d = C, process.env.NODE_ENV !== "production" && w.map !== void 0 && (d = {
        insert: function(T) {
          C.insert(T + w.map);
        }
      }), m(y ? y + "{" + w.styles + "}" : w.styles), E && (v.inserted[w.name] = !0);
    };
  }
  var v = {
    key: n,
    sheet: new Gg({
      key: n,
      container: i,
      nonce: t.nonce,
      speedy: t.speedy,
      prepend: t.prepend,
      insertionPoint: t.insertionPoint
    }),
    nonce: t.nonce,
    inserted: s,
    registered: {},
    insert: c
  };
  return v.sheet.hydrate(l), v;
}, rf = bl, Sy = {
  $$typeof: !0,
  render: !0,
  defaultProps: !0,
  displayName: !0,
  propTypes: !0
}, Py = {
  $$typeof: !0,
  compare: !0,
  defaultProps: !0,
  displayName: !0,
  propTypes: !0,
  type: !0
}, of = {};
of[rf.ForwardRef] = Sy;
of[rf.Memo] = Py;
var Ry = !0;
function Dl(e, t, n) {
  var o = "";
  return n.split(" ").forEach(function(a) {
    e[a] !== void 0 ? t.push(e[a] + ";") : o += a + " ";
  }), o;
}
var Rs = function(t, n, o) {
  var a = t.key + "-" + n.name;
  // we only need to add the styles to the registered cache if the
  // class name could be used further down
  // the tree but if it's a string tag, we know it won't
  // so we don't have to add it to registered cache.
  // this improves memory usage since we can avoid storing the whole style string
  (o === !1 || // we need to always store it if we're in compat mode and
  // in node since emotion-server relies on whether a style is in
  // the registered cache to know whether a style is global or not
  // also, note that this check will be dead code eliminated in the browser
  Ry === !1) && t.registered[a] === void 0 && (t.registered[a] = n.styles);
}, Ds = function(t, n, o) {
  Rs(t, n, o);
  var a = t.key + "-" + n.name;
  if (t.inserted[n.name] === void 0) {
    var s = n;
    do
      t.insert(n === s ? "." + a : "", s, t.sheet, !0), s = s.next;
    while (s !== void 0);
  }
};
function Dy(e) {
  for (var t = 0, n, o = 0, a = e.length; a >= 4; ++o, a -= 4)
    n = e.charCodeAt(o) & 255 | (e.charCodeAt(++o) & 255) << 8 | (e.charCodeAt(++o) & 255) << 16 | (e.charCodeAt(++o) & 255) << 24, n = /* Math.imul(k, m): */
    (n & 65535) * 1540483477 + ((n >>> 16) * 59797 << 16), n ^= /* k >>> r: */
    n >>> 24, t = /* Math.imul(k, m): */
    (n & 65535) * 1540483477 + ((n >>> 16) * 59797 << 16) ^ /* Math.imul(h, m): */
    (t & 65535) * 1540483477 + ((t >>> 16) * 59797 << 16);
  switch (a) {
    case 3:
      t ^= (e.charCodeAt(o + 2) & 255) << 16;
    case 2:
      t ^= (e.charCodeAt(o + 1) & 255) << 8;
    case 1:
      t ^= e.charCodeAt(o) & 255, t = /* Math.imul(h, m): */
      (t & 65535) * 1540483477 + ((t >>> 16) * 59797 << 16);
  }
  return t ^= t >>> 13, t = /* Math.imul(h, m): */
  (t & 65535) * 1540483477 + ((t >>> 16) * 59797 << 16), ((t ^ t >>> 15) >>> 0).toString(36);
}
var $y = {
  animationIterationCount: 1,
  aspectRatio: 1,
  borderImageOutset: 1,
  borderImageSlice: 1,
  borderImageWidth: 1,
  boxFlex: 1,
  boxFlexGroup: 1,
  boxOrdinalGroup: 1,
  columnCount: 1,
  columns: 1,
  flex: 1,
  flexGrow: 1,
  flexPositive: 1,
  flexShrink: 1,
  flexNegative: 1,
  flexOrder: 1,
  gridRow: 1,
  gridRowEnd: 1,
  gridRowSpan: 1,
  gridRowStart: 1,
  gridColumn: 1,
  gridColumnEnd: 1,
  gridColumnSpan: 1,
  gridColumnStart: 1,
  msGridRow: 1,
  msGridRowSpan: 1,
  msGridColumn: 1,
  msGridColumnSpan: 1,
  fontWeight: 1,
  lineHeight: 1,
  opacity: 1,
  order: 1,
  orphans: 1,
  tabSize: 1,
  widows: 1,
  zIndex: 1,
  zoom: 1,
  WebkitLineClamp: 1,
  // SVG-related properties
  fillOpacity: 1,
  floodOpacity: 1,
  stopOpacity: 1,
  strokeDasharray: 1,
  strokeDashoffset: 1,
  strokeMiterlimit: 1,
  strokeOpacity: 1,
  strokeWidth: 1
}, pu = `You have illegal escape sequence in your template literal, most likely inside content's property value.
Because you write your CSS inside a JavaScript string you actually have to do double escaping, so for example "content: '\\00d7';" should become "content: '\\\\00d7';".
You can read more about this here:
https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Template_literals#ES2018_revision_of_illegal_escape_sequences`, ky = "You have passed in falsy value as style object's key (can happen when in example you pass unexported component as computed key).", _y = /[A-Z]|^ms/g, af = /_EMO_([^_]+?)_([^]*?)_EMO_/g, $l = function(t) {
  return t.charCodeAt(1) === 45;
}, fu = function(t) {
  return t != null && typeof t != "boolean";
}, hi = /* @__PURE__ */ Kp(function(e) {
  return $l(e) ? e : e.replace(_y, "-$&").toLowerCase();
}), Za = function(t, n) {
  switch (t) {
    case "animation":
    case "animationName":
      if (typeof n == "string")
        return n.replace(af, function(o, a, s) {
          return en = {
            name: a,
            styles: s,
            next: en
          }, a;
        });
  }
  return $y[t] !== 1 && !$l(t) && typeof n == "number" && n !== 0 ? n + "px" : n;
};
if (process.env.NODE_ENV !== "production") {
  var My = /(var|attr|counters?|url|element|(((repeating-)?(linear|radial))|conic)-gradient)\(|(no-)?(open|close)-quote/, Iy = ["normal", "none", "initial", "inherit", "unset"], Ny = Za, jy = /^-ms-/, Ay = /-(.)/g, mu = {};
  Za = function(t, n) {
    if (t === "content" && (typeof n != "string" || Iy.indexOf(n) === -1 && !My.test(n) && (n.charAt(0) !== n.charAt(n.length - 1) || n.charAt(0) !== '"' && n.charAt(0) !== "'")))
      throw new Error("You seem to be using a value for 'content' without quotes, try replacing it with `content: '\"" + n + "\"'`");
    var o = Ny(t, n);
    return o !== "" && !$l(t) && t.indexOf("-") !== -1 && mu[t] === void 0 && (mu[t] = !0, console.error("Using kebab-case for css properties in objects is not supported. Did you mean " + t.replace(jy, "ms-").replace(Ay, function(a, s) {
      return s.toUpperCase();
    }) + "?")), o;
  };
}
var sf = "Component selectors can only be used in conjunction with @emotion/babel-plugin, the swc Emotion plugin, or another Emotion-aware compiler transform.";
function Ho(e, t, n) {
  if (n == null)
    return "";
  if (n.__emotion_styles !== void 0) {
    if (process.env.NODE_ENV !== "production" && n.toString() === "NO_COMPONENT_SELECTOR")
      throw new Error(sf);
    return n;
  }
  switch (typeof n) {
    case "boolean":
      return "";
    case "object": {
      if (n.anim === 1)
        return en = {
          name: n.name,
          styles: n.styles,
          next: en
        }, n.name;
      if (n.styles !== void 0) {
        var o = n.next;
        if (o !== void 0)
          for (; o !== void 0; )
            en = {
              name: o.name,
              styles: o.styles,
              next: en
            }, o = o.next;
        var a = n.styles + ";";
        return process.env.NODE_ENV !== "production" && n.map !== void 0 && (a += n.map), a;
      }
      return Fy(e, t, n);
    }
    case "function": {
      if (e !== void 0) {
        var s = en, i = n(e);
        return en = s, Ho(e, t, i);
      } else process.env.NODE_ENV !== "production" && console.error("Functions that are interpolated in css calls will be stringified.\nIf you want to have a css call based on props, create a function that returns a css call like this\nlet dynamicStyle = (props) => css`color: ${props.color}`\nIt can be called directly with props or interpolated in a styled call like this\nlet SomeComponent = styled('div')`${dynamicStyle}`");
      break;
    }
    case "string":
      if (process.env.NODE_ENV !== "production") {
        var l = [], c = n.replace(af, function(d, f, p) {
          var m = "animation" + l.length;
          return l.push("const " + m + " = keyframes`" + p.replace(/^@keyframes animation-\w+/, "") + "`"), "${" + m + "}";
        });
        l.length && console.error("`keyframes` output got interpolated into plain string, please wrap it with `css`.\n\nInstead of doing this:\n\n" + [].concat(l, ["`" + c + "`"]).join(`
`) + `

You should wrap it with \`css\` like this:

` + ("css`" + c + "`"));
      }
      break;
  }
  if (t == null)
    return n;
  var u = t[n];
  return u !== void 0 ? u : n;
}
function Fy(e, t, n) {
  var o = "";
  if (Array.isArray(n))
    for (var a = 0; a < n.length; a++)
      o += Ho(e, t, n[a]) + ";";
  else
    for (var s in n) {
      var i = n[s];
      if (typeof i != "object")
        t != null && t[i] !== void 0 ? o += s + "{" + t[i] + "}" : fu(i) && (o += hi(s) + ":" + Za(s, i) + ";");
      else {
        if (s === "NO_COMPONENT_SELECTOR" && process.env.NODE_ENV !== "production")
          throw new Error(sf);
        if (Array.isArray(i) && typeof i[0] == "string" && (t == null || t[i[0]] === void 0))
          for (var l = 0; l < i.length; l++)
            fu(i[l]) && (o += hi(s) + ":" + Za(s, i[l]) + ";");
        else {
          var c = Ho(e, t, i);
          switch (s) {
            case "animation":
            case "animationName": {
              o += hi(s) + ":" + c + ";";
              break;
            }
            default:
              process.env.NODE_ENV !== "production" && s === "undefined" && console.error(ky), o += s + "{" + c + "}";
          }
        }
      }
    }
  return o;
}
var hu = /label:\s*([^\s;\n{]+)\s*(;|$)/g, lf;
process.env.NODE_ENV !== "production" && (lf = /\/\*#\ssourceMappingURL=data:application\/json;\S+\s+\*\//g);
var en, Gr = function(t, n, o) {
  if (t.length === 1 && typeof t[0] == "object" && t[0] !== null && t[0].styles !== void 0)
    return t[0];
  var a = !0, s = "";
  en = void 0;
  var i = t[0];
  i == null || i.raw === void 0 ? (a = !1, s += Ho(o, n, i)) : (process.env.NODE_ENV !== "production" && i[0] === void 0 && console.error(pu), s += i[0]);
  for (var l = 1; l < t.length; l++)
    s += Ho(o, n, t[l]), a && (process.env.NODE_ENV !== "production" && i[l] === void 0 && console.error(pu), s += i[l]);
  var c;
  process.env.NODE_ENV !== "production" && (s = s.replace(lf, function(p) {
    return c = p, "";
  })), hu.lastIndex = 0;
  for (var u = "", d; (d = hu.exec(s)) !== null; )
    u += "-" + // $FlowFixMe we know it's not null
    d[1];
  var f = Dy(s) + u;
  return process.env.NODE_ENV !== "production" ? {
    name: f,
    styles: s,
    map: c,
    next: en,
    toString: function() {
      return "You have tried to stringify object returned from `css` function. It isn't supposed to be used directly (e.g. as value of the `className` prop), but rather handed to emotion so it can handle it (e.g. as value of `css` prop).";
    }
  } : {
    name: f,
    styles: s,
    next: en
  };
}, Vy = function(t) {
  return t();
}, cf = g.useInsertionEffect ? g.useInsertionEffect : !1, kl = cf || Vy, bu = cf || g.useLayoutEffect, Ly = {}.hasOwnProperty, _l = /* @__PURE__ */ g.createContext(
  // we're doing this to avoid preconstruct's dead code elimination in this one case
  // because this module is primarily intended for the browser and node
  // but it's also required in react native and similar environments sometimes
  // and we could have a special build just for that
  // but this is much easier and the native packages
  // might use a different theme context in the future anyway
  typeof HTMLElement < "u" ? /* @__PURE__ */ nf({
    key: "css"
  }) : null
);
process.env.NODE_ENV !== "production" && (_l.displayName = "EmotionCacheContext");
var By = _l.Provider, $s = function(t) {
  return /* @__PURE__ */ Op(function(n, o) {
    var a = Sp(_l);
    return t(n, a, o);
  });
}, dr = /* @__PURE__ */ g.createContext({});
process.env.NODE_ENV !== "production" && (dr.displayName = "EmotionThemeContext");
var gu = "__EMOTION_TYPE_PLEASE_DO_NOT_USE__", yu = "__EMOTION_LABEL_PLEASE_DO_NOT_USE__", zy = function(t) {
  var n = t.cache, o = t.serialized, a = t.isStringTag;
  return Rs(n, o, a), kl(function() {
    return Ds(n, o, a);
  }), null;
}, Wy = /* @__PURE__ */ $s(function(e, t, n) {
  var o = e.css;
  typeof o == "string" && t.registered[o] !== void 0 && (o = t.registered[o]);
  var a = e[gu], s = [o], i = "";
  typeof e.className == "string" ? i = Dl(t.registered, s, e.className) : e.className != null && (i = e.className + " ");
  var l = Gr(s, void 0, g.useContext(dr));
  if (process.env.NODE_ENV !== "production" && l.name.indexOf("-") === -1) {
    var c = e[yu];
    c && (l = Gr([l, "label:" + c + ";"]));
  }
  i += t.key + "-" + l.name;
  var u = {};
  for (var d in e)
    Ly.call(e, d) && d !== "css" && d !== gu && (process.env.NODE_ENV === "production" || d !== yu) && (u[d] = e[d]);
  return u.ref = n, u.className = i, /* @__PURE__ */ g.createElement(g.Fragment, null, /* @__PURE__ */ g.createElement(zy, {
    cache: t,
    serialized: l,
    isStringTag: typeof a == "string"
  }), /* @__PURE__ */ g.createElement(a, u));
});
process.env.NODE_ENV !== "production" && (Wy.displayName = "EmotionCssPropInternal");
var Uy = {
  name: "@emotion/react",
  version: "11.11.4",
  main: "dist/emotion-react.cjs.js",
  module: "dist/emotion-react.esm.js",
  browser: {
    "./dist/emotion-react.esm.js": "./dist/emotion-react.browser.esm.js"
  },
  exports: {
    ".": {
      module: {
        worker: "./dist/emotion-react.worker.esm.js",
        browser: "./dist/emotion-react.browser.esm.js",
        default: "./dist/emotion-react.esm.js"
      },
      import: "./dist/emotion-react.cjs.mjs",
      default: "./dist/emotion-react.cjs.js"
    },
    "./jsx-runtime": {
      module: {
        worker: "./jsx-runtime/dist/emotion-react-jsx-runtime.worker.esm.js",
        browser: "./jsx-runtime/dist/emotion-react-jsx-runtime.browser.esm.js",
        default: "./jsx-runtime/dist/emotion-react-jsx-runtime.esm.js"
      },
      import: "./jsx-runtime/dist/emotion-react-jsx-runtime.cjs.mjs",
      default: "./jsx-runtime/dist/emotion-react-jsx-runtime.cjs.js"
    },
    "./_isolated-hnrs": {
      module: {
        worker: "./_isolated-hnrs/dist/emotion-react-_isolated-hnrs.worker.esm.js",
        browser: "./_isolated-hnrs/dist/emotion-react-_isolated-hnrs.browser.esm.js",
        default: "./_isolated-hnrs/dist/emotion-react-_isolated-hnrs.esm.js"
      },
      import: "./_isolated-hnrs/dist/emotion-react-_isolated-hnrs.cjs.mjs",
      default: "./_isolated-hnrs/dist/emotion-react-_isolated-hnrs.cjs.js"
    },
    "./jsx-dev-runtime": {
      module: {
        worker: "./jsx-dev-runtime/dist/emotion-react-jsx-dev-runtime.worker.esm.js",
        browser: "./jsx-dev-runtime/dist/emotion-react-jsx-dev-runtime.browser.esm.js",
        default: "./jsx-dev-runtime/dist/emotion-react-jsx-dev-runtime.esm.js"
      },
      import: "./jsx-dev-runtime/dist/emotion-react-jsx-dev-runtime.cjs.mjs",
      default: "./jsx-dev-runtime/dist/emotion-react-jsx-dev-runtime.cjs.js"
    },
    "./package.json": "./package.json",
    "./types/css-prop": "./types/css-prop.d.ts",
    "./macro": {
      types: {
        import: "./macro.d.mts",
        default: "./macro.d.ts"
      },
      default: "./macro.js"
    }
  },
  types: "types/index.d.ts",
  files: [
    "src",
    "dist",
    "jsx-runtime",
    "jsx-dev-runtime",
    "_isolated-hnrs",
    "types/*.d.ts",
    "macro.*"
  ],
  sideEffects: !1,
  author: "Emotion Contributors",
  license: "MIT",
  scripts: {
    "test:typescript": "dtslint types"
  },
  dependencies: {
    "@babel/runtime": "^7.18.3",
    "@emotion/babel-plugin": "^11.11.0",
    "@emotion/cache": "^11.11.0",
    "@emotion/serialize": "^1.1.3",
    "@emotion/use-insertion-effect-with-fallbacks": "^1.0.1",
    "@emotion/utils": "^1.2.1",
    "@emotion/weak-memoize": "^0.3.1",
    "hoist-non-react-statics": "^3.3.1"
  },
  peerDependencies: {
    react: ">=16.8.0"
  },
  peerDependenciesMeta: {
    "@types/react": {
      optional: !0
    }
  },
  devDependencies: {
    "@definitelytyped/dtslint": "0.0.112",
    "@emotion/css": "11.11.2",
    "@emotion/css-prettifier": "1.1.3",
    "@emotion/server": "11.11.0",
    "@emotion/styled": "11.11.0",
    "html-tag-names": "^1.1.2",
    react: "16.14.0",
    "svg-tag-names": "^1.1.1",
    typescript: "^4.5.5"
  },
  repository: "https://github.com/emotion-js/emotion/tree/main/packages/react",
  publishConfig: {
    access: "public"
  },
  "umd:main": "dist/emotion-react.umd.min.js",
  preconstruct: {
    entrypoints: [
      "./index.js",
      "./jsx-runtime.js",
      "./jsx-dev-runtime.js",
      "./_isolated-hnrs.js"
    ],
    umdName: "emotionReact",
    exports: {
      envConditions: [
        "browser",
        "worker"
      ],
      extra: {
        "./types/css-prop": "./types/css-prop.d.ts",
        "./macro": {
          types: {
            import: "./macro.d.mts",
            default: "./macro.d.ts"
          },
          default: "./macro.js"
        }
      }
    }
  }
}, vu = !1, uf = /* @__PURE__ */ $s(function(e, t) {
  process.env.NODE_ENV !== "production" && !vu && // check for className as well since the user is
  // probably using the custom createElement which
  // means it will be turned into a className prop
  // $FlowFixMe I don't really want to add it to the type since it shouldn't be used
  (e.className || e.css) && (console.error("It looks like you're using the css prop on Global, did you mean to use the styles prop instead?"), vu = !0);
  var n = e.styles, o = Gr([n], void 0, g.useContext(dr)), a = g.useRef();
  return bu(function() {
    var s = t.key + "-global", i = new t.sheet.constructor({
      key: s,
      nonce: t.sheet.nonce,
      container: t.sheet.container,
      speedy: t.sheet.isSpeedy
    }), l = !1, c = document.querySelector('style[data-emotion="' + s + " " + o.name + '"]');
    return t.sheet.tags.length && (i.before = t.sheet.tags[0]), c !== null && (l = !0, c.setAttribute("data-emotion", s), i.hydrate([c])), a.current = [i, l], function() {
      i.flush();
    };
  }, [t]), bu(function() {
    var s = a.current, i = s[0], l = s[1];
    if (l) {
      s[1] = !1;
      return;
    }
    if (o.next !== void 0 && Ds(t, o.next, !0), i.tags.length) {
      var c = i.tags[i.tags.length - 1].nextElementSibling;
      i.before = c, i.flush();
    }
    t.insert("", o, i, !1);
  }, [t, o.name]), null;
});
process.env.NODE_ENV !== "production" && (uf.displayName = "EmotionGlobal");
function ks() {
  for (var e = arguments.length, t = new Array(e), n = 0; n < e; n++)
    t[n] = arguments[n];
  return Gr(t);
}
var io = function() {
  var t = ks.apply(void 0, arguments), n = "animation-" + t.name;
  return {
    name: n,
    styles: "@keyframes " + n + "{" + t.styles + "}",
    anim: 1,
    toString: function() {
      return "_EMO_" + this.name + "_" + this.styles + "_EMO_";
    }
  };
}, Hy = function e(t) {
  for (var n = t.length, o = 0, a = ""; o < n; o++) {
    var s = t[o];
    if (s != null) {
      var i = void 0;
      switch (typeof s) {
        case "boolean":
          break;
        case "object": {
          if (Array.isArray(s))
            i = e(s);
          else {
            process.env.NODE_ENV !== "production" && s.styles !== void 0 && s.name !== void 0 && console.error("You have passed styles created with `css` from `@emotion/react` package to the `cx`.\n`cx` is meant to compose class names (strings) so you should convert those styles to a class name by passing them to the `css` received from <ClassNames/> component."), i = "";
            for (var l in s)
              s[l] && l && (i && (i += " "), i += l);
          }
          break;
        }
        default:
          i = s;
      }
      i && (a && (a += " "), a += i);
    }
  }
  return a;
};
function qy(e, t, n) {
  var o = [], a = Dl(e, o, n);
  return o.length < 2 ? n : a + t(o);
}
var Yy = function(t) {
  var n = t.cache, o = t.serializedArr;
  return kl(function() {
    for (var a = 0; a < o.length; a++)
      Ds(n, o[a], !1);
  }), null;
}, Ky = /* @__PURE__ */ $s(function(e, t) {
  var n = !1, o = [], a = function() {
    if (n && process.env.NODE_ENV !== "production")
      throw new Error("css can only be used during render");
    for (var u = arguments.length, d = new Array(u), f = 0; f < u; f++)
      d[f] = arguments[f];
    var p = Gr(d, t.registered);
    return o.push(p), Rs(t, p, !1), t.key + "-" + p.name;
  }, s = function() {
    if (n && process.env.NODE_ENV !== "production")
      throw new Error("cx can only be used during render");
    for (var u = arguments.length, d = new Array(u), f = 0; f < u; f++)
      d[f] = arguments[f];
    return qy(t.registered, a, Hy(d));
  }, i = {
    css: a,
    cx: s,
    theme: g.useContext(dr)
  }, l = e.children(i);
  return n = !0, /* @__PURE__ */ g.createElement(g.Fragment, null, /* @__PURE__ */ g.createElement(Yy, {
    cache: t,
    serializedArr: o
  }), l);
});
process.env.NODE_ENV !== "production" && (Ky.displayName = "EmotionClassNames");
if (process.env.NODE_ENV !== "production") {
  var xu = !0, Gy = typeof jest < "u" || typeof vi < "u";
  if (xu && !Gy) {
    var Tu = (
      // $FlowIgnore
      typeof globalThis < "u" ? globalThis : xu ? window : global
    ), wu = "__EMOTION_REACT_" + Uy.version.split(".")[0] + "__";
    Tu[wu] && console.warn("You are loading @emotion/react when it is already loaded. Running multiple instances may cause problems. This can happen if multiple versions are used, or if multiple builds of the same version are used."), Tu[wu] = !0;
  }
}
var Xy = qg, Zy = function(t) {
  return t !== "theme";
}, Eu = function(t) {
  return typeof t == "string" && // 96 is one less than the char code
  // for "a" so this is checking that
  // it's a lowercase character
  t.charCodeAt(0) > 96 ? Xy : Zy;
}, Cu = function(t, n, o) {
  var a;
  if (n) {
    var s = n.shouldForwardProp;
    a = t.__emotion_forwardProp && s ? function(i) {
      return t.__emotion_forwardProp(i) && s(i);
    } : s;
  }
  return typeof a != "function" && o && (a = t.__emotion_forwardProp), a;
}, Ou = `You have illegal escape sequence in your template literal, most likely inside content's property value.
Because you write your CSS inside a JavaScript string you actually have to do double escaping, so for example "content: '\\00d7';" should become "content: '\\\\00d7';".
You can read more about this here:
https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Template_literals#ES2018_revision_of_illegal_escape_sequences`, Jy = function(t) {
  var n = t.cache, o = t.serialized, a = t.isStringTag;
  return Rs(n, o, a), kl(function() {
    return Ds(n, o, a);
  }), null;
}, Qy = function e(t, n) {
  if (process.env.NODE_ENV !== "production" && t === void 0)
    throw new Error(`You are trying to create a styled element with an undefined component.
You may have forgotten to import it.`);
  var o = t.__emotion_real === t, a = o && t.__emotion_base || t, s, i;
  n !== void 0 && (s = n.label, i = n.target);
  var l = Cu(t, n, o), c = l || Eu(a), u = !c("as");
  return function() {
    var d = arguments, f = o && t.__emotion_styles !== void 0 ? t.__emotion_styles.slice(0) : [];
    if (s !== void 0 && f.push("label:" + s + ";"), d[0] == null || d[0].raw === void 0)
      f.push.apply(f, d);
    else {
      process.env.NODE_ENV !== "production" && d[0][0] === void 0 && console.error(Ou), f.push(d[0][0]);
      for (var p = d.length, m = 1; m < p; m++)
        process.env.NODE_ENV !== "production" && d[0][m] === void 0 && console.error(Ou), f.push(d[m], d[0][m]);
    }
    var v = $s(function(h, y, w) {
      var C = u && h.as || a, E = "", O = [], T = h;
      if (h.theme == null) {
        T = {};
        for (var P in h)
          T[P] = h[P];
        T.theme = g.useContext(dr);
      }
      typeof h.className == "string" ? E = Dl(y.registered, O, h.className) : h.className != null && (E = h.className + " ");
      var S = Gr(f.concat(O), y.registered, T);
      E += y.key + "-" + S.name, i !== void 0 && (E += " " + i);
      var j = u && l === void 0 ? Eu(C) : c, $ = {};
      for (var V in h)
        u && V === "as" || // $FlowFixMe
        j(V) && ($[V] = h[V]);
      return $.className = E, $.ref = w, /* @__PURE__ */ g.createElement(g.Fragment, null, /* @__PURE__ */ g.createElement(Jy, {
        cache: y,
        serialized: S,
        isStringTag: typeof C == "string"
      }), /* @__PURE__ */ g.createElement(C, $));
    });
    return v.displayName = s !== void 0 ? s : "Styled(" + (typeof a == "string" ? a : a.displayName || a.name || "Component") + ")", v.defaultProps = t.defaultProps, v.__emotion_real = v, v.__emotion_base = a, v.__emotion_styles = f, v.__emotion_forwardProp = l, Object.defineProperty(v, "toString", {
      value: function() {
        return i === void 0 && process.env.NODE_ENV !== "production" ? "NO_COMPONENT_SELECTOR" : "." + i;
      }
    }), v.withComponent = function(h, y) {
      return e(h, b({}, n, y, {
        shouldForwardProp: Cu(v, y, !0)
      })).apply(void 0, f);
    }, v;
  };
}, ev = [
  "a",
  "abbr",
  "address",
  "area",
  "article",
  "aside",
  "audio",
  "b",
  "base",
  "bdi",
  "bdo",
  "big",
  "blockquote",
  "body",
  "br",
  "button",
  "canvas",
  "caption",
  "cite",
  "code",
  "col",
  "colgroup",
  "data",
  "datalist",
  "dd",
  "del",
  "details",
  "dfn",
  "dialog",
  "div",
  "dl",
  "dt",
  "em",
  "embed",
  "fieldset",
  "figcaption",
  "figure",
  "footer",
  "form",
  "h1",
  "h2",
  "h3",
  "h4",
  "h5",
  "h6",
  "head",
  "header",
  "hgroup",
  "hr",
  "html",
  "i",
  "iframe",
  "img",
  "input",
  "ins",
  "kbd",
  "keygen",
  "label",
  "legend",
  "li",
  "link",
  "main",
  "map",
  "mark",
  "marquee",
  "menu",
  "menuitem",
  "meta",
  "meter",
  "nav",
  "noscript",
  "object",
  "ol",
  "optgroup",
  "option",
  "output",
  "p",
  "param",
  "picture",
  "pre",
  "progress",
  "q",
  "rp",
  "rt",
  "ruby",
  "s",
  "samp",
  "script",
  "section",
  "select",
  "small",
  "source",
  "span",
  "strong",
  "style",
  "sub",
  "summary",
  "sup",
  "table",
  "tbody",
  "td",
  "textarea",
  "tfoot",
  "th",
  "thead",
  "time",
  "title",
  "tr",
  "track",
  "u",
  "ul",
  "var",
  "video",
  "wbr",
  // SVG
  "circle",
  "clipPath",
  "defs",
  "ellipse",
  "foreignObject",
  "g",
  "image",
  "line",
  "linearGradient",
  "mask",
  "path",
  "pattern",
  "polygon",
  "polyline",
  "radialGradient",
  "rect",
  "stop",
  "svg",
  "text",
  "tspan"
], zi = Qy.bind();
ev.forEach(function(e) {
  zi[e] = zi(e);
});
let Wi;
typeof document == "object" && (Wi = nf({
  key: "css",
  prepend: !0
}));
function df(e) {
  const {
    injectFirst: t,
    children: n
  } = e;
  return t && Wi ? /* @__PURE__ */ x.jsx(By, {
    value: Wi,
    children: n
  }) : n;
}
process.env.NODE_ENV !== "production" && (df.propTypes = {
  /**
   * Your component tree.
   */
  children: r.node,
  /**
   * By default, the styles are injected last in the <head> element of the page.
   * As a result, they gain more specificity than any other style sheet.
   * If you want to override MUI's styles, set this prop.
   */
  injectFirst: r.bool
});
function tv(e) {
  return e == null || Object.keys(e).length === 0;
}
function Ml(e) {
  const {
    styles: t,
    defaultTheme: n = {}
  } = e, o = typeof t == "function" ? (a) => t(tv(a) ? n : a) : t;
  return /* @__PURE__ */ x.jsx(uf, {
    styles: o
  });
}
process.env.NODE_ENV !== "production" && (Ml.propTypes = {
  defaultTheme: r.object,
  styles: r.oneOfType([r.array, r.string, r.object, r.func])
});
/**
 * @mui/styled-engine v5.15.14
 *
 * @license MIT
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
function pf(e, t) {
  const n = zi(e, t);
  return process.env.NODE_ENV !== "production" ? (...o) => {
    const a = typeof e == "string" ? `"${e}"` : "component";
    return o.length === 0 ? console.error([`MUI: Seems like you called \`styled(${a})()\` without a \`style\` argument.`, 'You must provide a `styles` argument: `styled("div")(styleYouForgotToPass)`.'].join(`
`)) : o.some((s) => s === void 0) && console.error(`MUI: the styled(${a})(...args) API requires all its args to be defined.`), n(...o);
  } : n;
}
const nv = (e, t) => {
  Array.isArray(e.__emotion_styles) && (e.__emotion_styles = t(e.__emotion_styles));
}, rv = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  GlobalStyles: Ml,
  StyledEngineProvider: df,
  ThemeContext: dr,
  css: ks,
  default: pf,
  internal_processStyles: nv,
  keyframes: io
}, Symbol.toStringTag, { value: "Module" }));
function ov(e) {
  return Object.keys(e).length === 0;
}
function ff(e = null) {
  const t = g.useContext(dr);
  return !t || ov(t) ? e : t;
}
const av = Cl();
function lo(e = av) {
  return ff(e);
}
function sv({
  props: e,
  name: t,
  defaultTheme: n,
  themeId: o
}) {
  let a = lo(n);
  return o && (a = a[o] || a), zp({
    theme: a,
    name: t,
    props: e
  });
}
const iv = ["sx"], lv = (e) => {
  var t, n;
  const o = {
    systemProps: {},
    otherProps: {}
  }, a = (t = e == null || (n = e.theme) == null ? void 0 : n.unstable_sxConfig) != null ? t : sa;
  return Object.keys(e).forEach((s) => {
    a[s] ? o.systemProps[s] = e[s] : o.otherProps[s] = e[s];
  }), o;
};
function Il(e) {
  const {
    sx: t
  } = e, n = ie(e, iv), {
    systemProps: o,
    otherProps: a
  } = lv(n);
  let s;
  return Array.isArray(t) ? s = [o, ...t] : typeof t == "function" ? s = (...i) => {
    const l = t(...i);
    return Mn(l) ? b({}, o, l) : o;
  } : s = b({}, o, t), b({}, a, {
    sx: s
  });
}
const cv = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  default: ia,
  extendSxProp: Il,
  unstable_createStyleFunctionSx: qp,
  unstable_defaultSxConfig: sa
}, Symbol.toStringTag, { value: "Module" }));
function uv(e, t) {
  return b({
    toolbar: {
      minHeight: 56,
      [e.up("xs")]: {
        "@media (orientation: landscape)": {
          minHeight: 48
        }
      },
      [e.up("sm")]: {
        minHeight: 64
      }
    }
  }, t);
}
var lt = {};
const dv = /* @__PURE__ */ On(Pb), pv = /* @__PURE__ */ On(Zb);
var mf = Ln;
Object.defineProperty(lt, "__esModule", {
  value: !0
});
var qe = lt.alpha = xf;
lt.blend = wv;
lt.colorChannel = void 0;
var hf = lt.darken = jl;
lt.decomposeColor = qt;
lt.emphasize = Tf;
var Su = lt.getContrastRatio = gv;
lt.getLuminance = Ja;
lt.hexToRgb = gf;
lt.hslToRgb = vf;
var bf = lt.lighten = Al;
lt.private_safeAlpha = yv;
lt.private_safeColorChannel = void 0;
lt.private_safeDarken = vv;
lt.private_safeEmphasize = Tv;
lt.private_safeLighten = xv;
lt.recomposeColor = co;
lt.rgbToHex = bv;
var Pu = mf(dv), fv = mf(pv);
function Nl(e, t = 0, n = 1) {
  return process.env.NODE_ENV !== "production" && (e < t || e > n) && console.error(`MUI: The value provided ${e} is out of range [${t}, ${n}].`), (0, fv.default)(e, t, n);
}
function gf(e) {
  e = e.slice(1);
  const t = new RegExp(`.{1,${e.length >= 6 ? 2 : 1}}`, "g");
  let n = e.match(t);
  return n && n[0].length === 1 && (n = n.map((o) => o + o)), n ? `rgb${n.length === 4 ? "a" : ""}(${n.map((o, a) => a < 3 ? parseInt(o, 16) : Math.round(parseInt(o, 16) / 255 * 1e3) / 1e3).join(", ")})` : "";
}
function mv(e) {
  const t = e.toString(16);
  return t.length === 1 ? `0${t}` : t;
}
function qt(e) {
  if (e.type)
    return e;
  if (e.charAt(0) === "#")
    return qt(gf(e));
  const t = e.indexOf("("), n = e.substring(0, t);
  if (["rgb", "rgba", "hsl", "hsla", "color"].indexOf(n) === -1)
    throw new Error(process.env.NODE_ENV !== "production" ? `MUI: Unsupported \`${e}\` color.
The following formats are supported: #nnn, #nnnnnn, rgb(), rgba(), hsl(), hsla(), color().` : (0, Pu.default)(9, e));
  let o = e.substring(t + 1, e.length - 1), a;
  if (n === "color") {
    if (o = o.split(" "), a = o.shift(), o.length === 4 && o[3].charAt(0) === "/" && (o[3] = o[3].slice(1)), ["srgb", "display-p3", "a98-rgb", "prophoto-rgb", "rec-2020"].indexOf(a) === -1)
      throw new Error(process.env.NODE_ENV !== "production" ? `MUI: unsupported \`${a}\` color space.
The following color spaces are supported: srgb, display-p3, a98-rgb, prophoto-rgb, rec-2020.` : (0, Pu.default)(10, a));
  } else
    o = o.split(",");
  return o = o.map((s) => parseFloat(s)), {
    type: n,
    values: o,
    colorSpace: a
  };
}
const yf = (e) => {
  const t = qt(e);
  return t.values.slice(0, 3).map((n, o) => t.type.indexOf("hsl") !== -1 && o !== 0 ? `${n}%` : n).join(" ");
};
lt.colorChannel = yf;
const hv = (e, t) => {
  try {
    return yf(e);
  } catch {
    return t && process.env.NODE_ENV !== "production" && console.warn(t), e;
  }
};
lt.private_safeColorChannel = hv;
function co(e) {
  const {
    type: t,
    colorSpace: n
  } = e;
  let {
    values: o
  } = e;
  return t.indexOf("rgb") !== -1 ? o = o.map((a, s) => s < 3 ? parseInt(a, 10) : a) : t.indexOf("hsl") !== -1 && (o[1] = `${o[1]}%`, o[2] = `${o[2]}%`), t.indexOf("color") !== -1 ? o = `${n} ${o.join(" ")}` : o = `${o.join(", ")}`, `${t}(${o})`;
}
function bv(e) {
  if (e.indexOf("#") === 0)
    return e;
  const {
    values: t
  } = qt(e);
  return `#${t.map((n, o) => mv(o === 3 ? Math.round(255 * n) : n)).join("")}`;
}
function vf(e) {
  e = qt(e);
  const {
    values: t
  } = e, n = t[0], o = t[1] / 100, a = t[2] / 100, s = o * Math.min(a, 1 - a), i = (u, d = (u + n / 30) % 12) => a - s * Math.max(Math.min(d - 3, 9 - d, 1), -1);
  let l = "rgb";
  const c = [Math.round(i(0) * 255), Math.round(i(8) * 255), Math.round(i(4) * 255)];
  return e.type === "hsla" && (l += "a", c.push(t[3])), co({
    type: l,
    values: c
  });
}
function Ja(e) {
  e = qt(e);
  let t = e.type === "hsl" || e.type === "hsla" ? qt(vf(e)).values : e.values;
  return t = t.map((n) => (e.type !== "color" && (n /= 255), n <= 0.03928 ? n / 12.92 : ((n + 0.055) / 1.055) ** 2.4)), Number((0.2126 * t[0] + 0.7152 * t[1] + 0.0722 * t[2]).toFixed(3));
}
function gv(e, t) {
  const n = Ja(e), o = Ja(t);
  return (Math.max(n, o) + 0.05) / (Math.min(n, o) + 0.05);
}
function xf(e, t) {
  return e = qt(e), t = Nl(t), (e.type === "rgb" || e.type === "hsl") && (e.type += "a"), e.type === "color" ? e.values[3] = `/${t}` : e.values[3] = t, co(e);
}
function yv(e, t, n) {
  try {
    return xf(e, t);
  } catch {
    return n && process.env.NODE_ENV !== "production" && console.warn(n), e;
  }
}
function jl(e, t) {
  if (e = qt(e), t = Nl(t), e.type.indexOf("hsl") !== -1)
    e.values[2] *= 1 - t;
  else if (e.type.indexOf("rgb") !== -1 || e.type.indexOf("color") !== -1)
    for (let n = 0; n < 3; n += 1)
      e.values[n] *= 1 - t;
  return co(e);
}
function vv(e, t, n) {
  try {
    return jl(e, t);
  } catch {
    return n && process.env.NODE_ENV !== "production" && console.warn(n), e;
  }
}
function Al(e, t) {
  if (e = qt(e), t = Nl(t), e.type.indexOf("hsl") !== -1)
    e.values[2] += (100 - e.values[2]) * t;
  else if (e.type.indexOf("rgb") !== -1)
    for (let n = 0; n < 3; n += 1)
      e.values[n] += (255 - e.values[n]) * t;
  else if (e.type.indexOf("color") !== -1)
    for (let n = 0; n < 3; n += 1)
      e.values[n] += (1 - e.values[n]) * t;
  return co(e);
}
function xv(e, t, n) {
  try {
    return Al(e, t);
  } catch {
    return n && process.env.NODE_ENV !== "production" && console.warn(n), e;
  }
}
function Tf(e, t = 0.15) {
  return Ja(e) > 0.5 ? jl(e, t) : Al(e, t);
}
function Tv(e, t, n) {
  try {
    return Tf(e, t);
  } catch {
    return n && process.env.NODE_ENV !== "production" && console.warn(n), e;
  }
}
function wv(e, t, n, o = 1) {
  const a = (c, u) => Math.round((c ** (1 / o) * (1 - n) + u ** (1 / o) * n) ** o), s = qt(e), i = qt(t), l = [a(s.values[0], i.values[0]), a(s.values[1], i.values[1]), a(s.values[2], i.values[2])];
  return co({
    type: "rgb",
    values: l
  });
}
const qo = {
  black: "#000",
  white: "#fff"
}, Ev = {
  50: "#fafafa",
  100: "#f5f5f5",
  200: "#eeeeee",
  300: "#e0e0e0",
  400: "#bdbdbd",
  500: "#9e9e9e",
  600: "#757575",
  700: "#616161",
  800: "#424242",
  900: "#212121",
  A100: "#f5f5f5",
  A200: "#eeeeee",
  A400: "#bdbdbd",
  A700: "#616161"
}, Sr = {
  50: "#f3e5f5",
  100: "#e1bee7",
  200: "#ce93d8",
  300: "#ba68c8",
  400: "#ab47bc",
  500: "#9c27b0",
  600: "#8e24aa",
  700: "#7b1fa2",
  800: "#6a1b9a",
  900: "#4a148c",
  A100: "#ea80fc",
  A200: "#e040fb",
  A400: "#d500f9",
  A700: "#aa00ff"
}, Pr = {
  50: "#ffebee",
  100: "#ffcdd2",
  200: "#ef9a9a",
  300: "#e57373",
  400: "#ef5350",
  500: "#f44336",
  600: "#e53935",
  700: "#d32f2f",
  800: "#c62828",
  900: "#b71c1c",
  A100: "#ff8a80",
  A200: "#ff5252",
  A400: "#ff1744",
  A700: "#d50000"
}, To = {
  50: "#fff3e0",
  100: "#ffe0b2",
  200: "#ffcc80",
  300: "#ffb74d",
  400: "#ffa726",
  500: "#ff9800",
  600: "#fb8c00",
  700: "#f57c00",
  800: "#ef6c00",
  900: "#e65100",
  A100: "#ffd180",
  A200: "#ffab40",
  A400: "#ff9100",
  A700: "#ff6d00"
}, Rr = {
  50: "#e3f2fd",
  100: "#bbdefb",
  200: "#90caf9",
  300: "#64b5f6",
  400: "#42a5f5",
  500: "#2196f3",
  600: "#1e88e5",
  700: "#1976d2",
  800: "#1565c0",
  900: "#0d47a1",
  A100: "#82b1ff",
  A200: "#448aff",
  A400: "#2979ff",
  A700: "#2962ff"
}, Dr = {
  50: "#e1f5fe",
  100: "#b3e5fc",
  200: "#81d4fa",
  300: "#4fc3f7",
  400: "#29b6f6",
  500: "#03a9f4",
  600: "#039be5",
  700: "#0288d1",
  800: "#0277bd",
  900: "#01579b",
  A100: "#80d8ff",
  A200: "#40c4ff",
  A400: "#00b0ff",
  A700: "#0091ea"
}, $r = {
  50: "#e8f5e9",
  100: "#c8e6c9",
  200: "#a5d6a7",
  300: "#81c784",
  400: "#66bb6a",
  500: "#4caf50",
  600: "#43a047",
  700: "#388e3c",
  800: "#2e7d32",
  900: "#1b5e20",
  A100: "#b9f6ca",
  A200: "#69f0ae",
  A400: "#00e676",
  A700: "#00c853"
}, Cv = ["mode", "contrastThreshold", "tonalOffset"], Ru = {
  // The colors used to style the text.
  text: {
    // The most important text.
    primary: "rgba(0, 0, 0, 0.87)",
    // Secondary text.
    secondary: "rgba(0, 0, 0, 0.6)",
    // Disabled text have even lower visual prominence.
    disabled: "rgba(0, 0, 0, 0.38)"
  },
  // The color used to divide different elements.
  divider: "rgba(0, 0, 0, 0.12)",
  // The background colors used to style the surfaces.
  // Consistency between these values is important.
  background: {
    paper: qo.white,
    default: qo.white
  },
  // The colors used to style the action elements.
  action: {
    // The color of an active action like an icon button.
    active: "rgba(0, 0, 0, 0.54)",
    // The color of an hovered action.
    hover: "rgba(0, 0, 0, 0.04)",
    hoverOpacity: 0.04,
    // The color of a selected action.
    selected: "rgba(0, 0, 0, 0.08)",
    selectedOpacity: 0.08,
    // The color of a disabled action.
    disabled: "rgba(0, 0, 0, 0.26)",
    // The background color of a disabled action.
    disabledBackground: "rgba(0, 0, 0, 0.12)",
    disabledOpacity: 0.38,
    focus: "rgba(0, 0, 0, 0.12)",
    focusOpacity: 0.12,
    activatedOpacity: 0.12
  }
}, bi = {
  text: {
    primary: qo.white,
    secondary: "rgba(255, 255, 255, 0.7)",
    disabled: "rgba(255, 255, 255, 0.5)",
    icon: "rgba(255, 255, 255, 0.5)"
  },
  divider: "rgba(255, 255, 255, 0.12)",
  background: {
    paper: "#121212",
    default: "#121212"
  },
  action: {
    active: qo.white,
    hover: "rgba(255, 255, 255, 0.08)",
    hoverOpacity: 0.08,
    selected: "rgba(255, 255, 255, 0.16)",
    selectedOpacity: 0.16,
    disabled: "rgba(255, 255, 255, 0.3)",
    disabledBackground: "rgba(255, 255, 255, 0.12)",
    disabledOpacity: 0.38,
    focus: "rgba(255, 255, 255, 0.12)",
    focusOpacity: 0.12,
    activatedOpacity: 0.24
  }
};
function Du(e, t, n, o) {
  const a = o.light || o, s = o.dark || o * 1.5;
  e[t] || (e.hasOwnProperty(n) ? e[t] = e[n] : t === "light" ? e.light = bf(e.main, a) : t === "dark" && (e.dark = hf(e.main, s)));
}
function Ov(e = "light") {
  return e === "dark" ? {
    main: Rr[200],
    light: Rr[50],
    dark: Rr[400]
  } : {
    main: Rr[700],
    light: Rr[400],
    dark: Rr[800]
  };
}
function Sv(e = "light") {
  return e === "dark" ? {
    main: Sr[200],
    light: Sr[50],
    dark: Sr[400]
  } : {
    main: Sr[500],
    light: Sr[300],
    dark: Sr[700]
  };
}
function Pv(e = "light") {
  return e === "dark" ? {
    main: Pr[500],
    light: Pr[300],
    dark: Pr[700]
  } : {
    main: Pr[700],
    light: Pr[400],
    dark: Pr[800]
  };
}
function Rv(e = "light") {
  return e === "dark" ? {
    main: Dr[400],
    light: Dr[300],
    dark: Dr[700]
  } : {
    main: Dr[700],
    light: Dr[500],
    dark: Dr[900]
  };
}
function Dv(e = "light") {
  return e === "dark" ? {
    main: $r[400],
    light: $r[300],
    dark: $r[700]
  } : {
    main: $r[800],
    light: $r[500],
    dark: $r[900]
  };
}
function $v(e = "light") {
  return e === "dark" ? {
    main: To[400],
    light: To[300],
    dark: To[700]
  } : {
    main: "#ed6c02",
    // closest to orange[800] that pass 3:1.
    light: To[500],
    dark: To[900]
  };
}
function kv(e) {
  const {
    mode: t = "light",
    contrastThreshold: n = 3,
    tonalOffset: o = 0.2
  } = e, a = ie(e, Cv), s = e.primary || Ov(t), i = e.secondary || Sv(t), l = e.error || Pv(t), c = e.info || Rv(t), u = e.success || Dv(t), d = e.warning || $v(t);
  function f(h) {
    const y = Su(h, bi.text.primary) >= n ? bi.text.primary : Ru.text.primary;
    if (process.env.NODE_ENV !== "production") {
      const w = Su(h, y);
      w < 3 && console.error([`MUI: The contrast ratio of ${w}:1 for ${y} on ${h}`, "falls below the WCAG recommended absolute minimum contrast ratio of 3:1.", "https://www.w3.org/TR/2008/REC-WCAG20-20081211/#visual-audio-contrast-contrast"].join(`
`));
    }
    return y;
  }
  const p = ({
    color: h,
    name: y,
    mainShade: w = 500,
    lightShade: C = 300,
    darkShade: E = 700
  }) => {
    if (h = b({}, h), !h.main && h[w] && (h.main = h[w]), !h.hasOwnProperty("main"))
      throw new Error(process.env.NODE_ENV !== "production" ? `MUI: The color${y ? ` (${y})` : ""} provided to augmentColor(color) is invalid.
The color object needs to have a \`main\` property or a \`${w}\` property.` : xn(11, y ? ` (${y})` : "", w));
    if (typeof h.main != "string")
      throw new Error(process.env.NODE_ENV !== "production" ? `MUI: The color${y ? ` (${y})` : ""} provided to augmentColor(color) is invalid.
\`color.main\` should be a string, but \`${JSON.stringify(h.main)}\` was provided instead.

Did you intend to use one of the following approaches?

import { green } from "@mui/material/colors";

const theme1 = createTheme({ palette: {
  primary: green,
} });

const theme2 = createTheme({ palette: {
  primary: { main: green[500] },
} });` : xn(12, y ? ` (${y})` : "", JSON.stringify(h.main)));
    return Du(h, "light", C, o), Du(h, "dark", E, o), h.contrastText || (h.contrastText = f(h.main)), h;
  }, m = {
    dark: bi,
    light: Ru
  };
  return process.env.NODE_ENV !== "production" && (m[t] || console.error(`MUI: The palette mode \`${t}\` is not supported.`)), kt(b({
    // A collection of common colors.
    common: b({}, qo),
    // prevent mutable object.
    // The palette mode, can be light or dark.
    mode: t,
    // The colors used to represent primary interface elements for a user.
    primary: p({
      color: s,
      name: "primary"
    }),
    // The colors used to represent secondary interface elements for a user.
    secondary: p({
      color: i,
      name: "secondary",
      mainShade: "A400",
      lightShade: "A200",
      darkShade: "A700"
    }),
    // The colors used to represent interface elements that the user should be made aware of.
    error: p({
      color: l,
      name: "error"
    }),
    // The colors used to represent potentially dangerous actions or important messages.
    warning: p({
      color: d,
      name: "warning"
    }),
    // The colors used to present information to the user that is neutral and not necessarily important.
    info: p({
      color: c,
      name: "info"
    }),
    // The colors used to indicate the successful completion of an action that user triggered.
    success: p({
      color: u,
      name: "success"
    }),
    // The grey colors.
    grey: Ev,
    // Used by `getContrastText()` to maximize the contrast between
    // the background and the text.
    contrastThreshold: n,
    // Takes a background color and returns the text color that maximizes the contrast.
    getContrastText: f,
    // Generate a rich color object.
    augmentColor: p,
    // Used by the functions below to shift a color's luminance by approximately
    // two indexes within its tonal palette.
    // E.g., shift from Red 500 to Red 300 or Red 700.
    tonalOffset: o
  }, m[t]), a);
}
const _v = ["fontFamily", "fontSize", "fontWeightLight", "fontWeightRegular", "fontWeightMedium", "fontWeightBold", "htmlFontSize", "allVariants", "pxToRem"];
function Mv(e) {
  return Math.round(e * 1e5) / 1e5;
}
const $u = {
  textTransform: "uppercase"
}, ku = '"Roboto", "Helvetica", "Arial", sans-serif';
function Iv(e, t) {
  const n = typeof t == "function" ? t(e) : t, {
    fontFamily: o = ku,
    // The default font size of the Material Specification.
    fontSize: a = 14,
    // px
    fontWeightLight: s = 300,
    fontWeightRegular: i = 400,
    fontWeightMedium: l = 500,
    fontWeightBold: c = 700,
    // Tell MUI what's the font-size on the html element.
    // 16px is the default font-size used by browsers.
    htmlFontSize: u = 16,
    // Apply the CSS properties to all the variants.
    allVariants: d,
    pxToRem: f
  } = n, p = ie(n, _v);
  process.env.NODE_ENV !== "production" && (typeof a != "number" && console.error("MUI: `fontSize` is required to be a number."), typeof u != "number" && console.error("MUI: `htmlFontSize` is required to be a number."));
  const m = a / 14, v = f || ((w) => `${w / u * m}rem`), h = (w, C, E, O, T) => b({
    fontFamily: o,
    fontWeight: w,
    fontSize: v(C),
    // Unitless following https://meyerweb.com/eric/thoughts/2006/02/08/unitless-line-heights/
    lineHeight: E
  }, o === ku ? {
    letterSpacing: `${Mv(O / C)}em`
  } : {}, T, d), y = {
    h1: h(s, 96, 1.167, -1.5),
    h2: h(s, 60, 1.2, -0.5),
    h3: h(i, 48, 1.167, 0),
    h4: h(i, 34, 1.235, 0.25),
    h5: h(i, 24, 1.334, 0),
    h6: h(l, 20, 1.6, 0.15),
    subtitle1: h(i, 16, 1.75, 0.15),
    subtitle2: h(l, 14, 1.57, 0.1),
    body1: h(i, 16, 1.5, 0.15),
    body2: h(i, 14, 1.43, 0.15),
    button: h(l, 14, 1.75, 0.4, $u),
    caption: h(i, 12, 1.66, 0.4),
    overline: h(i, 12, 2.66, 1, $u),
    // TODO v6: Remove handling of 'inherit' variant from the theme as it is already handled in Material UI's Typography component. Also, remember to remove the associated types.
    inherit: {
      fontFamily: "inherit",
      fontWeight: "inherit",
      fontSize: "inherit",
      lineHeight: "inherit",
      letterSpacing: "inherit"
    }
  };
  return kt(b({
    htmlFontSize: u,
    pxToRem: v,
    fontFamily: o,
    fontSize: a,
    fontWeightLight: s,
    fontWeightRegular: i,
    fontWeightMedium: l,
    fontWeightBold: c
  }, y), p, {
    clone: !1
    // No need to clone deep
  });
}
const Nv = 0.2, jv = 0.14, Av = 0.12;
function et(...e) {
  return [`${e[0]}px ${e[1]}px ${e[2]}px ${e[3]}px rgba(0,0,0,${Nv})`, `${e[4]}px ${e[5]}px ${e[6]}px ${e[7]}px rgba(0,0,0,${jv})`, `${e[8]}px ${e[9]}px ${e[10]}px ${e[11]}px rgba(0,0,0,${Av})`].join(",");
}
const Fv = ["none", et(0, 2, 1, -1, 0, 1, 1, 0, 0, 1, 3, 0), et(0, 3, 1, -2, 0, 2, 2, 0, 0, 1, 5, 0), et(0, 3, 3, -2, 0, 3, 4, 0, 0, 1, 8, 0), et(0, 2, 4, -1, 0, 4, 5, 0, 0, 1, 10, 0), et(0, 3, 5, -1, 0, 5, 8, 0, 0, 1, 14, 0), et(0, 3, 5, -1, 0, 6, 10, 0, 0, 1, 18, 0), et(0, 4, 5, -2, 0, 7, 10, 1, 0, 2, 16, 1), et(0, 5, 5, -3, 0, 8, 10, 1, 0, 3, 14, 2), et(0, 5, 6, -3, 0, 9, 12, 1, 0, 3, 16, 2), et(0, 6, 6, -3, 0, 10, 14, 1, 0, 4, 18, 3), et(0, 6, 7, -4, 0, 11, 15, 1, 0, 4, 20, 3), et(0, 7, 8, -4, 0, 12, 17, 2, 0, 5, 22, 4), et(0, 7, 8, -4, 0, 13, 19, 2, 0, 5, 24, 4), et(0, 7, 9, -4, 0, 14, 21, 2, 0, 5, 26, 4), et(0, 8, 9, -5, 0, 15, 22, 2, 0, 6, 28, 5), et(0, 8, 10, -5, 0, 16, 24, 2, 0, 6, 30, 5), et(0, 8, 11, -5, 0, 17, 26, 2, 0, 6, 32, 5), et(0, 9, 11, -5, 0, 18, 28, 2, 0, 7, 34, 6), et(0, 9, 12, -6, 0, 19, 29, 2, 0, 7, 36, 6), et(0, 10, 13, -6, 0, 20, 31, 3, 0, 8, 38, 7), et(0, 10, 13, -6, 0, 21, 33, 3, 0, 8, 40, 7), et(0, 10, 14, -6, 0, 22, 35, 3, 0, 8, 42, 7), et(0, 11, 14, -7, 0, 23, 36, 3, 0, 9, 44, 8), et(0, 11, 15, -7, 0, 24, 38, 3, 0, 9, 46, 8)], Vv = ["duration", "easing", "delay"], Lv = {
  // This is the most common easing curve.
  easeInOut: "cubic-bezier(0.4, 0, 0.2, 1)",
  // Objects enter the screen at full velocity from off-screen and
  // slowly decelerate to a resting point.
  easeOut: "cubic-bezier(0.0, 0, 0.2, 1)",
  // Objects leave the screen at full velocity. They do not decelerate when off-screen.
  easeIn: "cubic-bezier(0.4, 0, 1, 1)",
  // The sharp curve is used by objects that may return to the screen at any time.
  sharp: "cubic-bezier(0.4, 0, 0.6, 1)"
}, Bv = {
  shortest: 150,
  shorter: 200,
  short: 250,
  // most basic recommended timing
  standard: 300,
  // this is to be used in complex animations
  complex: 375,
  // recommended when something is entering screen
  enteringScreen: 225,
  // recommended when something is leaving screen
  leavingScreen: 195
};
function _u(e) {
  return `${Math.round(e)}ms`;
}
function zv(e) {
  if (!e)
    return 0;
  const t = e / 36;
  return Math.round((4 + 15 * t ** 0.25 + t / 5) * 10);
}
function Wv(e) {
  const t = b({}, Lv, e.easing), n = b({}, Bv, e.duration);
  return b({
    getAutoHeightDuration: zv,
    create: (a = ["all"], s = {}) => {
      const {
        duration: i = n.standard,
        easing: l = t.easeInOut,
        delay: c = 0
      } = s, u = ie(s, Vv);
      if (process.env.NODE_ENV !== "production") {
        const d = (p) => typeof p == "string", f = (p) => !isNaN(parseFloat(p));
        !d(a) && !Array.isArray(a) && console.error('MUI: Argument "props" must be a string or Array.'), !f(i) && !d(i) && console.error(`MUI: Argument "duration" must be a number or a string but found ${i}.`), d(l) || console.error('MUI: Argument "easing" must be a string.'), !f(c) && !d(c) && console.error('MUI: Argument "delay" must be a number or a string.'), typeof s != "object" && console.error(["MUI: Secong argument of transition.create must be an object.", "Arguments should be either `create('prop1', options)` or `create(['prop1', 'prop2'], options)`"].join(`
`)), Object.keys(u).length !== 0 && console.error(`MUI: Unrecognized argument(s) [${Object.keys(u).join(",")}].`);
      }
      return (Array.isArray(a) ? a : [a]).map((d) => `${d} ${typeof i == "string" ? i : _u(i)} ${l} ${typeof c == "string" ? c : _u(c)}`).join(",");
    }
  }, e, {
    easing: t,
    duration: n
  });
}
const Uv = {
  mobileStepper: 1e3,
  fab: 1050,
  speedDial: 1050,
  appBar: 1100,
  drawer: 1200,
  modal: 1300,
  snackbar: 1400,
  tooltip: 1500
}, Hv = ["breakpoints", "mixins", "spacing", "palette", "transitions", "typography", "shape"];
function wf(e = {}, ...t) {
  const {
    mixins: n = {},
    palette: o = {},
    transitions: a = {},
    typography: s = {}
  } = e, i = ie(e, Hv);
  if (e.vars)
    throw new Error(process.env.NODE_ENV !== "production" ? "MUI: `vars` is a private field used for CSS variables support.\nPlease use another name." : xn(18));
  const l = kv(o), c = Cl(e);
  let u = kt(c, {
    mixins: uv(c.breakpoints, n),
    palette: l,
    // Don't use [...shadows] until you've verified its transpiled code is not invoking the iterator protocol.
    shadows: Fv.slice(),
    typography: Iv(l, s),
    transitions: Wv(a),
    zIndex: b({}, Uv)
  });
  if (u = kt(u, i), u = t.reduce((d, f) => kt(d, f), u), process.env.NODE_ENV !== "production") {
    const d = ["active", "checked", "completed", "disabled", "error", "expanded", "focused", "focusVisible", "required", "selected"], f = (p, m) => {
      let v;
      for (v in p) {
        const h = p[v];
        if (d.indexOf(v) !== -1 && Object.keys(h).length > 0) {
          if (process.env.NODE_ENV !== "production") {
            const y = Pe("", v);
            console.error([`MUI: The \`${m}\` component increases the CSS specificity of the \`${v}\` internal state.`, "You can not override it like this: ", JSON.stringify(p, null, 2), "", `Instead, you need to use the '&.${y}' syntax:`, JSON.stringify({
              root: {
                [`&.${y}`]: h
              }
            }, null, 2), "", "https://mui.com/r/state-classes-guide"].join(`
`));
          }
          p[v] = {};
        }
      }
    };
    Object.keys(u.components).forEach((p) => {
      const m = u.components[p].styleOverrides;
      m && p.indexOf("Mui") === 0 && f(m, p);
    });
  }
  return u.unstable_sxConfig = b({}, sa, i == null ? void 0 : i.unstable_sxConfig), u.unstable_sx = function(f) {
    return ia({
      sx: f,
      theme: this
    });
  }, u;
}
const _s = wf(), ca = "$$material";
function Ee({
  props: e,
  name: t
}) {
  return sv({
    props: e,
    name: t,
    defaultTheme: _s,
    themeId: ca
  });
}
var ua = {}, gi = { exports: {} }, Mu;
function qv() {
  return Mu || (Mu = 1, function(e) {
    function t() {
      return e.exports = t = Object.assign ? Object.assign.bind() : function(n) {
        for (var o = 1; o < arguments.length; o++) {
          var a = arguments[o];
          for (var s in a) ({}).hasOwnProperty.call(a, s) && (n[s] = a[s]);
        }
        return n;
      }, e.exports.__esModule = !0, e.exports.default = e.exports, t.apply(null, arguments);
    }
    e.exports = t, e.exports.__esModule = !0, e.exports.default = e.exports;
  }(gi)), gi.exports;
}
var yi = { exports: {} }, Iu;
function Yv() {
  return Iu || (Iu = 1, function(e) {
    function t(n, o) {
      if (n == null) return {};
      var a = {};
      for (var s in n) if ({}.hasOwnProperty.call(n, s)) {
        if (o.indexOf(s) >= 0) continue;
        a[s] = n[s];
      }
      return a;
    }
    e.exports = t, e.exports.__esModule = !0, e.exports.default = e.exports;
  }(yi)), yi.exports;
}
const Ef = /* @__PURE__ */ On(rv), Kv = /* @__PURE__ */ On(fb), Gv = /* @__PURE__ */ On(Mb), Xv = /* @__PURE__ */ On(_b), Zv = /* @__PURE__ */ On(Ug), Jv = /* @__PURE__ */ On(cv);
var uo = Ln;
Object.defineProperty(ua, "__esModule", {
  value: !0
});
var Qv = ua.default = fx;
ua.shouldForwardProp = za;
ua.systemDefaultTheme = void 0;
var Lt = uo(qv()), Ui = uo(Yv()), Nu = lx(Ef), ex = Kv, tx = uo(Gv), nx = uo(Xv), rx = uo(Zv), ox = uo(Jv);
const ax = ["ownerState"], sx = ["variants"], ix = ["name", "slot", "skipVariantsResolver", "skipSx", "overridesResolver"];
function Cf(e) {
  if (typeof WeakMap != "function") return null;
  var t = /* @__PURE__ */ new WeakMap(), n = /* @__PURE__ */ new WeakMap();
  return (Cf = function(o) {
    return o ? n : t;
  })(e);
}
function lx(e, t) {
  if (e && e.__esModule) return e;
  if (e === null || typeof e != "object" && typeof e != "function") return { default: e };
  var n = Cf(t);
  if (n && n.has(e)) return n.get(e);
  var o = { __proto__: null }, a = Object.defineProperty && Object.getOwnPropertyDescriptor;
  for (var s in e) if (s !== "default" && Object.prototype.hasOwnProperty.call(e, s)) {
    var i = a ? Object.getOwnPropertyDescriptor(e, s) : null;
    i && (i.get || i.set) ? Object.defineProperty(o, s, i) : o[s] = e[s];
  }
  return o.default = e, n && n.set(e, o), o;
}
function cx(e) {
  return Object.keys(e).length === 0;
}
function ux(e) {
  return typeof e == "string" && // 96 is one less than the char code
  // for "a" so this is checking that
  // it's a lowercase character
  e.charCodeAt(0) > 96;
}
function za(e) {
  return e !== "ownerState" && e !== "theme" && e !== "sx" && e !== "as";
}
const dx = ua.systemDefaultTheme = (0, rx.default)(), ju = (e) => e && e.charAt(0).toLowerCase() + e.slice(1);
function Oa({
  defaultTheme: e,
  theme: t,
  themeId: n
}) {
  return cx(t) ? e : t[n] || t;
}
function px(e) {
  return e ? (t, n) => n[e] : null;
}
function Wa(e, t) {
  let {
    ownerState: n
  } = t, o = (0, Ui.default)(t, ax);
  const a = typeof e == "function" ? e((0, Lt.default)({
    ownerState: n
  }, o)) : e;
  if (Array.isArray(a))
    return a.flatMap((s) => Wa(s, (0, Lt.default)({
      ownerState: n
    }, o)));
  if (a && typeof a == "object" && Array.isArray(a.variants)) {
    const {
      variants: s = []
    } = a;
    let l = (0, Ui.default)(a, sx);
    return s.forEach((c) => {
      let u = !0;
      typeof c.props == "function" ? u = c.props((0, Lt.default)({
        ownerState: n
      }, o, n)) : Object.keys(c.props).forEach((d) => {
        (n == null ? void 0 : n[d]) !== c.props[d] && o[d] !== c.props[d] && (u = !1);
      }), u && (Array.isArray(l) || (l = [l]), l.push(typeof c.style == "function" ? c.style((0, Lt.default)({
        ownerState: n
      }, o, n)) : c.style));
    }), l;
  }
  return a;
}
function fx(e = {}) {
  const {
    themeId: t,
    defaultTheme: n = dx,
    rootShouldForwardProp: o = za,
    slotShouldForwardProp: a = za
  } = e, s = (i) => (0, ox.default)((0, Lt.default)({}, i, {
    theme: Oa((0, Lt.default)({}, i, {
      defaultTheme: n,
      themeId: t
    }))
  }));
  return s.__mui_systemSx = !0, (i, l = {}) => {
    (0, Nu.internal_processStyles)(i, (T) => T.filter((P) => !(P != null && P.__mui_systemSx)));
    const {
      name: c,
      slot: u,
      skipVariantsResolver: d,
      skipSx: f,
      // TODO v6: remove `lowercaseFirstLetter()` in the next major release
      // For more details: https://github.com/mui/material-ui/pull/37908
      overridesResolver: p = px(ju(u))
    } = l, m = (0, Ui.default)(l, ix), v = d !== void 0 ? d : (
      // TODO v6: remove `Root` in the next major release
      // For more details: https://github.com/mui/material-ui/pull/37908
      u && u !== "Root" && u !== "root" || !1
    ), h = f || !1;
    let y;
    process.env.NODE_ENV !== "production" && c && (y = `${c}-${ju(u || "Root")}`);
    let w = za;
    u === "Root" || u === "root" ? w = o : u ? w = a : ux(i) && (w = void 0);
    const C = (0, Nu.default)(i, (0, Lt.default)({
      shouldForwardProp: w,
      label: y
    }, m)), E = (T) => typeof T == "function" && T.__emotion_real !== T || (0, ex.isPlainObject)(T) ? (P) => Wa(T, (0, Lt.default)({}, P, {
      theme: Oa({
        theme: P.theme,
        defaultTheme: n,
        themeId: t
      })
    })) : T, O = (T, ...P) => {
      let S = E(T);
      const j = P ? P.map(E) : [];
      c && p && j.push((_) => {
        const L = Oa((0, Lt.default)({}, _, {
          defaultTheme: n,
          themeId: t
        }));
        if (!L.components || !L.components[c] || !L.components[c].styleOverrides)
          return null;
        const M = L.components[c].styleOverrides, R = {};
        return Object.entries(M).forEach(([D, F]) => {
          R[D] = Wa(F, (0, Lt.default)({}, _, {
            theme: L
          }));
        }), p(_, R);
      }), c && !v && j.push((_) => {
        var L;
        const M = Oa((0, Lt.default)({}, _, {
          defaultTheme: n,
          themeId: t
        })), R = M == null || (L = M.components) == null || (L = L[c]) == null ? void 0 : L.variants;
        return Wa({
          variants: R
        }, (0, Lt.default)({}, _, {
          theme: M
        }));
      }), h || j.push(s);
      const $ = j.length - P.length;
      if (Array.isArray(T) && $ > 0) {
        const _ = new Array($).fill("");
        S = [...T, ..._], S.raw = [...T.raw, ..._];
      }
      const V = C(S, ...j);
      if (process.env.NODE_ENV !== "production") {
        let _;
        c && (_ = `${c}${(0, tx.default)(u || "")}`), _ === void 0 && (_ = `Styled(${(0, nx.default)(i)})`), V.displayName = _;
      }
      return i.muiName && (V.muiName = i.muiName), V;
    };
    return C.withConfig && (O.withConfig = C.withConfig), O;
  };
}
function Of(e) {
  return e !== "ownerState" && e !== "theme" && e !== "sx" && e !== "as";
}
const nn = (e) => Of(e) && e !== "classes", Z = Qv({
  themeId: ca,
  defaultTheme: _s,
  rootShouldForwardProp: nn
});
function mx(e) {
  return Pe("MuiSvgIcon", e);
}
Ce("MuiSvgIcon", ["root", "colorPrimary", "colorSecondary", "colorAction", "colorError", "colorDisabled", "fontSizeInherit", "fontSizeSmall", "fontSizeMedium", "fontSizeLarge"]);
const hx = ["children", "className", "color", "component", "fontSize", "htmlColor", "inheritViewBox", "titleAccess", "viewBox"], bx = (e) => {
  const {
    color: t,
    fontSize: n,
    classes: o
  } = e, a = {
    root: ["root", t !== "inherit" && `color${de(t)}`, `fontSize${de(n)}`]
  };
  return Se(a, mx, o);
}, gx = Z("svg", {
  name: "MuiSvgIcon",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.color !== "inherit" && t[`color${de(n.color)}`], t[`fontSize${de(n.fontSize)}`]];
  }
})(({
  theme: e,
  ownerState: t
}) => {
  var n, o, a, s, i, l, c, u, d, f, p, m, v;
  return {
    userSelect: "none",
    width: "1em",
    height: "1em",
    display: "inline-block",
    // the <svg> will define the property that has `currentColor`
    // for example heroicons uses fill="none" and stroke="currentColor"
    fill: t.hasSvgAsChild ? void 0 : "currentColor",
    flexShrink: 0,
    transition: (n = e.transitions) == null || (o = n.create) == null ? void 0 : o.call(n, "fill", {
      duration: (a = e.transitions) == null || (a = a.duration) == null ? void 0 : a.shorter
    }),
    fontSize: {
      inherit: "inherit",
      small: ((s = e.typography) == null || (i = s.pxToRem) == null ? void 0 : i.call(s, 20)) || "1.25rem",
      medium: ((l = e.typography) == null || (c = l.pxToRem) == null ? void 0 : c.call(l, 24)) || "1.5rem",
      large: ((u = e.typography) == null || (d = u.pxToRem) == null ? void 0 : d.call(u, 35)) || "2.1875rem"
    }[t.fontSize],
    // TODO v5 deprecate, v6 remove for sx
    color: (f = (p = (e.vars || e).palette) == null || (p = p[t.color]) == null ? void 0 : p.main) != null ? f : {
      action: (m = (e.vars || e).palette) == null || (m = m.action) == null ? void 0 : m.active,
      disabled: (v = (e.vars || e).palette) == null || (v = v.action) == null ? void 0 : v.disabled,
      inherit: void 0
    }[t.color]
  };
}), Yo = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiSvgIcon"
  }), {
    children: a,
    className: s,
    color: i = "inherit",
    component: l = "svg",
    fontSize: c = "medium",
    htmlColor: u,
    inheritViewBox: d = !1,
    titleAccess: f,
    viewBox: p = "0 0 24 24"
  } = o, m = ie(o, hx), v = /* @__PURE__ */ g.isValidElement(a) && a.type === "svg", h = b({}, o, {
    color: i,
    component: l,
    fontSize: c,
    instanceFontSize: t.fontSize,
    inheritViewBox: d,
    viewBox: p,
    hasSvgAsChild: v
  }), y = {};
  d || (y.viewBox = p);
  const w = bx(h);
  return /* @__PURE__ */ x.jsxs(gx, b({
    as: l,
    className: pe(w.root, s),
    focusable: "false",
    color: u,
    "aria-hidden": f ? void 0 : !0,
    role: f ? "img" : void 0,
    ref: n
  }, y, m, v && a.props, {
    ownerState: h,
    children: [v ? a.props.children : a, f ? /* @__PURE__ */ x.jsx("title", {
      children: f
    }) : null]
  }));
});
process.env.NODE_ENV !== "production" && (Yo.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Node passed into the SVG element.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * You can use the `htmlColor` prop to apply a color attribute to the SVG element.
   * @default 'inherit'
   */
  color: r.oneOfType([r.oneOf(["inherit", "action", "disabled", "primary", "secondary", "error", "info", "success", "warning"]), r.string]),
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The fontSize applied to the icon. Defaults to 24px, but can be configure to inherit font size.
   * @default 'medium'
   */
  fontSize: r.oneOfType([r.oneOf(["inherit", "large", "medium", "small"]), r.string]),
  /**
   * Applies a color attribute to the SVG element.
   */
  htmlColor: r.string,
  /**
   * If `true`, the root node will inherit the custom `component`'s viewBox and the `viewBox`
   * prop will be ignored.
   * Useful when you want to reference a custom `component` and have `SvgIcon` pass that
   * `component`'s viewBox to the root node.
   * @default false
   */
  inheritViewBox: r.bool,
  /**
   * The shape-rendering attribute. The behavior of the different options is described on the
   * [MDN Web Docs](https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/shape-rendering).
   * If you are having issues with blurry icons you should investigate this prop.
   */
  shapeRendering: r.string,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Provides a human-readable title for the element that contains it.
   * https://www.w3.org/TR/SVG-access/#Equivalent
   */
  titleAccess: r.string,
  /**
   * Allows you to redefine what the coordinates without units mean inside an SVG element.
   * For example, if the SVG element is 500 (width) by 200 (height),
   * and you pass viewBox="0 0 50 20",
   * this means that the coordinates inside the SVG will go from the top left corner (0,0)
   * to bottom right (50,20) and each unit will be worth 10px.
   * @default '0 0 24 24'
   */
  viewBox: r.string
});
Yo.muiName = "SvgIcon";
function rn(e, t) {
  function n(o, a) {
    return /* @__PURE__ */ x.jsx(Yo, b({
      "data-testid": `${t}Icon`,
      ref: a
    }, o, {
      children: e
    }));
  }
  return process.env.NODE_ENV !== "production" && (n.displayName = `${t}Icon`), n.muiName = Yo.muiName, /* @__PURE__ */ g.memo(/* @__PURE__ */ g.forwardRef(n));
}
const yx = {
  configure: (e) => {
    process.env.NODE_ENV !== "production" && console.warn(["MUI: `ClassNameGenerator` import from `@mui/material/utils` is outdated and might cause unexpected issues.", "", "You should use `import { unstable_ClassNameGenerator } from '@mui/material/className'` instead", "", "The detail of the issue: https://github.com/mui/material-ui/issues/30011#issuecomment-1024993401", "", "The updated documentation: https://mui.com/guides/classname-generator/"].join(`
`)), Tl.configure(e);
  }
}, vx = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  capitalize: de,
  createChainedFunction: Ai,
  createSvgIcon: rn,
  debounce: yl,
  deprecatedPropType: Ib,
  isMuiElement: Br,
  ownerDocument: dt,
  ownerWindow: Fn,
  requirePropFactory: Nb,
  setRef: Ka,
  unstable_ClassNameGenerator: yx,
  unstable_useEnhancedEffect: ft,
  unstable_useId: Bn,
  unsupportedProp: Ip,
  useControlled: Ht,
  useEventCallback: we,
  useForkRef: Ke,
  useIsFocusVisible: vl
}, Symbol.toStringTag, { value: "Module" })), xx = /* @__PURE__ */ On(vx);
var Au;
function po() {
  return Au || (Au = 1, function(e) {
    "use client";
    Object.defineProperty(e, "__esModule", {
      value: !0
    }), Object.defineProperty(e, "default", {
      enumerable: !0,
      get: function() {
        return t.createSvgIcon;
      }
    });
    var t = xx;
  }(ii)), ii;
}
var Tx = Ln;
Object.defineProperty(hl, "__esModule", {
  value: !0
});
var Sf = hl.default = void 0, wx = Tx(po()), Ex = x;
Sf = hl.default = (0, wx.default)(/* @__PURE__ */ (0, Ex.jsx)("path", {
  d: "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6z"
}), "Add");
function Pf({
  styles: e,
  themeId: t,
  defaultTheme: n = {}
}) {
  const o = lo(n), a = typeof e == "function" ? e(t && o[t] || o) : e;
  return /* @__PURE__ */ x.jsx(Ml, {
    styles: a
  });
}
process.env.NODE_ENV !== "production" && (Pf.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │ To update them, edit the TypeScript types and run `pnpm proptypes`. │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * @ignore
   */
  defaultTheme: r.object,
  /**
   * @ignore
   */
  styles: r.oneOfType([r.array, r.func, r.number, r.object, r.string, r.bool]),
  /**
   * @ignore
   */
  themeId: r.string
});
const Cx = ["className", "component"];
function Ox(e = {}) {
  const {
    themeId: t,
    defaultTheme: n,
    defaultClassName: o = "MuiBox-root",
    generateClassName: a
  } = e, s = pf("div", {
    shouldForwardProp: (l) => l !== "theme" && l !== "sx" && l !== "as"
  })(ia);
  return /* @__PURE__ */ g.forwardRef(function(c, u) {
    const d = lo(n), f = Il(c), {
      className: p,
      component: m = "div"
    } = f, v = ie(f, Cx);
    return /* @__PURE__ */ x.jsx(s, b({
      as: m,
      ref: u,
      className: pe(p, a ? a(o) : o),
      theme: t && d[t] || d
    }, v));
  });
}
function Sx(e, t, n, o, a) {
  const [s, i] = g.useState(() => a && n ? n(e).matches : o ? o(e).matches : t);
  return ft(() => {
    let l = !0;
    if (!n)
      return;
    const c = n(e), u = () => {
      l && i(c.matches);
    };
    return u(), c.addListener(u), () => {
      l = !1, c.removeListener(u);
    };
  }, [e, n]), s;
}
const Rf = g.useSyncExternalStore;
function Px(e, t, n, o, a) {
  const s = g.useCallback(() => t, [t]), i = g.useMemo(() => {
    if (a && n)
      return () => n(e).matches;
    if (o !== null) {
      const {
        matches: d
      } = o(e);
      return () => d;
    }
    return s;
  }, [s, e, o, a, n]), [l, c] = g.useMemo(() => {
    if (n === null)
      return [s, () => () => {
      }];
    const d = n(e);
    return [() => d.matches, (f) => (d.addListener(f), () => {
      d.removeListener(f);
    })];
  }, [s, n, e]);
  return Rf(c, l, i);
}
function Df(e, t = {}) {
  const n = ff(), o = typeof window < "u" && typeof window.matchMedia < "u", {
    defaultMatches: a = !1,
    matchMedia: s = o ? window.matchMedia : null,
    ssrMatchMedia: i = null,
    noSsr: l = !1
  } = zp({
    name: "MuiUseMediaQuery",
    props: t,
    theme: n
  });
  process.env.NODE_ENV !== "production" && typeof e == "function" && n === null && console.error(["MUI: The `query` argument provided is invalid.", "You are providing a function without a theme in the context.", "One of the parent elements needs to use a ThemeProvider."].join(`
`));
  let c = typeof e == "function" ? e(n) : e;
  c = c.replace(/^@media( ?)/m, "");
  const d = (Rf !== void 0 ? Px : Sx)(c, a, s, i, l);
  return process.env.NODE_ENV !== "production" && g.useDebugValue({
    query: c,
    match: d
  }), d;
}
function Rx(e, t = 0, n = 1) {
  return process.env.NODE_ENV !== "production" && (e < t || e > n) && console.error(`MUI: The value provided ${e} is out of range [${t}, ${n}].`), Lp(e, t, n);
}
function Dx(e) {
  e = e.slice(1);
  const t = new RegExp(`.{1,${e.length >= 6 ? 2 : 1}}`, "g");
  let n = e.match(t);
  return n && n[0].length === 1 && (n = n.map((o) => o + o)), n ? `rgb${n.length === 4 ? "a" : ""}(${n.map((o, a) => a < 3 ? parseInt(o, 16) : Math.round(parseInt(o, 16) / 255 * 1e3) / 1e3).join(", ")})` : "";
}
function $f(e) {
  if (e.type)
    return e;
  if (e.charAt(0) === "#")
    return $f(Dx(e));
  const t = e.indexOf("("), n = e.substring(0, t);
  if (["rgb", "rgba", "hsl", "hsla", "color"].indexOf(n) === -1)
    throw new Error(process.env.NODE_ENV !== "production" ? `MUI: Unsupported \`${e}\` color.
The following formats are supported: #nnn, #nnnnnn, rgb(), rgba(), hsl(), hsla(), color().` : xn(9, e));
  let o = e.substring(t + 1, e.length - 1), a;
  if (n === "color") {
    if (o = o.split(" "), a = o.shift(), o.length === 4 && o[3].charAt(0) === "/" && (o[3] = o[3].slice(1)), ["srgb", "display-p3", "a98-rgb", "prophoto-rgb", "rec-2020"].indexOf(a) === -1)
      throw new Error(process.env.NODE_ENV !== "production" ? `MUI: unsupported \`${a}\` color space.
The following color spaces are supported: srgb, display-p3, a98-rgb, prophoto-rgb, rec-2020.` : xn(10, a));
  } else
    o = o.split(",");
  return o = o.map((s) => parseFloat(s)), {
    type: n,
    values: o,
    colorSpace: a
  };
}
function $x(e) {
  const {
    type: t,
    colorSpace: n
  } = e;
  let {
    values: o
  } = e;
  return t.indexOf("rgb") !== -1 ? o = o.map((a, s) => s < 3 ? parseInt(a, 10) : a) : t.indexOf("hsl") !== -1 && (o[1] = `${o[1]}%`, o[2] = `${o[2]}%`), t.indexOf("color") !== -1 ? o = `${n} ${o.join(" ")}` : o = `${o.join(", ")}`, `${t}(${o})`;
}
function Xr(e, t) {
  return e = $f(e), t = Rx(t), (e.type === "rgb" || e.type === "hsl") && (e.type += "a"), e.type === "color" ? e.values[3] = `/${t}` : e.values[3] = t, $x(e);
}
const kx = /* @__PURE__ */ g.createContext();
process.env.NODE_ENV !== "production" && (r.node, r.bool);
const kf = () => {
  const e = g.useContext(kx);
  return e ?? !1;
};
function Zt() {
  const e = lo(_s);
  return process.env.NODE_ENV !== "production" && g.useDebugValue(e), e[ca] || e;
}
const Fu = (e) => {
  let t;
  return e < 1 ? t = 5.11916 * e ** 2 : t = 4.5 * Math.log(e + 1) + 2, (t / 100).toFixed(2);
};
function Hi(e, t) {
  return Hi = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function(n, o) {
    return n.__proto__ = o, n;
  }, Hi(e, t);
}
function Fl(e, t) {
  e.prototype = Object.create(t.prototype), e.prototype.constructor = e, Hi(e, t);
}
function _x(e, t) {
  return e.classList ? !!t && e.classList.contains(t) : (" " + (e.className.baseVal || e.className) + " ").indexOf(" " + t + " ") !== -1;
}
function Mx(e, t) {
  e.classList ? e.classList.add(t) : _x(e, t) || (typeof e.className == "string" ? e.className = e.className + " " + t : e.setAttribute("class", (e.className && e.className.baseVal || "") + " " + t));
}
function Vu(e, t) {
  return e.replace(new RegExp("(^|\\s)" + t + "(?:\\s|$)", "g"), "$1").replace(/\s+/g, " ").replace(/^\s*|\s*$/g, "");
}
function Ix(e, t) {
  e.classList ? e.classList.remove(t) : typeof e.className == "string" ? e.className = Vu(e.className, t) : e.setAttribute("class", Vu(e.className && e.className.baseVal || "", t));
}
const Lu = {
  disabled: !1
};
var Nx = process.env.NODE_ENV !== "production" ? r.oneOfType([r.number, r.shape({
  enter: r.number,
  exit: r.number,
  appear: r.number
}).isRequired]) : null, jx = process.env.NODE_ENV !== "production" ? r.oneOfType([r.string, r.shape({
  enter: r.string,
  exit: r.string,
  active: r.string
}), r.shape({
  enter: r.string,
  enterDone: r.string,
  enterActive: r.string,
  exit: r.string,
  exitDone: r.string,
  exitActive: r.string
})]) : null;
const Qa = Ct.createContext(null);
var _f = function(t) {
  return t.scrollTop;
}, ko = "unmounted", Qn = "exited", er = "entering", Ir = "entered", qi = "exiting", Jt = /* @__PURE__ */ function(e) {
  Fl(t, e);
  function t(o, a) {
    var s;
    s = e.call(this, o, a) || this;
    var i = a, l = i && !i.isMounting ? o.enter : o.appear, c;
    return s.appearStatus = null, o.in ? l ? (c = Qn, s.appearStatus = er) : c = Ir : o.unmountOnExit || o.mountOnEnter ? c = ko : c = Qn, s.state = {
      status: c
    }, s.nextCallback = null, s;
  }
  t.getDerivedStateFromProps = function(a, s) {
    var i = a.in;
    return i && s.status === ko ? {
      status: Qn
    } : null;
  };
  var n = t.prototype;
  return n.componentDidMount = function() {
    this.updateStatus(!0, this.appearStatus);
  }, n.componentDidUpdate = function(a) {
    var s = null;
    if (a !== this.props) {
      var i = this.state.status;
      this.props.in ? i !== er && i !== Ir && (s = er) : (i === er || i === Ir) && (s = qi);
    }
    this.updateStatus(!1, s);
  }, n.componentWillUnmount = function() {
    this.cancelNextCallback();
  }, n.getTimeouts = function() {
    var a = this.props.timeout, s, i, l;
    return s = i = l = a, a != null && typeof a != "number" && (s = a.exit, i = a.enter, l = a.appear !== void 0 ? a.appear : i), {
      exit: s,
      enter: i,
      appear: l
    };
  }, n.updateStatus = function(a, s) {
    if (a === void 0 && (a = !1), s !== null)
      if (this.cancelNextCallback(), s === er) {
        if (this.props.unmountOnExit || this.props.mountOnEnter) {
          var i = this.props.nodeRef ? this.props.nodeRef.current : Ea.findDOMNode(this);
          i && _f(i);
        }
        this.performEnter(a);
      } else
        this.performExit();
    else this.props.unmountOnExit && this.state.status === Qn && this.setState({
      status: ko
    });
  }, n.performEnter = function(a) {
    var s = this, i = this.props.enter, l = this.context ? this.context.isMounting : a, c = this.props.nodeRef ? [l] : [Ea.findDOMNode(this), l], u = c[0], d = c[1], f = this.getTimeouts(), p = l ? f.appear : f.enter;
    if (!a && !i || Lu.disabled) {
      this.safeSetState({
        status: Ir
      }, function() {
        s.props.onEntered(u);
      });
      return;
    }
    this.props.onEnter(u, d), this.safeSetState({
      status: er
    }, function() {
      s.props.onEntering(u, d), s.onTransitionEnd(p, function() {
        s.safeSetState({
          status: Ir
        }, function() {
          s.props.onEntered(u, d);
        });
      });
    });
  }, n.performExit = function() {
    var a = this, s = this.props.exit, i = this.getTimeouts(), l = this.props.nodeRef ? void 0 : Ea.findDOMNode(this);
    if (!s || Lu.disabled) {
      this.safeSetState({
        status: Qn
      }, function() {
        a.props.onExited(l);
      });
      return;
    }
    this.props.onExit(l), this.safeSetState({
      status: qi
    }, function() {
      a.props.onExiting(l), a.onTransitionEnd(i.exit, function() {
        a.safeSetState({
          status: Qn
        }, function() {
          a.props.onExited(l);
        });
      });
    });
  }, n.cancelNextCallback = function() {
    this.nextCallback !== null && (this.nextCallback.cancel(), this.nextCallback = null);
  }, n.safeSetState = function(a, s) {
    s = this.setNextCallback(s), this.setState(a, s);
  }, n.setNextCallback = function(a) {
    var s = this, i = !0;
    return this.nextCallback = function(l) {
      i && (i = !1, s.nextCallback = null, a(l));
    }, this.nextCallback.cancel = function() {
      i = !1;
    }, this.nextCallback;
  }, n.onTransitionEnd = function(a, s) {
    this.setNextCallback(s);
    var i = this.props.nodeRef ? this.props.nodeRef.current : Ea.findDOMNode(this), l = a == null && !this.props.addEndListener;
    if (!i || l) {
      setTimeout(this.nextCallback, 0);
      return;
    }
    if (this.props.addEndListener) {
      var c = this.props.nodeRef ? [this.nextCallback] : [i, this.nextCallback], u = c[0], d = c[1];
      this.props.addEndListener(u, d);
    }
    a != null && setTimeout(this.nextCallback, a);
  }, n.render = function() {
    var a = this.state.status;
    if (a === ko)
      return null;
    var s = this.props, i = s.children;
    s.in, s.mountOnEnter, s.unmountOnExit, s.appear, s.enter, s.exit, s.timeout, s.addEndListener, s.onEnter, s.onEntering, s.onEntered, s.onExit, s.onExiting, s.onExited, s.nodeRef;
    var l = ie(s, ["children", "in", "mountOnEnter", "unmountOnExit", "appear", "enter", "exit", "timeout", "addEndListener", "onEnter", "onEntering", "onEntered", "onExit", "onExiting", "onExited", "nodeRef"]);
    return (
      // allows for nested Transitions
      /* @__PURE__ */ Ct.createElement(Qa.Provider, {
        value: null
      }, typeof i == "function" ? i(a, l) : Ct.cloneElement(Ct.Children.only(i), l))
    );
  }, t;
}(Ct.Component);
Jt.contextType = Qa;
Jt.propTypes = process.env.NODE_ENV !== "production" ? {
  /**
   * A React reference to DOM element that need to transition:
   * https://stackoverflow.com/a/51127130/4671932
   *
   *   - When `nodeRef` prop is used, `node` is not passed to callback functions
   *      (e.g. `onEnter`) because user already has direct access to the node.
   *   - When changing `key` prop of `Transition` in a `TransitionGroup` a new
   *     `nodeRef` need to be provided to `Transition` with changed `key` prop
   *     (see
   *     [test/CSSTransition-test.js](https://github.com/reactjs/react-transition-group/blob/13435f897b3ab71f6e19d724f145596f5910581c/test/CSSTransition-test.js#L362-L437)).
   */
  nodeRef: r.shape({
    current: typeof Element > "u" ? r.any : function(e, t, n, o, a, s) {
      var i = e[t];
      return r.instanceOf(i && "ownerDocument" in i ? i.ownerDocument.defaultView.Element : Element)(e, t, n, o, a, s);
    }
  }),
  /**
   * A `function` child can be used instead of a React element. This function is
   * called with the current transition status (`'entering'`, `'entered'`,
   * `'exiting'`, `'exited'`), which can be used to apply context
   * specific props to a component.
   *
   * ```jsx
   * <Transition in={this.state.in} timeout={150}>
   *   {state => (
   *     <MyComponent className={`fade fade-${state}`} />
   *   )}
   * </Transition>
   * ```
   */
  children: r.oneOfType([r.func.isRequired, r.element.isRequired]).isRequired,
  /**
   * Show the component; triggers the enter or exit states
   */
  in: r.bool,
  /**
   * By default the child component is mounted immediately along with
   * the parent `Transition` component. If you want to "lazy mount" the component on the
   * first `in={true}` you can set `mountOnEnter`. After the first enter transition the component will stay
   * mounted, even on "exited", unless you also specify `unmountOnExit`.
   */
  mountOnEnter: r.bool,
  /**
   * By default the child component stays mounted after it reaches the `'exited'` state.
   * Set `unmountOnExit` if you'd prefer to unmount the component after it finishes exiting.
   */
  unmountOnExit: r.bool,
  /**
   * By default the child component does not perform the enter transition when
   * it first mounts, regardless of the value of `in`. If you want this
   * behavior, set both `appear` and `in` to `true`.
   *
   * > **Note**: there are no special appear states like `appearing`/`appeared`, this prop
   * > only adds an additional enter transition. However, in the
   * > `<CSSTransition>` component that first enter transition does result in
   * > additional `.appear-*` classes, that way you can choose to style it
   * > differently.
   */
  appear: r.bool,
  /**
   * Enable or disable enter transitions.
   */
  enter: r.bool,
  /**
   * Enable or disable exit transitions.
   */
  exit: r.bool,
  /**
   * The duration of the transition, in milliseconds.
   * Required unless `addEndListener` is provided.
   *
   * You may specify a single timeout for all transitions:
   *
   * ```jsx
   * timeout={500}
   * ```
   *
   * or individually:
   *
   * ```jsx
   * timeout={{
   *  appear: 500,
   *  enter: 300,
   *  exit: 500,
   * }}
   * ```
   *
   * - `appear` defaults to the value of `enter`
   * - `enter` defaults to `0`
   * - `exit` defaults to `0`
   *
   * @type {number | { enter?: number, exit?: number, appear?: number }}
   */
  timeout: function(t) {
    var n = Nx;
    t.addEndListener || (n = n.isRequired);
    for (var o = arguments.length, a = new Array(o > 1 ? o - 1 : 0), s = 1; s < o; s++)
      a[s - 1] = arguments[s];
    return n.apply(void 0, [t].concat(a));
  },
  /**
   * Add a custom transition end trigger. Called with the transitioning
   * DOM node and a `done` callback. Allows for more fine grained transition end
   * logic. Timeouts are still used as a fallback if provided.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * ```jsx
   * addEndListener={(node, done) => {
   *   // use the css transitionend event to mark the finish of a transition
   *   node.addEventListener('transitionend', done, false);
   * }}
   * ```
   */
  addEndListener: r.func,
  /**
   * Callback fired before the "entering" status is applied. An extra parameter
   * `isAppearing` is supplied to indicate if the enter stage is occurring on the initial mount
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * @type Function(node: HtmlElement, isAppearing: bool) -> void
   */
  onEnter: r.func,
  /**
   * Callback fired after the "entering" status is applied. An extra parameter
   * `isAppearing` is supplied to indicate if the enter stage is occurring on the initial mount
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * @type Function(node: HtmlElement, isAppearing: bool)
   */
  onEntering: r.func,
  /**
   * Callback fired after the "entered" status is applied. An extra parameter
   * `isAppearing` is supplied to indicate if the enter stage is occurring on the initial mount
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * @type Function(node: HtmlElement, isAppearing: bool) -> void
   */
  onEntered: r.func,
  /**
   * Callback fired before the "exiting" status is applied.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * @type Function(node: HtmlElement) -> void
   */
  onExit: r.func,
  /**
   * Callback fired after the "exiting" status is applied.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * @type Function(node: HtmlElement) -> void
   */
  onExiting: r.func,
  /**
   * Callback fired after the "exited" status is applied.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed
   *
   * @type Function(node: HtmlElement) -> void
   */
  onExited: r.func
} : {};
function kr() {
}
Jt.defaultProps = {
  in: !1,
  mountOnEnter: !1,
  unmountOnExit: !1,
  appear: !1,
  enter: !0,
  exit: !0,
  onEnter: kr,
  onEntering: kr,
  onEntered: kr,
  onExit: kr,
  onExiting: kr,
  onExited: kr
};
Jt.UNMOUNTED = ko;
Jt.EXITED = Qn;
Jt.ENTERING = er;
Jt.ENTERED = Ir;
Jt.EXITING = qi;
var Ax = function(t, n) {
  return t && n && n.split(" ").forEach(function(o) {
    return Mx(t, o);
  });
}, xi = function(t, n) {
  return t && n && n.split(" ").forEach(function(o) {
    return Ix(t, o);
  });
}, Vl = /* @__PURE__ */ function(e) {
  Fl(t, e);
  function t() {
    for (var o, a = arguments.length, s = new Array(a), i = 0; i < a; i++)
      s[i] = arguments[i];
    return o = e.call.apply(e, [this].concat(s)) || this, o.appliedClasses = {
      appear: {},
      enter: {},
      exit: {}
    }, o.onEnter = function(l, c) {
      var u = o.resolveArguments(l, c), d = u[0], f = u[1];
      o.removeClasses(d, "exit"), o.addClass(d, f ? "appear" : "enter", "base"), o.props.onEnter && o.props.onEnter(l, c);
    }, o.onEntering = function(l, c) {
      var u = o.resolveArguments(l, c), d = u[0], f = u[1], p = f ? "appear" : "enter";
      o.addClass(d, p, "active"), o.props.onEntering && o.props.onEntering(l, c);
    }, o.onEntered = function(l, c) {
      var u = o.resolveArguments(l, c), d = u[0], f = u[1], p = f ? "appear" : "enter";
      o.removeClasses(d, p), o.addClass(d, p, "done"), o.props.onEntered && o.props.onEntered(l, c);
    }, o.onExit = function(l) {
      var c = o.resolveArguments(l), u = c[0];
      o.removeClasses(u, "appear"), o.removeClasses(u, "enter"), o.addClass(u, "exit", "base"), o.props.onExit && o.props.onExit(l);
    }, o.onExiting = function(l) {
      var c = o.resolveArguments(l), u = c[0];
      o.addClass(u, "exit", "active"), o.props.onExiting && o.props.onExiting(l);
    }, o.onExited = function(l) {
      var c = o.resolveArguments(l), u = c[0];
      o.removeClasses(u, "exit"), o.addClass(u, "exit", "done"), o.props.onExited && o.props.onExited(l);
    }, o.resolveArguments = function(l, c) {
      return o.props.nodeRef ? [o.props.nodeRef.current, l] : [l, c];
    }, o.getClassNames = function(l) {
      var c = o.props.classNames, u = typeof c == "string", d = u && c ? c + "-" : "", f = u ? "" + d + l : c[l], p = u ? f + "-active" : c[l + "Active"], m = u ? f + "-done" : c[l + "Done"];
      return {
        baseClassName: f,
        activeClassName: p,
        doneClassName: m
      };
    }, o;
  }
  var n = t.prototype;
  return n.addClass = function(a, s, i) {
    var l = this.getClassNames(s)[i + "ClassName"], c = this.getClassNames("enter"), u = c.doneClassName;
    s === "appear" && i === "done" && u && (l += " " + u), i === "active" && a && _f(a), l && (this.appliedClasses[s][i] = l, Ax(a, l));
  }, n.removeClasses = function(a, s) {
    var i = this.appliedClasses[s], l = i.base, c = i.active, u = i.done;
    this.appliedClasses[s] = {}, l && xi(a, l), c && xi(a, c), u && xi(a, u);
  }, n.render = function() {
    var a = this.props;
    a.classNames;
    var s = ie(a, ["classNames"]);
    return /* @__PURE__ */ Ct.createElement(Jt, b({}, s, {
      onEnter: this.onEnter,
      onEntered: this.onEntered,
      onEntering: this.onEntering,
      onExit: this.onExit,
      onExiting: this.onExiting,
      onExited: this.onExited
    }));
  }, t;
}(Ct.Component);
Vl.defaultProps = {
  classNames: ""
};
Vl.propTypes = process.env.NODE_ENV !== "production" ? b({}, Jt.propTypes, {
  /**
   * The animation classNames applied to the component as it appears, enters,
   * exits or has finished the transition. A single name can be provided, which
   * will be suffixed for each stage, e.g. `classNames="fade"` applies:
   *
   * - `fade-appear`, `fade-appear-active`, `fade-appear-done`
   * - `fade-enter`, `fade-enter-active`, `fade-enter-done`
   * - `fade-exit`, `fade-exit-active`, `fade-exit-done`
   *
   * A few details to note about how these classes are applied:
   *
   * 1. They are _joined_ with the ones that are already defined on the child
   *    component, so if you want to add some base styles, you can use
   *    `className` without worrying that it will be overridden.
   *
   * 2. If the transition component mounts with `in={false}`, no classes are
   *    applied yet. You might be expecting `*-exit-done`, but if you think
   *    about it, a component cannot finish exiting if it hasn't entered yet.
   *
   * 2. `fade-appear-done` and `fade-enter-done` will _both_ be applied. This
   *    allows you to define different behavior for when appearing is done and
   *    when regular entering is done, using selectors like
   *    `.fade-enter-done:not(.fade-appear-done)`. For example, you could apply
   *    an epic entrance animation when element first appears in the DOM using
   *    [Animate.css](https://daneden.github.io/animate.css/). Otherwise you can
   *    simply use `fade-enter-done` for defining both cases.
   *
   * Each individual classNames can also be specified independently like:
   *
   * ```js
   * classNames={{
   *  appear: 'my-appear',
   *  appearActive: 'my-active-appear',
   *  appearDone: 'my-done-appear',
   *  enter: 'my-enter',
   *  enterActive: 'my-active-enter',
   *  enterDone: 'my-done-enter',
   *  exit: 'my-exit',
   *  exitActive: 'my-active-exit',
   *  exitDone: 'my-done-exit',
   * }}
   * ```
   *
   * If you want to set these classes using CSS Modules:
   *
   * ```js
   * import styles from './styles.css';
   * ```
   *
   * you might want to use camelCase in your CSS file, that way could simply
   * spread them instead of listing them one by one:
   *
   * ```js
   * classNames={{ ...styles }}
   * ```
   *
   * @type {string | {
   *  appear?: string,
   *  appearActive?: string,
   *  appearDone?: string,
   *  enter?: string,
   *  enterActive?: string,
   *  enterDone?: string,
   *  exit?: string,
   *  exitActive?: string,
   *  exitDone?: string,
   * }}
   */
  classNames: jx,
  /**
   * A `<Transition>` callback fired immediately after the 'enter' or 'appear' class is
   * applied.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * @type Function(node: HtmlElement, isAppearing: bool)
   */
  onEnter: r.func,
  /**
   * A `<Transition>` callback fired immediately after the 'enter-active' or
   * 'appear-active' class is applied.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * @type Function(node: HtmlElement, isAppearing: bool)
   */
  onEntering: r.func,
  /**
   * A `<Transition>` callback fired immediately after the 'enter' or
   * 'appear' classes are **removed** and the `done` class is added to the DOM node.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed.
   *
   * @type Function(node: HtmlElement, isAppearing: bool)
   */
  onEntered: r.func,
  /**
   * A `<Transition>` callback fired immediately after the 'exit' class is
   * applied.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed
   *
   * @type Function(node: HtmlElement)
   */
  onExit: r.func,
  /**
   * A `<Transition>` callback fired immediately after the 'exit-active' is applied.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed
   *
   * @type Function(node: HtmlElement)
   */
  onExiting: r.func,
  /**
   * A `<Transition>` callback fired immediately after the 'exit' classes
   * are **removed** and the `exit-done` class is added to the DOM node.
   *
   * **Note**: when `nodeRef` prop is passed, `node` is not passed
   *
   * @type Function(node: HtmlElement)
   */
  onExited: r.func
}) : {};
function Fx(e) {
  if (e === void 0) throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
  return e;
}
function Ll(e, t) {
  var n = function(s) {
    return t && Aa(s) ? t(s) : s;
  }, o = /* @__PURE__ */ Object.create(null);
  return e && ml.map(e, function(a) {
    return a;
  }).forEach(function(a) {
    o[a.key] = n(a);
  }), o;
}
function Vx(e, t) {
  e = e || {}, t = t || {};
  function n(d) {
    return d in t ? t[d] : e[d];
  }
  var o = /* @__PURE__ */ Object.create(null), a = [];
  for (var s in e)
    s in t ? a.length && (o[s] = a, a = []) : a.push(s);
  var i, l = {};
  for (var c in t) {
    if (o[c])
      for (i = 0; i < o[c].length; i++) {
        var u = o[c][i];
        l[o[c][i]] = n(u);
      }
    l[c] = n(c);
  }
  for (i = 0; i < a.length; i++)
    l[a[i]] = n(a[i]);
  return l;
}
function nr(e, t, n) {
  return n[t] != null ? n[t] : e.props[t];
}
function Lx(e, t) {
  return Ll(e.children, function(n) {
    return Fa(n, {
      onExited: t.bind(null, n),
      in: !0,
      appear: nr(n, "appear", e),
      enter: nr(n, "enter", e),
      exit: nr(n, "exit", e)
    });
  });
}
function Bx(e, t, n) {
  var o = Ll(e.children), a = Vx(t, o);
  return Object.keys(a).forEach(function(s) {
    var i = a[s];
    if (Aa(i)) {
      var l = s in t, c = s in o, u = t[s], d = Aa(u) && !u.props.in;
      c && (!l || d) ? a[s] = Fa(i, {
        onExited: n.bind(null, i),
        in: !0,
        exit: nr(i, "exit", e),
        enter: nr(i, "enter", e)
      }) : !c && l && !d ? a[s] = Fa(i, {
        in: !1
      }) : c && l && Aa(u) && (a[s] = Fa(i, {
        onExited: n.bind(null, i),
        in: u.props.in,
        exit: nr(i, "exit", e),
        enter: nr(i, "enter", e)
      }));
    }
  }), a;
}
var zx = Object.values || function(e) {
  return Object.keys(e).map(function(t) {
    return e[t];
  });
}, Wx = {
  component: "div",
  childFactory: function(t) {
    return t;
  }
}, da = /* @__PURE__ */ function(e) {
  Fl(t, e);
  function t(o, a) {
    var s;
    s = e.call(this, o, a) || this;
    var i = s.handleExited.bind(Fx(s));
    return s.state = {
      contextValue: {
        isMounting: !0
      },
      handleExited: i,
      firstRender: !0
    }, s;
  }
  var n = t.prototype;
  return n.componentDidMount = function() {
    this.mounted = !0, this.setState({
      contextValue: {
        isMounting: !1
      }
    });
  }, n.componentWillUnmount = function() {
    this.mounted = !1;
  }, t.getDerivedStateFromProps = function(a, s) {
    var i = s.children, l = s.handleExited, c = s.firstRender;
    return {
      children: c ? Lx(a, l) : Bx(a, i, l),
      firstRender: !1
    };
  }, n.handleExited = function(a, s) {
    var i = Ll(this.props.children);
    a.key in i || (a.props.onExited && a.props.onExited(s), this.mounted && this.setState(function(l) {
      var c = b({}, l.children);
      return delete c[a.key], {
        children: c
      };
    }));
  }, n.render = function() {
    var a = this.props, s = a.component, i = a.childFactory, l = ie(a, ["component", "childFactory"]), c = this.state.contextValue, u = zx(this.state.children).map(i);
    return delete l.appear, delete l.enter, delete l.exit, s === null ? /* @__PURE__ */ Ct.createElement(Qa.Provider, {
      value: c
    }, u) : /* @__PURE__ */ Ct.createElement(Qa.Provider, {
      value: c
    }, /* @__PURE__ */ Ct.createElement(s, l, u));
  }, t;
}(Ct.Component);
da.propTypes = process.env.NODE_ENV !== "production" ? {
  /**
   * `<TransitionGroup>` renders a `<div>` by default. You can change this
   * behavior by providing a `component` prop.
   * If you use React v16+ and would like to avoid a wrapping `<div>` element
   * you can pass in `component={null}`. This is useful if the wrapping div
   * borks your css styles.
   */
  component: r.any,
  /**
   * A set of `<Transition>` components, that are toggled `in` and out as they
   * leave. the `<TransitionGroup>` will inject specific transition props, so
   * remember to spread them through if you are wrapping the `<Transition>` as
   * with our `<Fade>` example.
   *
   * While this component is meant for multiple `Transition` or `CSSTransition`
   * children, sometimes you may want to have a single transition child with
   * content that you want to be transitioned out and in when you change it
   * (e.g. routes, images etc.) In that case you can change the `key` prop of
   * the transition child as you change its content, this will cause
   * `TransitionGroup` to transition the child out and back in.
   */
  children: r.node,
  /**
   * A convenience prop that enables or disables appear animations
   * for all children. Note that specifying this will override any defaults set
   * on individual children Transitions.
   */
  appear: r.bool,
  /**
   * A convenience prop that enables or disables enter animations
   * for all children. Note that specifying this will override any defaults set
   * on individual children Transitions.
   */
  enter: r.bool,
  /**
   * A convenience prop that enables or disables exit animations
   * for all children. Note that specifying this will override any defaults set
   * on individual children Transitions.
   */
  exit: r.bool,
  /**
   * You may need to apply reactive updates to a child as it is exiting.
   * This is generally done by using `cloneElement` however in the case of an exiting
   * child the element has already been removed and not accessible to the consumer.
   *
   * If you do need to update a child as it leaves you can provide a `childFactory`
   * to wrap every child, even the ones that are leaving.
   *
   * @type Function(child: ReactElement) -> ReactElement
   */
  childFactory: r.func
} : {};
da.defaultProps = Wx;
const Mf = (e) => e.scrollTop;
function es(e, t) {
  var n, o;
  const {
    timeout: a,
    easing: s,
    style: i = {}
  } = e;
  return {
    duration: (n = i.transitionDuration) != null ? n : typeof a == "number" ? a : a[t.mode] || 0,
    easing: (o = i.transitionTimingFunction) != null ? o : typeof s == "object" ? s[t.mode] : s,
    delay: i.transitionDelay
  };
}
function Ux(e) {
  return Pe("MuiPaper", e);
}
Ce("MuiPaper", ["root", "rounded", "outlined", "elevation", "elevation0", "elevation1", "elevation2", "elevation3", "elevation4", "elevation5", "elevation6", "elevation7", "elevation8", "elevation9", "elevation10", "elevation11", "elevation12", "elevation13", "elevation14", "elevation15", "elevation16", "elevation17", "elevation18", "elevation19", "elevation20", "elevation21", "elevation22", "elevation23", "elevation24"]);
const Hx = ["className", "component", "elevation", "square", "variant"], qx = (e) => {
  const {
    square: t,
    elevation: n,
    variant: o,
    classes: a
  } = e, s = {
    root: ["root", o, !t && "rounded", o === "elevation" && `elevation${n}`]
  };
  return Se(s, Ux, a);
}, Yx = Z("div", {
  name: "MuiPaper",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, t[n.variant], !n.square && t.rounded, n.variant === "elevation" && t[`elevation${n.elevation}`]];
  }
})(({
  theme: e,
  ownerState: t
}) => {
  var n;
  return b({
    backgroundColor: (e.vars || e).palette.background.paper,
    color: (e.vars || e).palette.text.primary,
    transition: e.transitions.create("box-shadow")
  }, !t.square && {
    borderRadius: e.shape.borderRadius
  }, t.variant === "outlined" && {
    border: `1px solid ${(e.vars || e).palette.divider}`
  }, t.variant === "elevation" && b({
    boxShadow: (e.vars || e).shadows[t.elevation]
  }, !e.vars && e.palette.mode === "dark" && {
    backgroundImage: `linear-gradient(${qe("#fff", Fu(t.elevation))}, ${qe("#fff", Fu(t.elevation))})`
  }, e.vars && {
    backgroundImage: (n = e.vars.overlays) == null ? void 0 : n[t.elevation]
  }));
}), pa = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiPaper"
  }), {
    className: a,
    component: s = "div",
    elevation: i = 1,
    square: l = !1,
    variant: c = "elevation"
  } = o, u = ie(o, Hx), d = b({}, o, {
    component: s,
    elevation: i,
    square: l,
    variant: c
  }), f = qx(d);
  return process.env.NODE_ENV !== "production" && Zt().shadows[i] === void 0 && console.error([`MUI: The elevation provided <Paper elevation={${i}}> is not available in the theme.`, `Please make sure that \`theme.shadows[${i}]\` is defined.`].join(`
`)), /* @__PURE__ */ x.jsx(Yx, b({
    as: s,
    ownerState: d,
    className: pe(f.root, a),
    ref: n
  }, u));
});
process.env.NODE_ENV !== "production" && (pa.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * Shadow depth, corresponds to `dp` in the spec.
   * It accepts values between 0 and 24 inclusive.
   * @default 1
   */
  elevation: Sn(Fp, (e) => {
    const {
      elevation: t,
      variant: n
    } = e;
    return t > 0 && n === "outlined" ? new Error(`MUI: Combining \`elevation={${t}}\` with \`variant="${n}"\` has no effect. Either use \`elevation={0}\` or use a different \`variant\`.`) : null;
  }),
  /**
   * If `true`, rounded corners are disabled.
   * @default false
   */
  square: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The variant to use.
   * @default 'elevation'
   */
  variant: r.oneOfType([r.oneOf(["elevation", "outlined"]), r.string])
});
function Zr(e) {
  return typeof e == "string";
}
function _o(e, t, n) {
  return e === void 0 || Zr(e) ? t : b({}, t, {
    ownerState: b({}, t.ownerState, n)
  });
}
const Kx = {
  disableDefaultClasses: !1
}, If = /* @__PURE__ */ g.createContext(Kx);
process.env.NODE_ENV !== "production" && (If.displayName = "ClassNameConfiguratorContext");
function Gx(e) {
  const {
    disableDefaultClasses: t
  } = g.useContext(If);
  return (n) => t ? "" : e(n);
}
function Nf(e, t = []) {
  if (e === void 0)
    return {};
  const n = {};
  return Object.keys(e).filter((o) => o.match(/^on[A-Z]/) && typeof e[o] == "function" && !t.includes(o)).forEach((o) => {
    n[o] = e[o];
  }), n;
}
function Bl(e, t, n) {
  return typeof e == "function" ? e(t, n) : e;
}
function Bu(e) {
  if (e === void 0)
    return {};
  const t = {};
  return Object.keys(e).filter((n) => !(n.match(/^on[A-Z]/) && typeof e[n] == "function")).forEach((n) => {
    t[n] = e[n];
  }), t;
}
function Xx(e) {
  const {
    getSlotProps: t,
    additionalProps: n,
    externalSlotProps: o,
    externalForwardedProps: a,
    className: s
  } = e;
  if (!t) {
    const m = pe(n == null ? void 0 : n.className, s, a == null ? void 0 : a.className, o == null ? void 0 : o.className), v = b({}, n == null ? void 0 : n.style, a == null ? void 0 : a.style, o == null ? void 0 : o.style), h = b({}, n, a, o);
    return m.length > 0 && (h.className = m), Object.keys(v).length > 0 && (h.style = v), {
      props: h,
      internalRef: void 0
    };
  }
  const i = Nf(b({}, a, o)), l = Bu(o), c = Bu(a), u = t(i), d = pe(u == null ? void 0 : u.className, n == null ? void 0 : n.className, s, a == null ? void 0 : a.className, o == null ? void 0 : o.className), f = b({}, u == null ? void 0 : u.style, n == null ? void 0 : n.style, a == null ? void 0 : a.style, o == null ? void 0 : o.style), p = b({}, u, n, c, l);
  return d.length > 0 && (p.className = d), Object.keys(f).length > 0 && (p.style = f), {
    props: p,
    internalRef: u.ref
  };
}
const Zx = ["elementType", "externalSlotProps", "ownerState", "skipResolvingSlotProps"];
function Ye(e) {
  var t;
  const {
    elementType: n,
    externalSlotProps: o,
    ownerState: a,
    skipResolvingSlotProps: s = !1
  } = e, i = ie(e, Zx), l = s ? {} : Bl(o, a), {
    props: c,
    internalRef: u
  } = Xx(b({}, i, {
    externalSlotProps: l
  })), d = Ke(u, l == null ? void 0 : l.ref, (t = e.additionalProps) == null ? void 0 : t.ref);
  return _o(n, b({}, c, {
    ref: d
  }), a);
}
function jf(e) {
  const {
    className: t,
    classes: n,
    pulsate: o = !1,
    rippleX: a,
    rippleY: s,
    rippleSize: i,
    in: l,
    onExited: c,
    timeout: u
  } = e, [d, f] = g.useState(!1), p = pe(t, n.ripple, n.rippleVisible, o && n.ripplePulsate), m = {
    width: i,
    height: i,
    top: -(i / 2) + s,
    left: -(i / 2) + a
  }, v = pe(n.child, d && n.childLeaving, o && n.childPulsate);
  return !l && !d && f(!0), g.useEffect(() => {
    if (!l && c != null) {
      const h = setTimeout(c, u);
      return () => {
        clearTimeout(h);
      };
    }
  }, [c, l, u]), /* @__PURE__ */ x.jsx("span", {
    className: p,
    style: m,
    children: /* @__PURE__ */ x.jsx("span", {
      className: v
    })
  });
}
process.env.NODE_ENV !== "production" && (jf.propTypes = {
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object.isRequired,
  className: r.string,
  /**
   * @ignore - injected from TransitionGroup
   */
  in: r.bool,
  /**
   * @ignore - injected from TransitionGroup
   */
  onExited: r.func,
  /**
   * If `true`, the ripple pulsates, typically indicating the keyboard focus state of an element.
   */
  pulsate: r.bool,
  /**
   * Diameter of the ripple.
   */
  rippleSize: r.number,
  /**
   * Horizontal position of the ripple center.
   */
  rippleX: r.number,
  /**
   * Vertical position of the ripple center.
   */
  rippleY: r.number,
  /**
   * exit delay
   */
  timeout: r.number.isRequired
});
const zt = Ce("MuiTouchRipple", ["root", "ripple", "rippleVisible", "ripplePulsate", "child", "childLeaving", "childPulsate"]), Jx = ["center", "classes", "className"];
let Ms = (e) => e, zu, Wu, Uu, Hu;
const Yi = 550, Qx = 80, e0 = io(zu || (zu = Ms`
  0% {
    transform: scale(0);
    opacity: 0.1;
  }

  100% {
    transform: scale(1);
    opacity: 0.3;
  }
`)), t0 = io(Wu || (Wu = Ms`
  0% {
    opacity: 1;
  }

  100% {
    opacity: 0;
  }
`)), n0 = io(Uu || (Uu = Ms`
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(0.92);
  }

  100% {
    transform: scale(1);
  }
`)), r0 = Z("span", {
  name: "MuiTouchRipple",
  slot: "Root"
})({
  overflow: "hidden",
  pointerEvents: "none",
  position: "absolute",
  zIndex: 0,
  top: 0,
  right: 0,
  bottom: 0,
  left: 0,
  borderRadius: "inherit"
}), o0 = Z(jf, {
  name: "MuiTouchRipple",
  slot: "Ripple"
})(Hu || (Hu = Ms`
  opacity: 0;
  position: absolute;

  &.${0} {
    opacity: 0.3;
    transform: scale(1);
    animation-name: ${0};
    animation-duration: ${0}ms;
    animation-timing-function: ${0};
  }

  &.${0} {
    animation-duration: ${0}ms;
  }

  & .${0} {
    opacity: 1;
    display: block;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background-color: currentColor;
  }

  & .${0} {
    opacity: 0;
    animation-name: ${0};
    animation-duration: ${0}ms;
    animation-timing-function: ${0};
  }

  & .${0} {
    position: absolute;
    /* @noflip */
    left: 0px;
    top: 0;
    animation-name: ${0};
    animation-duration: 2500ms;
    animation-timing-function: ${0};
    animation-iteration-count: infinite;
    animation-delay: 200ms;
  }
`), zt.rippleVisible, e0, Yi, ({
  theme: e
}) => e.transitions.easing.easeInOut, zt.ripplePulsate, ({
  theme: e
}) => e.transitions.duration.shorter, zt.child, zt.childLeaving, t0, Yi, ({
  theme: e
}) => e.transitions.easing.easeInOut, zt.childPulsate, n0, ({
  theme: e
}) => e.transitions.easing.easeInOut), Af = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTouchRipple"
  }), {
    center: a = !1,
    classes: s = {},
    className: i
  } = o, l = ie(o, Jx), [c, u] = g.useState([]), d = g.useRef(0), f = g.useRef(null);
  g.useEffect(() => {
    f.current && (f.current(), f.current = null);
  }, [c]);
  const p = g.useRef(!1), m = jr(), v = g.useRef(null), h = g.useRef(null), y = g.useCallback((O) => {
    const {
      pulsate: T,
      rippleX: P,
      rippleY: S,
      rippleSize: j,
      cb: $
    } = O;
    u((V) => [...V, /* @__PURE__ */ x.jsx(o0, {
      classes: {
        ripple: pe(s.ripple, zt.ripple),
        rippleVisible: pe(s.rippleVisible, zt.rippleVisible),
        ripplePulsate: pe(s.ripplePulsate, zt.ripplePulsate),
        child: pe(s.child, zt.child),
        childLeaving: pe(s.childLeaving, zt.childLeaving),
        childPulsate: pe(s.childPulsate, zt.childPulsate)
      },
      timeout: Yi,
      pulsate: T,
      rippleX: P,
      rippleY: S,
      rippleSize: j
    }, d.current)]), d.current += 1, f.current = $;
  }, [s]), w = g.useCallback((O = {}, T = {}, P = () => {
  }) => {
    const {
      pulsate: S = !1,
      center: j = a || T.pulsate,
      fakeElement: $ = !1
      // For test purposes
    } = T;
    if ((O == null ? void 0 : O.type) === "mousedown" && p.current) {
      p.current = !1;
      return;
    }
    (O == null ? void 0 : O.type) === "touchstart" && (p.current = !0);
    const V = $ ? null : h.current, _ = V ? V.getBoundingClientRect() : {
      width: 0,
      height: 0,
      left: 0,
      top: 0
    };
    let L, M, R;
    if (j || O === void 0 || O.clientX === 0 && O.clientY === 0 || !O.clientX && !O.touches)
      L = Math.round(_.width / 2), M = Math.round(_.height / 2);
    else {
      const {
        clientX: D,
        clientY: F
      } = O.touches && O.touches.length > 0 ? O.touches[0] : O;
      L = Math.round(D - _.left), M = Math.round(F - _.top);
    }
    if (j)
      R = Math.sqrt((2 * _.width ** 2 + _.height ** 2) / 3), R % 2 === 0 && (R += 1);
    else {
      const D = Math.max(Math.abs((V ? V.clientWidth : 0) - L), L) * 2 + 2, F = Math.max(Math.abs((V ? V.clientHeight : 0) - M), M) * 2 + 2;
      R = Math.sqrt(D ** 2 + F ** 2);
    }
    O != null && O.touches ? v.current === null && (v.current = () => {
      y({
        pulsate: S,
        rippleX: L,
        rippleY: M,
        rippleSize: R,
        cb: P
      });
    }, m.start(Qx, () => {
      v.current && (v.current(), v.current = null);
    })) : y({
      pulsate: S,
      rippleX: L,
      rippleY: M,
      rippleSize: R,
      cb: P
    });
  }, [a, y, m]), C = g.useCallback(() => {
    w({}, {
      pulsate: !0
    });
  }, [w]), E = g.useCallback((O, T) => {
    if (m.clear(), (O == null ? void 0 : O.type) === "touchend" && v.current) {
      v.current(), v.current = null, m.start(0, () => {
        E(O, T);
      });
      return;
    }
    v.current = null, u((P) => P.length > 0 ? P.slice(1) : P), f.current = T;
  }, [m]);
  return g.useImperativeHandle(n, () => ({
    pulsate: C,
    start: w,
    stop: E
  }), [C, w, E]), /* @__PURE__ */ x.jsx(r0, b({
    className: pe(zt.root, s.root, i),
    ref: h
  }, l, {
    children: /* @__PURE__ */ x.jsx(da, {
      component: null,
      exit: !0,
      children: c
    })
  }));
});
process.env.NODE_ENV !== "production" && (Af.propTypes = {
  /**
   * If `true`, the ripple starts at the center of the component
   * rather than at the point of interaction.
   */
  center: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string
});
function a0(e) {
  return Pe("MuiButtonBase", e);
}
const s0 = Ce("MuiButtonBase", ["root", "disabled", "focusVisible"]), i0 = ["action", "centerRipple", "children", "className", "component", "disabled", "disableRipple", "disableTouchRipple", "focusRipple", "focusVisibleClassName", "LinkComponent", "onBlur", "onClick", "onContextMenu", "onDragLeave", "onFocus", "onFocusVisible", "onKeyDown", "onKeyUp", "onMouseDown", "onMouseLeave", "onMouseUp", "onTouchEnd", "onTouchMove", "onTouchStart", "tabIndex", "TouchRippleProps", "touchRippleRef", "type"], l0 = (e) => {
  const {
    disabled: t,
    focusVisible: n,
    focusVisibleClassName: o,
    classes: a
  } = e, i = Se({
    root: ["root", t && "disabled", n && "focusVisible"]
  }, a0, a);
  return n && o && (i.root += ` ${o}`), i;
}, c0 = Z("button", {
  name: "MuiButtonBase",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "inline-flex",
  alignItems: "center",
  justifyContent: "center",
  position: "relative",
  boxSizing: "border-box",
  WebkitTapHighlightColor: "transparent",
  backgroundColor: "transparent",
  // Reset default value
  // We disable the focus ring for mouse, touch and keyboard users.
  outline: 0,
  border: 0,
  margin: 0,
  // Remove the margin in Safari
  borderRadius: 0,
  padding: 0,
  // Remove the padding in Firefox
  cursor: "pointer",
  userSelect: "none",
  verticalAlign: "middle",
  MozAppearance: "none",
  // Reset
  WebkitAppearance: "none",
  // Reset
  textDecoration: "none",
  // So we take precedent over the style of a native <a /> element.
  color: "inherit",
  "&::-moz-focus-inner": {
    borderStyle: "none"
    // Remove Firefox dotted outline.
  },
  [`&.${s0.disabled}`]: {
    pointerEvents: "none",
    // Disable link interactions
    cursor: "default"
  },
  "@media print": {
    colorAdjust: "exact"
  }
}), lr = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiButtonBase"
  }), {
    action: a,
    centerRipple: s = !1,
    children: i,
    className: l,
    component: c = "button",
    disabled: u = !1,
    disableRipple: d = !1,
    disableTouchRipple: f = !1,
    focusRipple: p = !1,
    LinkComponent: m = "a",
    onBlur: v,
    onClick: h,
    onContextMenu: y,
    onDragLeave: w,
    onFocus: C,
    onFocusVisible: E,
    onKeyDown: O,
    onKeyUp: T,
    onMouseDown: P,
    onMouseLeave: S,
    onMouseUp: j,
    onTouchEnd: $,
    onTouchMove: V,
    onTouchStart: _,
    tabIndex: L = 0,
    TouchRippleProps: M,
    touchRippleRef: R,
    type: D
  } = o, F = ie(o, i0), z = g.useRef(null), N = g.useRef(null), q = Ke(N, R), {
    isFocusVisibleRef: A,
    onFocus: H,
    onBlur: te,
    ref: re
  } = vl(), [B, G] = g.useState(!1);
  u && B && G(!1), g.useImperativeHandle(a, () => ({
    focusVisible: () => {
      G(!0), z.current.focus();
    }
  }), []);
  const [ee, W] = g.useState(!1);
  g.useEffect(() => {
    W(!0);
  }, []);
  const J = ee && !d && !u;
  g.useEffect(() => {
    B && p && !d && ee && N.current.pulsate();
  }, [d, p, B, ee]);
  function se(be, _e, st = f) {
    return we((rt) => (_e && _e(rt), !st && N.current && N.current[be](rt), !0));
  }
  const le = se("start", P), X = se("stop", y), U = se("stop", w), K = se("stop", j), Y = se("stop", (be) => {
    B && be.preventDefault(), S && S(be);
  }), he = se("start", _), Oe = se("stop", $), Ne = se("stop", V), fe = se("stop", (be) => {
    te(be), A.current === !1 && G(!1), v && v(be);
  }, !1), ve = we((be) => {
    z.current || (z.current = be.currentTarget), H(be), A.current === !0 && (G(!0), E && E(be)), C && C(be);
  }), oe = () => {
    const be = z.current;
    return c && c !== "button" && !(be.tagName === "A" && be.href);
  }, ce = g.useRef(!1), I = we((be) => {
    p && !ce.current && B && N.current && be.key === " " && (ce.current = !0, N.current.stop(be, () => {
      N.current.start(be);
    })), be.target === be.currentTarget && oe() && be.key === " " && be.preventDefault(), O && O(be), be.target === be.currentTarget && oe() && be.key === "Enter" && !u && (be.preventDefault(), h && h(be));
  }), Q = we((be) => {
    p && be.key === " " && N.current && B && !be.defaultPrevented && (ce.current = !1, N.current.stop(be, () => {
      N.current.pulsate(be);
    })), T && T(be), h && be.target === be.currentTarget && oe() && be.key === " " && !be.defaultPrevented && h(be);
  });
  let ne = c;
  ne === "button" && (F.href || F.to) && (ne = m);
  const ue = {};
  ne === "button" ? (ue.type = D === void 0 ? "button" : D, ue.disabled = u) : (!F.href && !F.to && (ue.role = "button"), u && (ue["aria-disabled"] = u));
  const ge = Ke(n, re, z);
  process.env.NODE_ENV !== "production" && g.useEffect(() => {
    J && !N.current && console.error(["MUI: The `component` prop provided to ButtonBase is invalid.", "Please make sure the children prop is rendered in this custom component."].join(`
`));
  }, [J]);
  const ye = b({}, o, {
    centerRipple: s,
    component: c,
    disabled: u,
    disableRipple: d,
    disableTouchRipple: f,
    focusRipple: p,
    tabIndex: L,
    focusVisible: B
  }), xe = l0(ye);
  return /* @__PURE__ */ x.jsxs(c0, b({
    as: ne,
    className: pe(xe.root, l),
    ownerState: ye,
    onBlur: fe,
    onClick: h,
    onContextMenu: X,
    onFocus: ve,
    onKeyDown: I,
    onKeyUp: Q,
    onMouseDown: le,
    onMouseLeave: Y,
    onMouseUp: K,
    onDragLeave: U,
    onTouchEnd: Oe,
    onTouchMove: Ne,
    onTouchStart: he,
    ref: ge,
    tabIndex: u ? -1 : L,
    type: D
  }, ue, F, {
    children: [i, J ? (
      /* TouchRipple is only needed client-side, x2 boost on the server. */
      /* @__PURE__ */ x.jsx(Af, b({
        ref: q,
        center: s
      }, M))
    ) : null]
  }));
});
process.env.NODE_ENV !== "production" && (lr.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * A ref for imperative actions.
   * It currently only supports `focusVisible()` action.
   */
  action: vt,
  /**
   * If `true`, the ripples are centered.
   * They won't start at the cursor interaction position.
   * @default false
   */
  centerRipple: r.bool,
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: hs,
  /**
   * If `true`, the component is disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, the ripple effect is disabled.
   *
   * ⚠️ Without a ripple there is no styling for :focus-visible by default. Be sure
   * to highlight the element by applying separate styles with the `.Mui-focusVisible` class.
   * @default false
   */
  disableRipple: r.bool,
  /**
   * If `true`, the touch ripple effect is disabled.
   * @default false
   */
  disableTouchRipple: r.bool,
  /**
   * If `true`, the base button will have a keyboard focus ripple.
   * @default false
   */
  focusRipple: r.bool,
  /**
   * This prop can help identify which element has keyboard focus.
   * The class name will be applied when the element gains the focus through keyboard interaction.
   * It's a polyfill for the [CSS :focus-visible selector](https://drafts.csswg.org/selectors-4/#the-focus-visible-pseudo).
   * The rationale for using this feature [is explained here](https://github.com/WICG/focus-visible/blob/HEAD/explainer.md).
   * A [polyfill can be used](https://github.com/WICG/focus-visible) to apply a `focus-visible` class to other components
   * if needed.
   */
  focusVisibleClassName: r.string,
  /**
   * @ignore
   */
  href: r.any,
  /**
   * The component used to render a link when the `href` prop is provided.
   * @default 'a'
   */
  LinkComponent: r.elementType,
  /**
   * @ignore
   */
  onBlur: r.func,
  /**
   * @ignore
   */
  onClick: r.func,
  /**
   * @ignore
   */
  onContextMenu: r.func,
  /**
   * @ignore
   */
  onDragLeave: r.func,
  /**
   * @ignore
   */
  onFocus: r.func,
  /**
   * Callback fired when the component is focused with a keyboard.
   * We trigger a `onFocus` callback too.
   */
  onFocusVisible: r.func,
  /**
   * @ignore
   */
  onKeyDown: r.func,
  /**
   * @ignore
   */
  onKeyUp: r.func,
  /**
   * @ignore
   */
  onMouseDown: r.func,
  /**
   * @ignore
   */
  onMouseLeave: r.func,
  /**
   * @ignore
   */
  onMouseUp: r.func,
  /**
   * @ignore
   */
  onTouchEnd: r.func,
  /**
   * @ignore
   */
  onTouchMove: r.func,
  /**
   * @ignore
   */
  onTouchStart: r.func,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * @default 0
   */
  tabIndex: r.number,
  /**
   * Props applied to the `TouchRipple` element.
   */
  TouchRippleProps: r.object,
  /**
   * A ref that points to the `TouchRipple` element.
   */
  touchRippleRef: r.oneOfType([r.func, r.shape({
    current: r.shape({
      pulsate: r.func.isRequired,
      start: r.func.isRequired,
      stop: r.func.isRequired
    })
  })]),
  /**
   * @ignore
   */
  type: r.oneOfType([r.oneOf(["button", "reset", "submit"]), r.string])
});
function u0(e) {
  return Pe("MuiIconButton", e);
}
const d0 = Ce("MuiIconButton", ["root", "disabled", "colorInherit", "colorPrimary", "colorSecondary", "colorError", "colorInfo", "colorSuccess", "colorWarning", "edgeStart", "edgeEnd", "sizeSmall", "sizeMedium", "sizeLarge"]), p0 = ["edge", "children", "className", "color", "disabled", "disableFocusRipple", "size"], f0 = (e) => {
  const {
    classes: t,
    disabled: n,
    color: o,
    edge: a,
    size: s
  } = e, i = {
    root: ["root", n && "disabled", o !== "default" && `color${de(o)}`, a && `edge${de(a)}`, `size${de(s)}`]
  };
  return Se(i, u0, t);
}, m0 = Z(lr, {
  name: "MuiIconButton",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.color !== "default" && t[`color${de(n.color)}`], n.edge && t[`edge${de(n.edge)}`], t[`size${de(n.size)}`]];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  textAlign: "center",
  flex: "0 0 auto",
  fontSize: e.typography.pxToRem(24),
  padding: 8,
  borderRadius: "50%",
  overflow: "visible",
  // Explicitly set the default value to solve a bug on IE11.
  color: (e.vars || e).palette.action.active,
  transition: e.transitions.create("background-color", {
    duration: e.transitions.duration.shortest
  })
}, !t.disableRipple && {
  "&:hover": {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.action.activeChannel} / ${e.vars.palette.action.hoverOpacity})` : qe(e.palette.action.active, e.palette.action.hoverOpacity),
    // Reset on touch devices, it doesn't add specificity
    "@media (hover: none)": {
      backgroundColor: "transparent"
    }
  }
}, t.edge === "start" && {
  marginLeft: t.size === "small" ? -3 : -12
}, t.edge === "end" && {
  marginRight: t.size === "small" ? -3 : -12
}), ({
  theme: e,
  ownerState: t
}) => {
  var n;
  const o = (n = (e.vars || e).palette) == null ? void 0 : n[t.color];
  return b({}, t.color === "inherit" && {
    color: "inherit"
  }, t.color !== "inherit" && t.color !== "default" && b({
    color: o == null ? void 0 : o.main
  }, !t.disableRipple && {
    "&:hover": b({}, o && {
      backgroundColor: e.vars ? `rgba(${o.mainChannel} / ${e.vars.palette.action.hoverOpacity})` : qe(o.main, e.palette.action.hoverOpacity)
    }, {
      // Reset on touch devices, it doesn't add specificity
      "@media (hover: none)": {
        backgroundColor: "transparent"
      }
    })
  }), t.size === "small" && {
    padding: 5,
    fontSize: e.typography.pxToRem(18)
  }, t.size === "large" && {
    padding: 12,
    fontSize: e.typography.pxToRem(28)
  }, {
    [`&.${d0.disabled}`]: {
      backgroundColor: "transparent",
      color: (e.vars || e).palette.action.disabled
    }
  });
}), pr = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiIconButton"
  }), {
    edge: a = !1,
    children: s,
    className: i,
    color: l = "default",
    disabled: c = !1,
    disableFocusRipple: u = !1,
    size: d = "medium"
  } = o, f = ie(o, p0), p = b({}, o, {
    edge: a,
    color: l,
    disabled: c,
    disableFocusRipple: u,
    size: d
  }), m = f0(p);
  return /* @__PURE__ */ x.jsx(m0, b({
    className: pe(m.root, i),
    centerRipple: !0,
    focusRipple: !u,
    disabled: c,
    ref: n
  }, f, {
    ownerState: p,
    children: s
  }));
});
process.env.NODE_ENV !== "production" && (pr.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The icon to display.
   */
  children: Sn(r.node, (e) => g.Children.toArray(e.children).some((n) => /* @__PURE__ */ g.isValidElement(n) && n.props.onClick) ? new Error(["MUI: You are providing an onClick event listener to a child of a button element.", "Prefer applying it to the IconButton directly.", "This guarantees that the whole <button> will be responsive to click events."].join(`
`)) : null),
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * @default 'default'
   */
  color: r.oneOfType([r.oneOf(["inherit", "default", "primary", "secondary", "error", "info", "success", "warning"]), r.string]),
  /**
   * If `true`, the component is disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, the  keyboard focus ripple is disabled.
   * @default false
   */
  disableFocusRipple: r.bool,
  /**
   * If `true`, the ripple effect is disabled.
   *
   * ⚠️ Without a ripple there is no styling for :focus-visible by default. Be sure
   * to highlight the element by applying separate styles with the `.Mui-focusVisible` class.
   * @default false
   */
  disableRipple: r.bool,
  /**
   * If given, uses a negative margin to counteract the padding on one
   * side (this is often helpful for aligning the left or right
   * side of the icon with content above or below, without ruining the border
   * size and shape).
   * @default false
   */
  edge: r.oneOf(["end", "start", !1]),
  /**
   * The size of the component.
   * `small` is equivalent to the dense button styling.
   * @default 'medium'
   */
  size: r.oneOfType([r.oneOf(["small", "medium", "large"]), r.string]),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function h0(e) {
  return Pe("MuiTypography", e);
}
Ce("MuiTypography", ["root", "h1", "h2", "h3", "h4", "h5", "h6", "subtitle1", "subtitle2", "body1", "body2", "inherit", "button", "caption", "overline", "alignLeft", "alignRight", "alignCenter", "alignJustify", "noWrap", "gutterBottom", "paragraph"]);
const b0 = ["align", "className", "component", "gutterBottom", "noWrap", "paragraph", "variant", "variantMapping"], g0 = (e) => {
  const {
    align: t,
    gutterBottom: n,
    noWrap: o,
    paragraph: a,
    variant: s,
    classes: i
  } = e, l = {
    root: ["root", s, e.align !== "inherit" && `align${de(t)}`, n && "gutterBottom", o && "noWrap", a && "paragraph"]
  };
  return Se(l, h0, i);
}, y0 = Z("span", {
  name: "MuiTypography",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.variant && t[n.variant], n.align !== "inherit" && t[`align${de(n.align)}`], n.noWrap && t.noWrap, n.gutterBottom && t.gutterBottom, n.paragraph && t.paragraph];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  margin: 0
}, t.variant === "inherit" && {
  // Some elements, like <button> on Chrome have default font that doesn't inherit, reset this.
  font: "inherit"
}, t.variant !== "inherit" && e.typography[t.variant], t.align !== "inherit" && {
  textAlign: t.align
}, t.noWrap && {
  overflow: "hidden",
  textOverflow: "ellipsis",
  whiteSpace: "nowrap"
}, t.gutterBottom && {
  marginBottom: "0.35em"
}, t.paragraph && {
  marginBottom: 16
})), qu = {
  h1: "h1",
  h2: "h2",
  h3: "h3",
  h4: "h4",
  h5: "h5",
  h6: "h6",
  subtitle1: "h6",
  subtitle2: "h6",
  body1: "p",
  body2: "p",
  inherit: "p"
}, v0 = {
  primary: "primary.main",
  textPrimary: "text.primary",
  secondary: "secondary.main",
  textSecondary: "text.secondary",
  error: "error.main"
}, x0 = (e) => v0[e] || e, Rt = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTypography"
  }), a = x0(o.color), s = Il(b({}, o, {
    color: a
  })), {
    align: i = "inherit",
    className: l,
    component: c,
    gutterBottom: u = !1,
    noWrap: d = !1,
    paragraph: f = !1,
    variant: p = "body1",
    variantMapping: m = qu
  } = s, v = ie(s, b0), h = b({}, s, {
    align: i,
    color: a,
    className: l,
    component: c,
    gutterBottom: u,
    noWrap: d,
    paragraph: f,
    variant: p,
    variantMapping: m
  }), y = c || (f ? "p" : m[p] || qu[p]) || "span", w = g0(h);
  return /* @__PURE__ */ x.jsx(y0, b({
    as: y,
    ref: n,
    ownerState: h,
    className: pe(w.root, l)
  }, v));
});
process.env.NODE_ENV !== "production" && (Rt.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Set the text-align on the component.
   * @default 'inherit'
   */
  align: r.oneOf(["center", "inherit", "justify", "left", "right"]),
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * If `true`, the text will have a bottom margin.
   * @default false
   */
  gutterBottom: r.bool,
  /**
   * If `true`, the text will not wrap, but instead will truncate with a text overflow ellipsis.
   *
   * Note that text overflow can only happen with block or inline-block level elements
   * (the element needs to have a width in order to overflow).
   * @default false
   */
  noWrap: r.bool,
  /**
   * If `true`, the element will be a paragraph element.
   * @default false
   */
  paragraph: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Applies the theme typography styles.
   * @default 'body1'
   */
  variant: r.oneOfType([r.oneOf(["body1", "body2", "button", "caption", "h1", "h2", "h3", "h4", "h5", "h6", "inherit", "overline", "subtitle1", "subtitle2"]), r.string]),
  /**
   * The component maps the variant prop to a range of different HTML element types.
   * For instance, subtitle1 to `<h6>`.
   * If you wish to change that mapping, you can provide your own.
   * Alternatively, you can use the `component` prop.
   * @default {
   *   h1: 'h1',
   *   h2: 'h2',
   *   h3: 'h3',
   *   h4: 'h4',
   *   h5: 'h5',
   *   h6: 'h6',
   *   subtitle1: 'h6',
   *   subtitle2: 'h6',
   *   body1: 'p',
   *   body2: 'p',
   *   inherit: 'p',
   * }
   */
  variantMapping: r.object
});
const Ff = "base";
function T0(e) {
  return `${Ff}--${e}`;
}
function w0(e, t) {
  return `${Ff}-${e}-${t}`;
}
function Vf(e, t) {
  const n = Vp[t];
  return n ? T0(n) : w0(e, t);
}
function E0(e, t) {
  const n = {};
  return t.forEach((o) => {
    n[o] = Vf(e, o);
  }), n;
}
const C0 = ["input", "select", "textarea", "a[href]", "button", "[tabindex]", "audio[controls]", "video[controls]", '[contenteditable]:not([contenteditable="false"])'].join(",");
function O0(e) {
  const t = parseInt(e.getAttribute("tabindex") || "", 10);
  return Number.isNaN(t) ? e.contentEditable === "true" || (e.nodeName === "AUDIO" || e.nodeName === "VIDEO" || e.nodeName === "DETAILS") && e.getAttribute("tabindex") === null ? 0 : e.tabIndex : t;
}
function S0(e) {
  if (e.tagName !== "INPUT" || e.type !== "radio" || !e.name)
    return !1;
  const t = (o) => e.ownerDocument.querySelector(`input[type="radio"]${o}`);
  let n = t(`[name="${e.name}"]:checked`);
  return n || (n = t(`[name="${e.name}"]`)), n !== e;
}
function P0(e) {
  return !(e.disabled || e.tagName === "INPUT" && e.type === "hidden" || S0(e));
}
function R0(e) {
  const t = [], n = [];
  return Array.from(e.querySelectorAll(C0)).forEach((o, a) => {
    const s = O0(o);
    s === -1 || !P0(o) || (s === 0 ? t.push(o) : n.push({
      documentOrder: a,
      tabIndex: s,
      node: o
    }));
  }), n.sort((o, a) => o.tabIndex === a.tabIndex ? o.documentOrder - a.documentOrder : o.tabIndex - a.tabIndex).map((o) => o.node).concat(t);
}
function D0() {
  return !0;
}
function Ko(e) {
  const {
    children: t,
    disableAutoFocus: n = !1,
    disableEnforceFocus: o = !1,
    disableRestoreFocus: a = !1,
    getTabbable: s = R0,
    isEnabled: i = D0,
    open: l
  } = e, c = g.useRef(!1), u = g.useRef(null), d = g.useRef(null), f = g.useRef(null), p = g.useRef(null), m = g.useRef(!1), v = g.useRef(null), h = Ke(t.ref, v), y = g.useRef(null);
  g.useEffect(() => {
    !l || !v.current || (m.current = !n);
  }, [n, l]), g.useEffect(() => {
    if (!l || !v.current)
      return;
    const E = dt(v.current);
    return v.current.contains(E.activeElement) || (v.current.hasAttribute("tabIndex") || (process.env.NODE_ENV !== "production" && console.error(["MUI: The modal content node does not accept focus.", 'For the benefit of assistive technologies, the tabIndex of the node is being set to "-1".'].join(`
`)), v.current.setAttribute("tabIndex", "-1")), m.current && v.current.focus()), () => {
      a || (f.current && f.current.focus && (c.current = !0, f.current.focus()), f.current = null);
    };
  }, [l]), g.useEffect(() => {
    if (!l || !v.current)
      return;
    const E = dt(v.current), O = (S) => {
      y.current = S, !(o || !i() || S.key !== "Tab") && E.activeElement === v.current && S.shiftKey && (c.current = !0, d.current && d.current.focus());
    }, T = () => {
      const S = v.current;
      if (S === null)
        return;
      if (!E.hasFocus() || !i() || c.current) {
        c.current = !1;
        return;
      }
      if (S.contains(E.activeElement) || o && E.activeElement !== u.current && E.activeElement !== d.current)
        return;
      if (E.activeElement !== p.current)
        p.current = null;
      else if (p.current !== null)
        return;
      if (!m.current)
        return;
      let j = [];
      if ((E.activeElement === u.current || E.activeElement === d.current) && (j = s(v.current)), j.length > 0) {
        var $, V;
        const _ = !!(($ = y.current) != null && $.shiftKey && ((V = y.current) == null ? void 0 : V.key) === "Tab"), L = j[0], M = j[j.length - 1];
        typeof L != "string" && typeof M != "string" && (_ ? M.focus() : L.focus());
      } else
        S.focus();
    };
    E.addEventListener("focusin", T), E.addEventListener("keydown", O, !0);
    const P = setInterval(() => {
      E.activeElement && E.activeElement.tagName === "BODY" && T();
    }, 50);
    return () => {
      clearInterval(P), E.removeEventListener("focusin", T), E.removeEventListener("keydown", O, !0);
    };
  }, [n, o, a, i, l, s]);
  const w = (E) => {
    f.current === null && (f.current = E.relatedTarget), m.current = !0, p.current = E.target;
    const O = t.props.onFocus;
    O && O(E);
  }, C = (E) => {
    f.current === null && (f.current = E.relatedTarget), m.current = !0;
  };
  return /* @__PURE__ */ x.jsxs(g.Fragment, {
    children: [/* @__PURE__ */ x.jsx("div", {
      tabIndex: l ? 0 : -1,
      onFocus: C,
      ref: u,
      "data-testid": "sentinelStart"
    }), /* @__PURE__ */ g.cloneElement(t, {
      ref: h,
      onFocus: w
    }), /* @__PURE__ */ x.jsx("div", {
      tabIndex: l ? 0 : -1,
      onFocus: C,
      ref: d,
      "data-testid": "sentinelEnd"
    })]
  });
}
process.env.NODE_ENV !== "production" && (Ko.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │ To update them, edit the TypeScript types and run `pnpm proptypes`. │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * A single child content element.
   */
  children: ao,
  /**
   * If `true`, the focus trap will not automatically shift focus to itself when it opens, and
   * replace it to the last focused element when it closes.
   * This also works correctly with any focus trap children that have the `disableAutoFocus` prop.
   *
   * Generally this should never be set to `true` as it makes the focus trap less
   * accessible to assistive technologies, like screen readers.
   * @default false
   */
  disableAutoFocus: r.bool,
  /**
   * If `true`, the focus trap will not prevent focus from leaving the focus trap while open.
   *
   * Generally this should never be set to `true` as it makes the focus trap less
   * accessible to assistive technologies, like screen readers.
   * @default false
   */
  disableEnforceFocus: r.bool,
  /**
   * If `true`, the focus trap will not restore focus to previously focused element once
   * focus trap is hidden or unmounted.
   * @default false
   */
  disableRestoreFocus: r.bool,
  /**
   * Returns an array of ordered tabbable nodes (i.e. in tab order) within the root.
   * For instance, you can provide the "tabbable" npm dependency.
   * @param {HTMLElement} root
   */
  getTabbable: r.func,
  /**
   * This prop extends the `open` prop.
   * It allows to toggle the open state without having to wait for a rerender when changing the `open` prop.
   * This prop should be memoized.
   * It can be used to support multiple focus trap mounted at the same time.
   * @default function defaultIsEnabled(): boolean {
   *   return true;
   * }
   */
  isEnabled: r.func,
  /**
   * If `true`, focus is locked.
   */
  open: r.bool.isRequired
});
process.env.NODE_ENV !== "production" && (Ko.propTypes = kp(Ko.propTypes));
function $0(e) {
  return typeof e == "function" ? e() : e;
}
const Go = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const {
    children: o,
    container: a,
    disablePortal: s = !1
  } = t, [i, l] = g.useState(null), c = Ke(/* @__PURE__ */ g.isValidElement(o) ? o.ref : null, n);
  if (ft(() => {
    s || l($0(a) || document.body);
  }, [a, s]), ft(() => {
    if (i && !s)
      return Ka(n, i), () => {
        Ka(n, null);
      };
  }, [n, i, s]), s) {
    if (/* @__PURE__ */ g.isValidElement(o)) {
      const u = {
        ref: c
      };
      return /* @__PURE__ */ g.cloneElement(o, u);
    }
    return /* @__PURE__ */ x.jsx(g.Fragment, {
      children: o
    });
  }
  return /* @__PURE__ */ x.jsx(g.Fragment, {
    children: i && /* @__PURE__ */ ub.createPortal(o, i)
  });
});
process.env.NODE_ENV !== "production" && (Go.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │ To update them, edit the TypeScript types and run `pnpm proptypes`. │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The children to render into the `container`.
   */
  children: r.node,
  /**
   * An HTML element or function that returns one.
   * The `container` will have the portal children appended to it.
   *
   * You can also provide a callback, which is called in a React layout effect.
   * This lets you set the container from a ref, and also makes server-side rendering possible.
   *
   * By default, it uses the body of the top-level document object,
   * so it's simply `document.body` most of the time.
   */
  container: r.oneOfType([Tn, r.func]),
  /**
   * The `children` will be under the DOM hierarchy of the parent component.
   * @default false
   */
  disablePortal: r.bool
});
process.env.NODE_ENV !== "production" && (Go.propTypes = kp(Go.propTypes));
function k0(e) {
  const t = dt(e);
  return t.body === e ? Fn(e).innerWidth > t.documentElement.clientWidth : e.scrollHeight > e.clientHeight;
}
function Io(e, t) {
  t ? e.setAttribute("aria-hidden", "true") : e.removeAttribute("aria-hidden");
}
function Yu(e) {
  return parseInt(Fn(e).getComputedStyle(e).paddingRight, 10) || 0;
}
function _0(e) {
  const n = ["TEMPLATE", "SCRIPT", "STYLE", "LINK", "MAP", "META", "NOSCRIPT", "PICTURE", "COL", "COLGROUP", "PARAM", "SLOT", "SOURCE", "TRACK"].indexOf(e.tagName) !== -1, o = e.tagName === "INPUT" && e.getAttribute("type") === "hidden";
  return n || o;
}
function Ku(e, t, n, o, a) {
  const s = [t, n, ...o];
  [].forEach.call(e.children, (i) => {
    const l = s.indexOf(i) === -1, c = !_0(i);
    l && c && Io(i, a);
  });
}
function Ti(e, t) {
  let n = -1;
  return e.some((o, a) => t(o) ? (n = a, !0) : !1), n;
}
function M0(e, t) {
  const n = [], o = e.container;
  if (!t.disableScrollLock) {
    if (k0(o)) {
      const i = Np(dt(o));
      n.push({
        value: o.style.paddingRight,
        property: "padding-right",
        el: o
      }), o.style.paddingRight = `${Yu(o) + i}px`;
      const l = dt(o).querySelectorAll(".mui-fixed");
      [].forEach.call(l, (c) => {
        n.push({
          value: c.style.paddingRight,
          property: "padding-right",
          el: c
        }), c.style.paddingRight = `${Yu(c) + i}px`;
      });
    }
    let s;
    if (o.parentNode instanceof DocumentFragment)
      s = dt(o).body;
    else {
      const i = o.parentElement, l = Fn(o);
      s = (i == null ? void 0 : i.nodeName) === "HTML" && l.getComputedStyle(i).overflowY === "scroll" ? i : o;
    }
    n.push({
      value: s.style.overflow,
      property: "overflow",
      el: s
    }, {
      value: s.style.overflowX,
      property: "overflow-x",
      el: s
    }, {
      value: s.style.overflowY,
      property: "overflow-y",
      el: s
    }), s.style.overflow = "hidden";
  }
  return () => {
    n.forEach(({
      value: s,
      el: i,
      property: l
    }) => {
      s ? i.style.setProperty(l, s) : i.style.removeProperty(l);
    });
  };
}
function I0(e) {
  const t = [];
  return [].forEach.call(e.children, (n) => {
    n.getAttribute("aria-hidden") === "true" && t.push(n);
  }), t;
}
class N0 {
  constructor() {
    this.containers = void 0, this.modals = void 0, this.modals = [], this.containers = [];
  }
  add(t, n) {
    let o = this.modals.indexOf(t);
    if (o !== -1)
      return o;
    o = this.modals.length, this.modals.push(t), t.modalRef && Io(t.modalRef, !1);
    const a = I0(n);
    Ku(n, t.mount, t.modalRef, a, !0);
    const s = Ti(this.containers, (i) => i.container === n);
    return s !== -1 ? (this.containers[s].modals.push(t), o) : (this.containers.push({
      modals: [t],
      container: n,
      restore: null,
      hiddenSiblings: a
    }), o);
  }
  mount(t, n) {
    const o = Ti(this.containers, (s) => s.modals.indexOf(t) !== -1), a = this.containers[o];
    a.restore || (a.restore = M0(a, n));
  }
  remove(t, n = !0) {
    const o = this.modals.indexOf(t);
    if (o === -1)
      return o;
    const a = Ti(this.containers, (i) => i.modals.indexOf(t) !== -1), s = this.containers[a];
    if (s.modals.splice(s.modals.indexOf(t), 1), this.modals.splice(o, 1), s.modals.length === 0)
      s.restore && s.restore(), t.modalRef && Io(t.modalRef, n), Ku(s.container, t.mount, t.modalRef, s.hiddenSiblings, !1), this.containers.splice(a, 1);
    else {
      const i = s.modals[s.modals.length - 1];
      i.modalRef && Io(i.modalRef, !1);
    }
    return o;
  }
  isTopModal(t) {
    return this.modals.length > 0 && this.modals[this.modals.length - 1] === t;
  }
}
function j0(e) {
  return typeof e == "function" ? e() : e;
}
function A0(e) {
  return e ? e.props.hasOwnProperty("in") : !1;
}
const F0 = new N0();
function V0(e) {
  const {
    container: t,
    disableEscapeKeyDown: n = !1,
    disableScrollLock: o = !1,
    // @ts-ignore internal logic - Base UI supports the manager as a prop too
    manager: a = F0,
    closeAfterTransition: s = !1,
    onTransitionEnter: i,
    onTransitionExited: l,
    children: c,
    onClose: u,
    open: d,
    rootRef: f
  } = e, p = g.useRef({}), m = g.useRef(null), v = g.useRef(null), h = Ke(v, f), [y, w] = g.useState(!d), C = A0(c);
  let E = !0;
  (e["aria-hidden"] === "false" || e["aria-hidden"] === !1) && (E = !1);
  const O = () => dt(m.current), T = () => (p.current.modalRef = v.current, p.current.mount = m.current, p.current), P = () => {
    a.mount(T(), {
      disableScrollLock: o
    }), v.current && (v.current.scrollTop = 0);
  }, S = we(() => {
    const F = j0(t) || O().body;
    a.add(T(), F), v.current && P();
  }), j = g.useCallback(() => a.isTopModal(T()), [a]), $ = we((F) => {
    m.current = F, F && (d && j() ? P() : v.current && Io(v.current, E));
  }), V = g.useCallback(() => {
    a.remove(T(), E);
  }, [E, a]);
  g.useEffect(() => () => {
    V();
  }, [V]), g.useEffect(() => {
    d ? S() : (!C || !s) && V();
  }, [d, V, C, s, S]);
  const _ = (F) => (z) => {
    var N;
    (N = F.onKeyDown) == null || N.call(F, z), !(z.key !== "Escape" || z.which === 229 || // Wait until IME is settled.
    !j()) && (n || (z.stopPropagation(), u && u(z, "escapeKeyDown")));
  }, L = (F) => (z) => {
    var N;
    (N = F.onClick) == null || N.call(F, z), z.target === z.currentTarget && u && u(z, "backdropClick");
  };
  return {
    getRootProps: (F = {}) => {
      const z = Nf(e);
      delete z.onTransitionEnter, delete z.onTransitionExited;
      const N = b({}, z, F);
      return b({
        role: "presentation"
      }, N, {
        onKeyDown: _(N),
        ref: h
      });
    },
    getBackdropProps: (F = {}) => {
      const z = F;
      return b({
        "aria-hidden": !0
      }, z, {
        onClick: L(z),
        open: d
      });
    },
    getTransitionProps: () => {
      const F = () => {
        w(!1), i && i();
      }, z = () => {
        w(!0), l && l(), s && V();
      };
      return {
        onEnter: Ai(F, c == null ? void 0 : c.props.onEnter),
        onExited: Ai(z, c == null ? void 0 : c.props.onExited)
      };
    },
    rootRef: h,
    portalRef: $,
    isTopModal: j,
    exited: y,
    hasTransition: C
  };
}
var Ot = "top", Yt = "bottom", Kt = "right", St = "left", zl = "auto", fa = [Ot, Yt, Kt, St], Jr = "start", Xo = "end", L0 = "clippingParents", Lf = "viewport", wo = "popper", B0 = "reference", Gu = /* @__PURE__ */ fa.reduce(function(e, t) {
  return e.concat([t + "-" + Jr, t + "-" + Xo]);
}, []), Bf = /* @__PURE__ */ [].concat(fa, [zl]).reduce(function(e, t) {
  return e.concat([t, t + "-" + Jr, t + "-" + Xo]);
}, []), z0 = "beforeRead", W0 = "read", U0 = "afterRead", H0 = "beforeMain", q0 = "main", Y0 = "afterMain", K0 = "beforeWrite", G0 = "write", X0 = "afterWrite", Z0 = [z0, W0, U0, H0, q0, Y0, K0, G0, X0];
function pn(e) {
  return e ? (e.nodeName || "").toLowerCase() : null;
}
function Mt(e) {
  if (e == null)
    return window;
  if (e.toString() !== "[object Window]") {
    var t = e.ownerDocument;
    return t && t.defaultView || window;
  }
  return e;
}
function cr(e) {
  var t = Mt(e).Element;
  return e instanceof t || e instanceof Element;
}
function Ut(e) {
  var t = Mt(e).HTMLElement;
  return e instanceof t || e instanceof HTMLElement;
}
function Wl(e) {
  if (typeof ShadowRoot > "u")
    return !1;
  var t = Mt(e).ShadowRoot;
  return e instanceof t || e instanceof ShadowRoot;
}
function J0(e) {
  var t = e.state;
  Object.keys(t.elements).forEach(function(n) {
    var o = t.styles[n] || {}, a = t.attributes[n] || {}, s = t.elements[n];
    !Ut(s) || !pn(s) || (Object.assign(s.style, o), Object.keys(a).forEach(function(i) {
      var l = a[i];
      l === !1 ? s.removeAttribute(i) : s.setAttribute(i, l === !0 ? "" : l);
    }));
  });
}
function Q0(e) {
  var t = e.state, n = {
    popper: {
      position: t.options.strategy,
      left: "0",
      top: "0",
      margin: "0"
    },
    arrow: {
      position: "absolute"
    },
    reference: {}
  };
  return Object.assign(t.elements.popper.style, n.popper), t.styles = n, t.elements.arrow && Object.assign(t.elements.arrow.style, n.arrow), function() {
    Object.keys(t.elements).forEach(function(o) {
      var a = t.elements[o], s = t.attributes[o] || {}, i = Object.keys(t.styles.hasOwnProperty(o) ? t.styles[o] : n[o]), l = i.reduce(function(c, u) {
        return c[u] = "", c;
      }, {});
      !Ut(a) || !pn(a) || (Object.assign(a.style, l), Object.keys(s).forEach(function(c) {
        a.removeAttribute(c);
      }));
    });
  };
}
const eT = {
  name: "applyStyles",
  enabled: !0,
  phase: "write",
  fn: J0,
  effect: Q0,
  requires: ["computeStyles"]
};
function dn(e) {
  return e.split("-")[0];
}
var ar = Math.max, ts = Math.min, Qr = Math.round;
function Ki() {
  var e = navigator.userAgentData;
  return e != null && e.brands && Array.isArray(e.brands) ? e.brands.map(function(t) {
    return t.brand + "/" + t.version;
  }).join(" ") : navigator.userAgent;
}
function zf() {
  return !/^((?!chrome|android).)*safari/i.test(Ki());
}
function eo(e, t, n) {
  t === void 0 && (t = !1), n === void 0 && (n = !1);
  var o = e.getBoundingClientRect(), a = 1, s = 1;
  t && Ut(e) && (a = e.offsetWidth > 0 && Qr(o.width) / e.offsetWidth || 1, s = e.offsetHeight > 0 && Qr(o.height) / e.offsetHeight || 1);
  var i = cr(e) ? Mt(e) : window, l = i.visualViewport, c = !zf() && n, u = (o.left + (c && l ? l.offsetLeft : 0)) / a, d = (o.top + (c && l ? l.offsetTop : 0)) / s, f = o.width / a, p = o.height / s;
  return {
    width: f,
    height: p,
    top: d,
    right: u + f,
    bottom: d + p,
    left: u,
    x: u,
    y: d
  };
}
function Ul(e) {
  var t = eo(e), n = e.offsetWidth, o = e.offsetHeight;
  return Math.abs(t.width - n) <= 1 && (n = t.width), Math.abs(t.height - o) <= 1 && (o = t.height), {
    x: e.offsetLeft,
    y: e.offsetTop,
    width: n,
    height: o
  };
}
function Wf(e, t) {
  var n = t.getRootNode && t.getRootNode();
  if (e.contains(t))
    return !0;
  if (n && Wl(n)) {
    var o = t;
    do {
      if (o && e.isSameNode(o))
        return !0;
      o = o.parentNode || o.host;
    } while (o);
  }
  return !1;
}
function En(e) {
  return Mt(e).getComputedStyle(e);
}
function tT(e) {
  return ["table", "td", "th"].indexOf(pn(e)) >= 0;
}
function Wn(e) {
  return ((cr(e) ? e.ownerDocument : (
    // $FlowFixMe[prop-missing]
    e.document
  )) || window.document).documentElement;
}
function Is(e) {
  return pn(e) === "html" ? e : (
    // this is a quicker (but less type safe) way to save quite some bytes from the bundle
    // $FlowFixMe[incompatible-return]
    // $FlowFixMe[prop-missing]
    e.assignedSlot || // step into the shadow DOM of the parent of a slotted node
    e.parentNode || // DOM Element detected
    (Wl(e) ? e.host : null) || // ShadowRoot detected
    // $FlowFixMe[incompatible-call]: HTMLElement is a Node
    Wn(e)
  );
}
function Xu(e) {
  return !Ut(e) || // https://github.com/popperjs/popper-core/issues/837
  En(e).position === "fixed" ? null : e.offsetParent;
}
function nT(e) {
  var t = /firefox/i.test(Ki()), n = /Trident/i.test(Ki());
  if (n && Ut(e)) {
    var o = En(e);
    if (o.position === "fixed")
      return null;
  }
  var a = Is(e);
  for (Wl(a) && (a = a.host); Ut(a) && ["html", "body"].indexOf(pn(a)) < 0; ) {
    var s = En(a);
    if (s.transform !== "none" || s.perspective !== "none" || s.contain === "paint" || ["transform", "perspective"].indexOf(s.willChange) !== -1 || t && s.willChange === "filter" || t && s.filter && s.filter !== "none")
      return a;
    a = a.parentNode;
  }
  return null;
}
function ma(e) {
  for (var t = Mt(e), n = Xu(e); n && tT(n) && En(n).position === "static"; )
    n = Xu(n);
  return n && (pn(n) === "html" || pn(n) === "body" && En(n).position === "static") ? t : n || nT(e) || t;
}
function Hl(e) {
  return ["top", "bottom"].indexOf(e) >= 0 ? "x" : "y";
}
function No(e, t, n) {
  return ar(e, ts(t, n));
}
function rT(e, t, n) {
  var o = No(e, t, n);
  return o > n ? n : o;
}
function Uf() {
  return {
    top: 0,
    right: 0,
    bottom: 0,
    left: 0
  };
}
function Hf(e) {
  return Object.assign({}, Uf(), e);
}
function qf(e, t) {
  return t.reduce(function(n, o) {
    return n[o] = e, n;
  }, {});
}
var oT = function(t, n) {
  return t = typeof t == "function" ? t(Object.assign({}, n.rects, {
    placement: n.placement
  })) : t, Hf(typeof t != "number" ? t : qf(t, fa));
};
function aT(e) {
  var t, n = e.state, o = e.name, a = e.options, s = n.elements.arrow, i = n.modifiersData.popperOffsets, l = dn(n.placement), c = Hl(l), u = [St, Kt].indexOf(l) >= 0, d = u ? "height" : "width";
  if (!(!s || !i)) {
    var f = oT(a.padding, n), p = Ul(s), m = c === "y" ? Ot : St, v = c === "y" ? Yt : Kt, h = n.rects.reference[d] + n.rects.reference[c] - i[c] - n.rects.popper[d], y = i[c] - n.rects.reference[c], w = ma(s), C = w ? c === "y" ? w.clientHeight || 0 : w.clientWidth || 0 : 0, E = h / 2 - y / 2, O = f[m], T = C - p[d] - f[v], P = C / 2 - p[d] / 2 + E, S = No(O, P, T), j = c;
    n.modifiersData[o] = (t = {}, t[j] = S, t.centerOffset = S - P, t);
  }
}
function sT(e) {
  var t = e.state, n = e.options, o = n.element, a = o === void 0 ? "[data-popper-arrow]" : o;
  a != null && (typeof a == "string" && (a = t.elements.popper.querySelector(a), !a) || Wf(t.elements.popper, a) && (t.elements.arrow = a));
}
const iT = {
  name: "arrow",
  enabled: !0,
  phase: "main",
  fn: aT,
  effect: sT,
  requires: ["popperOffsets"],
  requiresIfExists: ["preventOverflow"]
};
function to(e) {
  return e.split("-")[1];
}
var lT = {
  top: "auto",
  right: "auto",
  bottom: "auto",
  left: "auto"
};
function cT(e, t) {
  var n = e.x, o = e.y, a = t.devicePixelRatio || 1;
  return {
    x: Qr(n * a) / a || 0,
    y: Qr(o * a) / a || 0
  };
}
function Zu(e) {
  var t, n = e.popper, o = e.popperRect, a = e.placement, s = e.variation, i = e.offsets, l = e.position, c = e.gpuAcceleration, u = e.adaptive, d = e.roundOffsets, f = e.isFixed, p = i.x, m = p === void 0 ? 0 : p, v = i.y, h = v === void 0 ? 0 : v, y = typeof d == "function" ? d({
    x: m,
    y: h
  }) : {
    x: m,
    y: h
  };
  m = y.x, h = y.y;
  var w = i.hasOwnProperty("x"), C = i.hasOwnProperty("y"), E = St, O = Ot, T = window;
  if (u) {
    var P = ma(n), S = "clientHeight", j = "clientWidth";
    if (P === Mt(n) && (P = Wn(n), En(P).position !== "static" && l === "absolute" && (S = "scrollHeight", j = "scrollWidth")), P = P, a === Ot || (a === St || a === Kt) && s === Xo) {
      O = Yt;
      var $ = f && P === T && T.visualViewport ? T.visualViewport.height : (
        // $FlowFixMe[prop-missing]
        P[S]
      );
      h -= $ - o.height, h *= c ? 1 : -1;
    }
    if (a === St || (a === Ot || a === Yt) && s === Xo) {
      E = Kt;
      var V = f && P === T && T.visualViewport ? T.visualViewport.width : (
        // $FlowFixMe[prop-missing]
        P[j]
      );
      m -= V - o.width, m *= c ? 1 : -1;
    }
  }
  var _ = Object.assign({
    position: l
  }, u && lT), L = d === !0 ? cT({
    x: m,
    y: h
  }, Mt(n)) : {
    x: m,
    y: h
  };
  if (m = L.x, h = L.y, c) {
    var M;
    return Object.assign({}, _, (M = {}, M[O] = C ? "0" : "", M[E] = w ? "0" : "", M.transform = (T.devicePixelRatio || 1) <= 1 ? "translate(" + m + "px, " + h + "px)" : "translate3d(" + m + "px, " + h + "px, 0)", M));
  }
  return Object.assign({}, _, (t = {}, t[O] = C ? h + "px" : "", t[E] = w ? m + "px" : "", t.transform = "", t));
}
function uT(e) {
  var t = e.state, n = e.options, o = n.gpuAcceleration, a = o === void 0 ? !0 : o, s = n.adaptive, i = s === void 0 ? !0 : s, l = n.roundOffsets, c = l === void 0 ? !0 : l, u = {
    placement: dn(t.placement),
    variation: to(t.placement),
    popper: t.elements.popper,
    popperRect: t.rects.popper,
    gpuAcceleration: a,
    isFixed: t.options.strategy === "fixed"
  };
  t.modifiersData.popperOffsets != null && (t.styles.popper = Object.assign({}, t.styles.popper, Zu(Object.assign({}, u, {
    offsets: t.modifiersData.popperOffsets,
    position: t.options.strategy,
    adaptive: i,
    roundOffsets: c
  })))), t.modifiersData.arrow != null && (t.styles.arrow = Object.assign({}, t.styles.arrow, Zu(Object.assign({}, u, {
    offsets: t.modifiersData.arrow,
    position: "absolute",
    adaptive: !1,
    roundOffsets: c
  })))), t.attributes.popper = Object.assign({}, t.attributes.popper, {
    "data-popper-placement": t.placement
  });
}
const dT = {
  name: "computeStyles",
  enabled: !0,
  phase: "beforeWrite",
  fn: uT,
  data: {}
};
var Sa = {
  passive: !0
};
function pT(e) {
  var t = e.state, n = e.instance, o = e.options, a = o.scroll, s = a === void 0 ? !0 : a, i = o.resize, l = i === void 0 ? !0 : i, c = Mt(t.elements.popper), u = [].concat(t.scrollParents.reference, t.scrollParents.popper);
  return s && u.forEach(function(d) {
    d.addEventListener("scroll", n.update, Sa);
  }), l && c.addEventListener("resize", n.update, Sa), function() {
    s && u.forEach(function(d) {
      d.removeEventListener("scroll", n.update, Sa);
    }), l && c.removeEventListener("resize", n.update, Sa);
  };
}
const fT = {
  name: "eventListeners",
  enabled: !0,
  phase: "write",
  fn: function() {
  },
  effect: pT,
  data: {}
};
var mT = {
  left: "right",
  right: "left",
  bottom: "top",
  top: "bottom"
};
function Ua(e) {
  return e.replace(/left|right|bottom|top/g, function(t) {
    return mT[t];
  });
}
var hT = {
  start: "end",
  end: "start"
};
function Ju(e) {
  return e.replace(/start|end/g, function(t) {
    return hT[t];
  });
}
function ql(e) {
  var t = Mt(e), n = t.pageXOffset, o = t.pageYOffset;
  return {
    scrollLeft: n,
    scrollTop: o
  };
}
function Yl(e) {
  return eo(Wn(e)).left + ql(e).scrollLeft;
}
function bT(e, t) {
  var n = Mt(e), o = Wn(e), a = n.visualViewport, s = o.clientWidth, i = o.clientHeight, l = 0, c = 0;
  if (a) {
    s = a.width, i = a.height;
    var u = zf();
    (u || !u && t === "fixed") && (l = a.offsetLeft, c = a.offsetTop);
  }
  return {
    width: s,
    height: i,
    x: l + Yl(e),
    y: c
  };
}
function gT(e) {
  var t, n = Wn(e), o = ql(e), a = (t = e.ownerDocument) == null ? void 0 : t.body, s = ar(n.scrollWidth, n.clientWidth, a ? a.scrollWidth : 0, a ? a.clientWidth : 0), i = ar(n.scrollHeight, n.clientHeight, a ? a.scrollHeight : 0, a ? a.clientHeight : 0), l = -o.scrollLeft + Yl(e), c = -o.scrollTop;
  return En(a || n).direction === "rtl" && (l += ar(n.clientWidth, a ? a.clientWidth : 0) - s), {
    width: s,
    height: i,
    x: l,
    y: c
  };
}
function Kl(e) {
  var t = En(e), n = t.overflow, o = t.overflowX, a = t.overflowY;
  return /auto|scroll|overlay|hidden/.test(n + a + o);
}
function Yf(e) {
  return ["html", "body", "#document"].indexOf(pn(e)) >= 0 ? e.ownerDocument.body : Ut(e) && Kl(e) ? e : Yf(Is(e));
}
function jo(e, t) {
  var n;
  t === void 0 && (t = []);
  var o = Yf(e), a = o === ((n = e.ownerDocument) == null ? void 0 : n.body), s = Mt(o), i = a ? [s].concat(s.visualViewport || [], Kl(o) ? o : []) : o, l = t.concat(i);
  return a ? l : (
    // $FlowFixMe[incompatible-call]: isBody tells us target will be an HTMLElement here
    l.concat(jo(Is(i)))
  );
}
function Gi(e) {
  return Object.assign({}, e, {
    left: e.x,
    top: e.y,
    right: e.x + e.width,
    bottom: e.y + e.height
  });
}
function yT(e, t) {
  var n = eo(e, !1, t === "fixed");
  return n.top = n.top + e.clientTop, n.left = n.left + e.clientLeft, n.bottom = n.top + e.clientHeight, n.right = n.left + e.clientWidth, n.width = e.clientWidth, n.height = e.clientHeight, n.x = n.left, n.y = n.top, n;
}
function Qu(e, t, n) {
  return t === Lf ? Gi(bT(e, n)) : cr(t) ? yT(t, n) : Gi(gT(Wn(e)));
}
function vT(e) {
  var t = jo(Is(e)), n = ["absolute", "fixed"].indexOf(En(e).position) >= 0, o = n && Ut(e) ? ma(e) : e;
  return cr(o) ? t.filter(function(a) {
    return cr(a) && Wf(a, o) && pn(a) !== "body";
  }) : [];
}
function xT(e, t, n, o) {
  var a = t === "clippingParents" ? vT(e) : [].concat(t), s = [].concat(a, [n]), i = s[0], l = s.reduce(function(c, u) {
    var d = Qu(e, u, o);
    return c.top = ar(d.top, c.top), c.right = ts(d.right, c.right), c.bottom = ts(d.bottom, c.bottom), c.left = ar(d.left, c.left), c;
  }, Qu(e, i, o));
  return l.width = l.right - l.left, l.height = l.bottom - l.top, l.x = l.left, l.y = l.top, l;
}
function Kf(e) {
  var t = e.reference, n = e.element, o = e.placement, a = o ? dn(o) : null, s = o ? to(o) : null, i = t.x + t.width / 2 - n.width / 2, l = t.y + t.height / 2 - n.height / 2, c;
  switch (a) {
    case Ot:
      c = {
        x: i,
        y: t.y - n.height
      };
      break;
    case Yt:
      c = {
        x: i,
        y: t.y + t.height
      };
      break;
    case Kt:
      c = {
        x: t.x + t.width,
        y: l
      };
      break;
    case St:
      c = {
        x: t.x - n.width,
        y: l
      };
      break;
    default:
      c = {
        x: t.x,
        y: t.y
      };
  }
  var u = a ? Hl(a) : null;
  if (u != null) {
    var d = u === "y" ? "height" : "width";
    switch (s) {
      case Jr:
        c[u] = c[u] - (t[d] / 2 - n[d] / 2);
        break;
      case Xo:
        c[u] = c[u] + (t[d] / 2 - n[d] / 2);
        break;
    }
  }
  return c;
}
function Zo(e, t) {
  t === void 0 && (t = {});
  var n = t, o = n.placement, a = o === void 0 ? e.placement : o, s = n.strategy, i = s === void 0 ? e.strategy : s, l = n.boundary, c = l === void 0 ? L0 : l, u = n.rootBoundary, d = u === void 0 ? Lf : u, f = n.elementContext, p = f === void 0 ? wo : f, m = n.altBoundary, v = m === void 0 ? !1 : m, h = n.padding, y = h === void 0 ? 0 : h, w = Hf(typeof y != "number" ? y : qf(y, fa)), C = p === wo ? B0 : wo, E = e.rects.popper, O = e.elements[v ? C : p], T = xT(cr(O) ? O : O.contextElement || Wn(e.elements.popper), c, d, i), P = eo(e.elements.reference), S = Kf({
    reference: P,
    element: E,
    strategy: "absolute",
    placement: a
  }), j = Gi(Object.assign({}, E, S)), $ = p === wo ? j : P, V = {
    top: T.top - $.top + w.top,
    bottom: $.bottom - T.bottom + w.bottom,
    left: T.left - $.left + w.left,
    right: $.right - T.right + w.right
  }, _ = e.modifiersData.offset;
  if (p === wo && _) {
    var L = _[a];
    Object.keys(V).forEach(function(M) {
      var R = [Kt, Yt].indexOf(M) >= 0 ? 1 : -1, D = [Ot, Yt].indexOf(M) >= 0 ? "y" : "x";
      V[M] += L[D] * R;
    });
  }
  return V;
}
function TT(e, t) {
  t === void 0 && (t = {});
  var n = t, o = n.placement, a = n.boundary, s = n.rootBoundary, i = n.padding, l = n.flipVariations, c = n.allowedAutoPlacements, u = c === void 0 ? Bf : c, d = to(o), f = d ? l ? Gu : Gu.filter(function(v) {
    return to(v) === d;
  }) : fa, p = f.filter(function(v) {
    return u.indexOf(v) >= 0;
  });
  p.length === 0 && (p = f);
  var m = p.reduce(function(v, h) {
    return v[h] = Zo(e, {
      placement: h,
      boundary: a,
      rootBoundary: s,
      padding: i
    })[dn(h)], v;
  }, {});
  return Object.keys(m).sort(function(v, h) {
    return m[v] - m[h];
  });
}
function wT(e) {
  if (dn(e) === zl)
    return [];
  var t = Ua(e);
  return [Ju(e), t, Ju(t)];
}
function ET(e) {
  var t = e.state, n = e.options, o = e.name;
  if (!t.modifiersData[o]._skip) {
    for (var a = n.mainAxis, s = a === void 0 ? !0 : a, i = n.altAxis, l = i === void 0 ? !0 : i, c = n.fallbackPlacements, u = n.padding, d = n.boundary, f = n.rootBoundary, p = n.altBoundary, m = n.flipVariations, v = m === void 0 ? !0 : m, h = n.allowedAutoPlacements, y = t.options.placement, w = dn(y), C = w === y, E = c || (C || !v ? [Ua(y)] : wT(y)), O = [y].concat(E).reduce(function(B, G) {
      return B.concat(dn(G) === zl ? TT(t, {
        placement: G,
        boundary: d,
        rootBoundary: f,
        padding: u,
        flipVariations: v,
        allowedAutoPlacements: h
      }) : G);
    }, []), T = t.rects.reference, P = t.rects.popper, S = /* @__PURE__ */ new Map(), j = !0, $ = O[0], V = 0; V < O.length; V++) {
      var _ = O[V], L = dn(_), M = to(_) === Jr, R = [Ot, Yt].indexOf(L) >= 0, D = R ? "width" : "height", F = Zo(t, {
        placement: _,
        boundary: d,
        rootBoundary: f,
        altBoundary: p,
        padding: u
      }), z = R ? M ? Kt : St : M ? Yt : Ot;
      T[D] > P[D] && (z = Ua(z));
      var N = Ua(z), q = [];
      if (s && q.push(F[L] <= 0), l && q.push(F[z] <= 0, F[N] <= 0), q.every(function(B) {
        return B;
      })) {
        $ = _, j = !1;
        break;
      }
      S.set(_, q);
    }
    if (j)
      for (var A = v ? 3 : 1, H = function(G) {
        var ee = O.find(function(W) {
          var J = S.get(W);
          if (J)
            return J.slice(0, G).every(function(se) {
              return se;
            });
        });
        if (ee)
          return $ = ee, "break";
      }, te = A; te > 0; te--) {
        var re = H(te);
        if (re === "break") break;
      }
    t.placement !== $ && (t.modifiersData[o]._skip = !0, t.placement = $, t.reset = !0);
  }
}
const CT = {
  name: "flip",
  enabled: !0,
  phase: "main",
  fn: ET,
  requiresIfExists: ["offset"],
  data: {
    _skip: !1
  }
};
function ed(e, t, n) {
  return n === void 0 && (n = {
    x: 0,
    y: 0
  }), {
    top: e.top - t.height - n.y,
    right: e.right - t.width + n.x,
    bottom: e.bottom - t.height + n.y,
    left: e.left - t.width - n.x
  };
}
function td(e) {
  return [Ot, Kt, Yt, St].some(function(t) {
    return e[t] >= 0;
  });
}
function OT(e) {
  var t = e.state, n = e.name, o = t.rects.reference, a = t.rects.popper, s = t.modifiersData.preventOverflow, i = Zo(t, {
    elementContext: "reference"
  }), l = Zo(t, {
    altBoundary: !0
  }), c = ed(i, o), u = ed(l, a, s), d = td(c), f = td(u);
  t.modifiersData[n] = {
    referenceClippingOffsets: c,
    popperEscapeOffsets: u,
    isReferenceHidden: d,
    hasPopperEscaped: f
  }, t.attributes.popper = Object.assign({}, t.attributes.popper, {
    "data-popper-reference-hidden": d,
    "data-popper-escaped": f
  });
}
const ST = {
  name: "hide",
  enabled: !0,
  phase: "main",
  requiresIfExists: ["preventOverflow"],
  fn: OT
};
function PT(e, t, n) {
  var o = dn(e), a = [St, Ot].indexOf(o) >= 0 ? -1 : 1, s = typeof n == "function" ? n(Object.assign({}, t, {
    placement: e
  })) : n, i = s[0], l = s[1];
  return i = i || 0, l = (l || 0) * a, [St, Kt].indexOf(o) >= 0 ? {
    x: l,
    y: i
  } : {
    x: i,
    y: l
  };
}
function RT(e) {
  var t = e.state, n = e.options, o = e.name, a = n.offset, s = a === void 0 ? [0, 0] : a, i = Bf.reduce(function(d, f) {
    return d[f] = PT(f, t.rects, s), d;
  }, {}), l = i[t.placement], c = l.x, u = l.y;
  t.modifiersData.popperOffsets != null && (t.modifiersData.popperOffsets.x += c, t.modifiersData.popperOffsets.y += u), t.modifiersData[o] = i;
}
const DT = {
  name: "offset",
  enabled: !0,
  phase: "main",
  requires: ["popperOffsets"],
  fn: RT
};
function $T(e) {
  var t = e.state, n = e.name;
  t.modifiersData[n] = Kf({
    reference: t.rects.reference,
    element: t.rects.popper,
    strategy: "absolute",
    placement: t.placement
  });
}
const kT = {
  name: "popperOffsets",
  enabled: !0,
  phase: "read",
  fn: $T,
  data: {}
};
function _T(e) {
  return e === "x" ? "y" : "x";
}
function MT(e) {
  var t = e.state, n = e.options, o = e.name, a = n.mainAxis, s = a === void 0 ? !0 : a, i = n.altAxis, l = i === void 0 ? !1 : i, c = n.boundary, u = n.rootBoundary, d = n.altBoundary, f = n.padding, p = n.tether, m = p === void 0 ? !0 : p, v = n.tetherOffset, h = v === void 0 ? 0 : v, y = Zo(t, {
    boundary: c,
    rootBoundary: u,
    padding: f,
    altBoundary: d
  }), w = dn(t.placement), C = to(t.placement), E = !C, O = Hl(w), T = _T(O), P = t.modifiersData.popperOffsets, S = t.rects.reference, j = t.rects.popper, $ = typeof h == "function" ? h(Object.assign({}, t.rects, {
    placement: t.placement
  })) : h, V = typeof $ == "number" ? {
    mainAxis: $,
    altAxis: $
  } : Object.assign({
    mainAxis: 0,
    altAxis: 0
  }, $), _ = t.modifiersData.offset ? t.modifiersData.offset[t.placement] : null, L = {
    x: 0,
    y: 0
  };
  if (P) {
    if (s) {
      var M, R = O === "y" ? Ot : St, D = O === "y" ? Yt : Kt, F = O === "y" ? "height" : "width", z = P[O], N = z + y[R], q = z - y[D], A = m ? -j[F] / 2 : 0, H = C === Jr ? S[F] : j[F], te = C === Jr ? -j[F] : -S[F], re = t.elements.arrow, B = m && re ? Ul(re) : {
        width: 0,
        height: 0
      }, G = t.modifiersData["arrow#persistent"] ? t.modifiersData["arrow#persistent"].padding : Uf(), ee = G[R], W = G[D], J = No(0, S[F], B[F]), se = E ? S[F] / 2 - A - J - ee - V.mainAxis : H - J - ee - V.mainAxis, le = E ? -S[F] / 2 + A + J + W + V.mainAxis : te + J + W + V.mainAxis, X = t.elements.arrow && ma(t.elements.arrow), U = X ? O === "y" ? X.clientTop || 0 : X.clientLeft || 0 : 0, K = (M = _ == null ? void 0 : _[O]) != null ? M : 0, Y = z + se - K - U, he = z + le - K, Oe = No(m ? ts(N, Y) : N, z, m ? ar(q, he) : q);
      P[O] = Oe, L[O] = Oe - z;
    }
    if (l) {
      var Ne, fe = O === "x" ? Ot : St, ve = O === "x" ? Yt : Kt, oe = P[T], ce = T === "y" ? "height" : "width", I = oe + y[fe], Q = oe - y[ve], ne = [Ot, St].indexOf(w) !== -1, ue = (Ne = _ == null ? void 0 : _[T]) != null ? Ne : 0, ge = ne ? I : oe - S[ce] - j[ce] - ue + V.altAxis, ye = ne ? oe + S[ce] + j[ce] - ue - V.altAxis : Q, xe = m && ne ? rT(ge, oe, ye) : No(m ? ge : I, oe, m ? ye : Q);
      P[T] = xe, L[T] = xe - oe;
    }
    t.modifiersData[o] = L;
  }
}
const IT = {
  name: "preventOverflow",
  enabled: !0,
  phase: "main",
  fn: MT,
  requiresIfExists: ["offset"]
};
function NT(e) {
  return {
    scrollLeft: e.scrollLeft,
    scrollTop: e.scrollTop
  };
}
function jT(e) {
  return e === Mt(e) || !Ut(e) ? ql(e) : NT(e);
}
function AT(e) {
  var t = e.getBoundingClientRect(), n = Qr(t.width) / e.offsetWidth || 1, o = Qr(t.height) / e.offsetHeight || 1;
  return n !== 1 || o !== 1;
}
function FT(e, t, n) {
  n === void 0 && (n = !1);
  var o = Ut(t), a = Ut(t) && AT(t), s = Wn(t), i = eo(e, a, n), l = {
    scrollLeft: 0,
    scrollTop: 0
  }, c = {
    x: 0,
    y: 0
  };
  return (o || !o && !n) && ((pn(t) !== "body" || // https://github.com/popperjs/popper-core/issues/1078
  Kl(s)) && (l = jT(t)), Ut(t) ? (c = eo(t, !0), c.x += t.clientLeft, c.y += t.clientTop) : s && (c.x = Yl(s))), {
    x: i.left + l.scrollLeft - c.x,
    y: i.top + l.scrollTop - c.y,
    width: i.width,
    height: i.height
  };
}
function VT(e) {
  var t = /* @__PURE__ */ new Map(), n = /* @__PURE__ */ new Set(), o = [];
  e.forEach(function(s) {
    t.set(s.name, s);
  });
  function a(s) {
    n.add(s.name);
    var i = [].concat(s.requires || [], s.requiresIfExists || []);
    i.forEach(function(l) {
      if (!n.has(l)) {
        var c = t.get(l);
        c && a(c);
      }
    }), o.push(s);
  }
  return e.forEach(function(s) {
    n.has(s.name) || a(s);
  }), o;
}
function LT(e) {
  var t = VT(e);
  return Z0.reduce(function(n, o) {
    return n.concat(t.filter(function(a) {
      return a.phase === o;
    }));
  }, []);
}
function BT(e) {
  var t;
  return function() {
    return t || (t = new Promise(function(n) {
      Promise.resolve().then(function() {
        t = void 0, n(e());
      });
    })), t;
  };
}
function zT(e) {
  var t = e.reduce(function(n, o) {
    var a = n[o.name];
    return n[o.name] = a ? Object.assign({}, a, o, {
      options: Object.assign({}, a.options, o.options),
      data: Object.assign({}, a.data, o.data)
    }) : o, n;
  }, {});
  return Object.keys(t).map(function(n) {
    return t[n];
  });
}
var nd = {
  placement: "bottom",
  modifiers: [],
  strategy: "absolute"
};
function rd() {
  for (var e = arguments.length, t = new Array(e), n = 0; n < e; n++)
    t[n] = arguments[n];
  return !t.some(function(o) {
    return !(o && typeof o.getBoundingClientRect == "function");
  });
}
function WT(e) {
  e === void 0 && (e = {});
  var t = e, n = t.defaultModifiers, o = n === void 0 ? [] : n, a = t.defaultOptions, s = a === void 0 ? nd : a;
  return function(l, c, u) {
    u === void 0 && (u = s);
    var d = {
      placement: "bottom",
      orderedModifiers: [],
      options: Object.assign({}, nd, s),
      modifiersData: {},
      elements: {
        reference: l,
        popper: c
      },
      attributes: {},
      styles: {}
    }, f = [], p = !1, m = {
      state: d,
      setOptions: function(w) {
        var C = typeof w == "function" ? w(d.options) : w;
        h(), d.options = Object.assign({}, s, d.options, C), d.scrollParents = {
          reference: cr(l) ? jo(l) : l.contextElement ? jo(l.contextElement) : [],
          popper: jo(c)
        };
        var E = LT(zT([].concat(o, d.options.modifiers)));
        return d.orderedModifiers = E.filter(function(O) {
          return O.enabled;
        }), v(), m.update();
      },
      // Sync update – it will always be executed, even if not necessary. This
      // is useful for low frequency updates where sync behavior simplifies the
      // logic.
      // For high frequency updates (e.g. `resize` and `scroll` events), always
      // prefer the async Popper#update method
      forceUpdate: function() {
        if (!p) {
          var w = d.elements, C = w.reference, E = w.popper;
          if (rd(C, E)) {
            d.rects = {
              reference: FT(C, ma(E), d.options.strategy === "fixed"),
              popper: Ul(E)
            }, d.reset = !1, d.placement = d.options.placement, d.orderedModifiers.forEach(function(V) {
              return d.modifiersData[V.name] = Object.assign({}, V.data);
            });
            for (var O = 0; O < d.orderedModifiers.length; O++) {
              if (d.reset === !0) {
                d.reset = !1, O = -1;
                continue;
              }
              var T = d.orderedModifiers[O], P = T.fn, S = T.options, j = S === void 0 ? {} : S, $ = T.name;
              typeof P == "function" && (d = P({
                state: d,
                options: j,
                name: $,
                instance: m
              }) || d);
            }
          }
        }
      },
      // Async and optimistically optimized update – it will not be executed if
      // not necessary (debounced to run at most once-per-tick)
      update: BT(function() {
        return new Promise(function(y) {
          m.forceUpdate(), y(d);
        });
      }),
      destroy: function() {
        h(), p = !0;
      }
    };
    if (!rd(l, c))
      return m;
    m.setOptions(u).then(function(y) {
      !p && u.onFirstUpdate && u.onFirstUpdate(y);
    });
    function v() {
      d.orderedModifiers.forEach(function(y) {
        var w = y.name, C = y.options, E = C === void 0 ? {} : C, O = y.effect;
        if (typeof O == "function") {
          var T = O({
            state: d,
            name: w,
            instance: m,
            options: E
          }), P = function() {
          };
          f.push(T || P);
        }
      });
    }
    function h() {
      f.forEach(function(y) {
        return y();
      }), f = [];
    }
    return m;
  };
}
var UT = [fT, kT, dT, eT, DT, CT, IT, iT, ST], HT = /* @__PURE__ */ WT({
  defaultModifiers: UT
});
const Gf = "Popper";
function qT(e) {
  return Vf(Gf, e);
}
E0(Gf, ["root"]);
const YT = ["anchorEl", "children", "direction", "disablePortal", "modifiers", "open", "placement", "popperOptions", "popperRef", "slotProps", "slots", "TransitionProps", "ownerState"], KT = ["anchorEl", "children", "container", "direction", "disablePortal", "keepMounted", "modifiers", "open", "placement", "popperOptions", "popperRef", "style", "transition", "slotProps", "slots"];
function GT(e, t) {
  if (t === "ltr")
    return e;
  switch (e) {
    case "bottom-end":
      return "bottom-start";
    case "bottom-start":
      return "bottom-end";
    case "top-end":
      return "top-start";
    case "top-start":
      return "top-end";
    default:
      return e;
  }
}
function ns(e) {
  return typeof e == "function" ? e() : e;
}
function Ns(e) {
  return e.nodeType !== void 0;
}
function XT(e) {
  return !Ns(e);
}
const ZT = () => Se({
  root: ["root"]
}, Gx(qT)), JT = {}, QT = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o;
  const {
    anchorEl: a,
    children: s,
    direction: i,
    disablePortal: l,
    modifiers: c,
    open: u,
    placement: d,
    popperOptions: f,
    popperRef: p,
    slotProps: m = {},
    slots: v = {},
    TransitionProps: h
    // @ts-ignore internal logic
    // prevent from spreading to DOM, it can come from the parent component e.g. Select.
  } = t, y = ie(t, YT), w = g.useRef(null), C = Ke(w, n), E = g.useRef(null), O = Ke(E, p), T = g.useRef(O);
  ft(() => {
    T.current = O;
  }, [O]), g.useImperativeHandle(p, () => E.current, []);
  const P = GT(d, i), [S, j] = g.useState(P), [$, V] = g.useState(ns(a));
  g.useEffect(() => {
    E.current && E.current.forceUpdate();
  }), g.useEffect(() => {
    a && V(ns(a));
  }, [a]), ft(() => {
    if (!$ || !u)
      return;
    const D = (N) => {
      j(N.placement);
    };
    if (process.env.NODE_ENV !== "production" && $ && Ns($) && $.nodeType === 1) {
      const N = $.getBoundingClientRect();
      process.env.NODE_ENV !== "test" && N.top === 0 && N.left === 0 && N.right === 0 && N.bottom === 0 && console.warn(["MUI: The `anchorEl` prop provided to the component is invalid.", "The anchor element should be part of the document layout.", "Make sure the element is present in the document or that it's not display none."].join(`
`));
    }
    let F = [{
      name: "preventOverflow",
      options: {
        altBoundary: l
      }
    }, {
      name: "flip",
      options: {
        altBoundary: l
      }
    }, {
      name: "onUpdate",
      enabled: !0,
      phase: "afterWrite",
      fn: ({
        state: N
      }) => {
        D(N);
      }
    }];
    c != null && (F = F.concat(c)), f && f.modifiers != null && (F = F.concat(f.modifiers));
    const z = HT($, w.current, b({
      placement: P
    }, f, {
      modifiers: F
    }));
    return T.current(z), () => {
      z.destroy(), T.current(null);
    };
  }, [$, l, c, u, f, P]);
  const _ = {
    placement: S
  };
  h !== null && (_.TransitionProps = h);
  const L = ZT(), M = (o = v.root) != null ? o : "div", R = Ye({
    elementType: M,
    externalSlotProps: m.root,
    externalForwardedProps: y,
    additionalProps: {
      role: "tooltip",
      ref: C
    },
    ownerState: t,
    className: L.root
  });
  return /* @__PURE__ */ x.jsx(M, b({}, R, {
    children: typeof s == "function" ? s(_) : s
  }));
}), Xf = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const {
    anchorEl: o,
    children: a,
    container: s,
    direction: i = "ltr",
    disablePortal: l = !1,
    keepMounted: c = !1,
    modifiers: u,
    open: d,
    placement: f = "bottom",
    popperOptions: p = JT,
    popperRef: m,
    style: v,
    transition: h = !1,
    slotProps: y = {},
    slots: w = {}
  } = t, C = ie(t, KT), [E, O] = g.useState(!0), T = () => {
    O(!1);
  }, P = () => {
    O(!0);
  };
  if (!c && !d && (!h || E))
    return null;
  let S;
  if (s)
    S = s;
  else if (o) {
    const V = ns(o);
    S = V && Ns(V) ? dt(V).body : dt(null).body;
  }
  const j = !d && c && (!h || E) ? "none" : void 0, $ = h ? {
    in: d,
    onEnter: T,
    onExited: P
  } : void 0;
  return /* @__PURE__ */ x.jsx(Go, {
    disablePortal: l,
    container: S,
    children: /* @__PURE__ */ x.jsx(QT, b({
      anchorEl: o,
      direction: i,
      disablePortal: l,
      modifiers: u,
      ref: n,
      open: h ? !E : d,
      placement: f,
      popperOptions: p,
      popperRef: m,
      slotProps: y,
      slots: w
    }, C, {
      style: b({
        // Prevents scroll issue, waiting for Popper.js to add this style once initiated.
        position: "fixed",
        // Fix Popper.js display issue
        top: 0,
        left: 0,
        display: j
      }, v),
      TransitionProps: $,
      children: a
    }))
  });
});
process.env.NODE_ENV !== "production" && (Xf.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │ To update them, edit the TypeScript types and run `pnpm proptypes`. │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * An HTML element, [virtualElement](https://popper.js.org/docs/v2/virtual-elements/),
   * or a function that returns either.
   * It's used to set the position of the popper.
   * The return value will passed as the reference object of the Popper instance.
   */
  anchorEl: Sn(r.oneOfType([Tn, r.object, r.func]), (e) => {
    if (e.open) {
      const t = ns(e.anchorEl);
      if (t && Ns(t) && t.nodeType === 1) {
        const n = t.getBoundingClientRect();
        if (process.env.NODE_ENV !== "test" && n.top === 0 && n.left === 0 && n.right === 0 && n.bottom === 0)
          return new Error(["MUI: The `anchorEl` prop provided to the component is invalid.", "The anchor element should be part of the document layout.", "Make sure the element is present in the document or that it's not display none."].join(`
`));
      } else if (!t || typeof t.getBoundingClientRect != "function" || XT(t) && t.contextElement != null && t.contextElement.nodeType !== 1)
        return new Error(["MUI: The `anchorEl` prop provided to the component is invalid.", "It should be an HTML element instance or a virtualElement ", "(https://popper.js.org/docs/v2/virtual-elements/)."].join(`
`));
    }
    return null;
  }),
  /**
   * Popper render function or node.
   */
  children: r.oneOfType([r.node, r.func]),
  /**
   * An HTML element or function that returns one.
   * The `container` will have the portal children appended to it.
   *
   * You can also provide a callback, which is called in a React layout effect.
   * This lets you set the container from a ref, and also makes server-side rendering possible.
   *
   * By default, it uses the body of the top-level document object,
   * so it's simply `document.body` most of the time.
   */
  container: r.oneOfType([Tn, r.func]),
  /**
   * Direction of the text.
   * @default 'ltr'
   */
  direction: r.oneOf(["ltr", "rtl"]),
  /**
   * The `children` will be under the DOM hierarchy of the parent component.
   * @default false
   */
  disablePortal: r.bool,
  /**
   * Always keep the children in the DOM.
   * This prop can be useful in SEO situation or
   * when you want to maximize the responsiveness of the Popper.
   * @default false
   */
  keepMounted: r.bool,
  /**
   * Popper.js is based on a "plugin-like" architecture,
   * most of its features are fully encapsulated "modifiers".
   *
   * A modifier is a function that is called each time Popper.js needs to
   * compute the position of the popper.
   * For this reason, modifiers should be very performant to avoid bottlenecks.
   * To learn how to create a modifier, [read the modifiers documentation](https://popper.js.org/docs/v2/modifiers/).
   */
  modifiers: r.arrayOf(r.shape({
    data: r.object,
    effect: r.func,
    enabled: r.bool,
    fn: r.func,
    name: r.any,
    options: r.object,
    phase: r.oneOf(["afterMain", "afterRead", "afterWrite", "beforeMain", "beforeRead", "beforeWrite", "main", "read", "write"]),
    requires: r.arrayOf(r.string),
    requiresIfExists: r.arrayOf(r.string)
  })),
  /**
   * If `true`, the component is shown.
   */
  open: r.bool.isRequired,
  /**
   * Popper placement.
   * @default 'bottom'
   */
  placement: r.oneOf(["auto-end", "auto-start", "auto", "bottom-end", "bottom-start", "bottom", "left-end", "left-start", "left", "right-end", "right-start", "right", "top-end", "top-start", "top"]),
  /**
   * Options provided to the [`Popper.js`](https://popper.js.org/docs/v2/constructors/#options) instance.
   * @default {}
   */
  popperOptions: r.shape({
    modifiers: r.array,
    onFirstUpdate: r.func,
    placement: r.oneOf(["auto-end", "auto-start", "auto", "bottom-end", "bottom-start", "bottom", "left-end", "left-start", "left", "right-end", "right-start", "right", "top-end", "top-start", "top"]),
    strategy: r.oneOf(["absolute", "fixed"])
  }),
  /**
   * A ref that points to the used popper instance.
   */
  popperRef: vt,
  /**
   * The props used for each slot inside the Popper.
   * @default {}
   */
  slotProps: r.shape({
    root: r.oneOfType([r.func, r.object])
  }),
  /**
   * The components used for each slot inside the Popper.
   * Either a string to use a HTML element or a component.
   * @default {}
   */
  slots: r.shape({
    root: r.elementType
  }),
  /**
   * Help supporting a react-transition-group/Transition component.
   * @default false
   */
  transition: r.bool
});
const ew = ["onChange", "maxRows", "minRows", "style", "value"];
function Pa(e) {
  return parseInt(e, 10) || 0;
}
const tw = {
  shadow: {
    // Visibility needed to hide the extra text area on iPads
    visibility: "hidden",
    // Remove from the content flow
    position: "absolute",
    // Ignore the scrollbar width
    overflow: "hidden",
    height: 0,
    top: 0,
    left: 0,
    // Create a new layer, increase the isolation of the computed values
    transform: "translateZ(0)"
  }
};
function nw(e) {
  return e == null || Object.keys(e).length === 0 || e.outerHeightStyle === 0 && !e.overflowing;
}
const Zf = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const {
    onChange: o,
    maxRows: a,
    minRows: s = 1,
    style: i,
    value: l
  } = t, c = ie(t, ew), {
    current: u
  } = g.useRef(l != null), d = g.useRef(null), f = Ke(n, d), p = g.useRef(null), m = g.useCallback(() => {
    const y = d.current, C = Fn(y).getComputedStyle(y);
    if (C.width === "0px")
      return {
        outerHeightStyle: 0,
        overflowing: !1
      };
    const E = p.current;
    E.style.width = C.width, E.value = y.value || t.placeholder || "x", E.value.slice(-1) === `
` && (E.value += " ");
    const O = C.boxSizing, T = Pa(C.paddingBottom) + Pa(C.paddingTop), P = Pa(C.borderBottomWidth) + Pa(C.borderTopWidth), S = E.scrollHeight;
    E.value = "x";
    const j = E.scrollHeight;
    let $ = S;
    s && ($ = Math.max(Number(s) * j, $)), a && ($ = Math.min(Number(a) * j, $)), $ = Math.max($, j);
    const V = $ + (O === "border-box" ? T + P : 0), _ = Math.abs($ - S) <= 1;
    return {
      outerHeightStyle: V,
      overflowing: _
    };
  }, [a, s, t.placeholder]), v = g.useCallback(() => {
    const y = m();
    if (nw(y))
      return;
    const w = d.current;
    w.style.height = `${y.outerHeightStyle}px`, w.style.overflow = y.overflowing ? "hidden" : "";
  }, [m]);
  ft(() => {
    const y = () => {
      v();
    };
    let w;
    const C = () => {
      cancelAnimationFrame(w), w = requestAnimationFrame(() => {
        y();
      });
    }, E = yl(y), O = d.current, T = Fn(O);
    T.addEventListener("resize", E);
    let P;
    return typeof ResizeObserver < "u" && (P = new ResizeObserver(process.env.NODE_ENV === "test" ? C : y), P.observe(O)), () => {
      E.clear(), cancelAnimationFrame(w), T.removeEventListener("resize", E), P && P.disconnect();
    };
  }, [m, v]), ft(() => {
    v();
  });
  const h = (y) => {
    u || v(), o && o(y);
  };
  return /* @__PURE__ */ x.jsxs(g.Fragment, {
    children: [/* @__PURE__ */ x.jsx("textarea", b({
      value: l,
      onChange: h,
      ref: f,
      rows: s,
      style: i
    }, c)), /* @__PURE__ */ x.jsx("textarea", {
      "aria-hidden": !0,
      className: t.className,
      readOnly: !0,
      ref: p,
      tabIndex: -1,
      style: b({}, tw.shadow, i, {
        paddingTop: 0,
        paddingBottom: 0
      })
    })]
  });
});
process.env.NODE_ENV !== "production" && (Zf.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │ To update them, edit the TypeScript types and run `pnpm proptypes`. │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * @ignore
   */
  className: r.string,
  /**
   * Maximum number of rows to display.
   */
  maxRows: r.oneOfType([r.number, r.string]),
  /**
   * Minimum number of rows to display.
   * @default 1
   */
  minRows: r.oneOfType([r.number, r.string]),
  /**
   * @ignore
   */
  onChange: r.func,
  /**
   * @ignore
   */
  placeholder: r.string,
  /**
   * @ignore
   */
  style: r.object,
  /**
   * @ignore
   */
  value: r.oneOfType([r.arrayOf(r.string), r.number, r.string])
});
var Gl = {};
Object.defineProperty(Gl, "__esModule", {
  value: !0
});
var Jf = Gl.default = void 0, rw = aw(Ct), ow = Ef;
function Qf(e) {
  if (typeof WeakMap != "function") return null;
  var t = /* @__PURE__ */ new WeakMap(), n = /* @__PURE__ */ new WeakMap();
  return (Qf = function(o) {
    return o ? n : t;
  })(e);
}
function aw(e, t) {
  if (e && e.__esModule) return e;
  if (e === null || typeof e != "object" && typeof e != "function") return { default: e };
  var n = Qf(t);
  if (n && n.has(e)) return n.get(e);
  var o = { __proto__: null }, a = Object.defineProperty && Object.getOwnPropertyDescriptor;
  for (var s in e) if (s !== "default" && Object.prototype.hasOwnProperty.call(e, s)) {
    var i = a ? Object.getOwnPropertyDescriptor(e, s) : null;
    i && (i.get || i.set) ? Object.defineProperty(o, s, i) : o[s] = e[s];
  }
  return o.default = e, n && n.set(e, o), o;
}
function sw(e) {
  return Object.keys(e).length === 0;
}
function iw(e = null) {
  const t = rw.useContext(ow.ThemeContext);
  return !t || sw(t) ? e : t;
}
Jf = Gl.default = iw;
const lw = ["anchorEl", "component", "components", "componentsProps", "container", "disablePortal", "keepMounted", "modifiers", "open", "placement", "popperOptions", "popperRef", "transition", "slots", "slotProps"], cw = Z(Xf, {
  name: "MuiPopper",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({}), js = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o;
  const a = Jf(), s = Ee({
    props: t,
    name: "MuiPopper"
  }), {
    anchorEl: i,
    component: l,
    components: c,
    componentsProps: u,
    container: d,
    disablePortal: f,
    keepMounted: p,
    modifiers: m,
    open: v,
    placement: h,
    popperOptions: y,
    popperRef: w,
    transition: C,
    slots: E,
    slotProps: O
  } = s, T = ie(s, lw), P = (o = E == null ? void 0 : E.root) != null ? o : c == null ? void 0 : c.Root, S = b({
    anchorEl: i,
    container: d,
    disablePortal: f,
    keepMounted: p,
    modifiers: m,
    open: v,
    placement: h,
    popperOptions: y,
    popperRef: w,
    transition: C
  }, T);
  return /* @__PURE__ */ x.jsx(cw, b({
    as: l,
    direction: a == null ? void 0 : a.direction,
    slots: {
      root: P
    },
    slotProps: O ?? u
  }, S, {
    ref: n
  }));
});
process.env.NODE_ENV !== "production" && (js.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │ To update them, edit the TypeScript types and run `pnpm proptypes`. │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * An HTML element, [virtualElement](https://popper.js.org/docs/v2/virtual-elements/),
   * or a function that returns either.
   * It's used to set the position of the popper.
   * The return value will passed as the reference object of the Popper instance.
   */
  anchorEl: r.oneOfType([Tn, r.object, r.func]),
  /**
   * Popper render function or node.
   */
  children: r.oneOfType([r.node, r.func]),
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The components used for each slot inside the Popper.
   * Either a string to use a HTML element or a component.
   * @default {}
   */
  components: r.shape({
    Root: r.elementType
  }),
  /**
   * The props used for each slot inside the Popper.
   * @default {}
   */
  componentsProps: r.shape({
    root: r.oneOfType([r.func, r.object])
  }),
  /**
   * An HTML element or function that returns one.
   * The `container` will have the portal children appended to it.
   *
   * You can also provide a callback, which is called in a React layout effect.
   * This lets you set the container from a ref, and also makes server-side rendering possible.
   *
   * By default, it uses the body of the top-level document object,
   * so it's simply `document.body` most of the time.
   */
  container: r.oneOfType([Tn, r.func]),
  /**
   * The `children` will be under the DOM hierarchy of the parent component.
   * @default false
   */
  disablePortal: r.bool,
  /**
   * Always keep the children in the DOM.
   * This prop can be useful in SEO situation or
   * when you want to maximize the responsiveness of the Popper.
   * @default false
   */
  keepMounted: r.bool,
  /**
   * Popper.js is based on a "plugin-like" architecture,
   * most of its features are fully encapsulated "modifiers".
   *
   * A modifier is a function that is called each time Popper.js needs to
   * compute the position of the popper.
   * For this reason, modifiers should be very performant to avoid bottlenecks.
   * To learn how to create a modifier, [read the modifiers documentation](https://popper.js.org/docs/v2/modifiers/).
   */
  modifiers: r.arrayOf(r.shape({
    data: r.object,
    effect: r.func,
    enabled: r.bool,
    fn: r.func,
    name: r.any,
    options: r.object,
    phase: r.oneOf(["afterMain", "afterRead", "afterWrite", "beforeMain", "beforeRead", "beforeWrite", "main", "read", "write"]),
    requires: r.arrayOf(r.string),
    requiresIfExists: r.arrayOf(r.string)
  })),
  /**
   * If `true`, the component is shown.
   */
  open: r.bool.isRequired,
  /**
   * Popper placement.
   * @default 'bottom'
   */
  placement: r.oneOf(["auto-end", "auto-start", "auto", "bottom-end", "bottom-start", "bottom", "left-end", "left-start", "left", "right-end", "right-start", "right", "top-end", "top-start", "top"]),
  /**
   * Options provided to the [`Popper.js`](https://popper.js.org/docs/v2/constructors/#options) instance.
   * @default {}
   */
  popperOptions: r.shape({
    modifiers: r.array,
    onFirstUpdate: r.func,
    placement: r.oneOf(["auto-end", "auto-start", "auto", "bottom-end", "bottom-start", "bottom", "left-end", "left-start", "left", "right-end", "right-start", "right", "top-end", "top-start", "top"]),
    strategy: r.oneOf(["absolute", "fixed"])
  }),
  /**
   * A ref that points to the used popper instance.
   */
  popperRef: vt,
  /**
   * The props used for each slot inside the Popper.
   * @default {}
   */
  slotProps: r.shape({
    root: r.oneOfType([r.func, r.object])
  }),
  /**
   * The components used for each slot inside the Popper.
   * Either a string to use a HTML element or a component.
   * @default {}
   */
  slots: r.shape({
    root: r.elementType
  }),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Help supporting a react-transition-group/Transition component.
   * @default false
   */
  transition: r.bool
});
const uw = rn(/* @__PURE__ */ x.jsx("path", {
  d: "M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z"
}), "Cancel");
function dw(e) {
  return Pe("MuiChip", e);
}
const Ae = Ce("MuiChip", ["root", "sizeSmall", "sizeMedium", "colorError", "colorInfo", "colorPrimary", "colorSecondary", "colorSuccess", "colorWarning", "disabled", "clickable", "clickableColorPrimary", "clickableColorSecondary", "deletable", "deletableColorPrimary", "deletableColorSecondary", "outlined", "filled", "outlinedPrimary", "outlinedSecondary", "filledPrimary", "filledSecondary", "avatar", "avatarSmall", "avatarMedium", "avatarColorPrimary", "avatarColorSecondary", "icon", "iconSmall", "iconMedium", "iconColorPrimary", "iconColorSecondary", "label", "labelSmall", "labelMedium", "deleteIcon", "deleteIconSmall", "deleteIconMedium", "deleteIconColorPrimary", "deleteIconColorSecondary", "deleteIconOutlinedColorPrimary", "deleteIconOutlinedColorSecondary", "deleteIconFilledColorPrimary", "deleteIconFilledColorSecondary", "focusVisible"]), pw = ["avatar", "className", "clickable", "color", "component", "deleteIcon", "disabled", "icon", "label", "onClick", "onDelete", "onKeyDown", "onKeyUp", "size", "variant", "tabIndex", "skipFocusWhenDisabled"], fw = (e) => {
  const {
    classes: t,
    disabled: n,
    size: o,
    color: a,
    iconColor: s,
    onDelete: i,
    clickable: l,
    variant: c
  } = e, u = {
    root: ["root", c, n && "disabled", `size${de(o)}`, `color${de(a)}`, l && "clickable", l && `clickableColor${de(a)}`, i && "deletable", i && `deletableColor${de(a)}`, `${c}${de(a)}`],
    label: ["label", `label${de(o)}`],
    avatar: ["avatar", `avatar${de(o)}`, `avatarColor${de(a)}`],
    icon: ["icon", `icon${de(o)}`, `iconColor${de(s)}`],
    deleteIcon: ["deleteIcon", `deleteIcon${de(o)}`, `deleteIconColor${de(a)}`, `deleteIcon${de(c)}Color${de(a)}`]
  };
  return Se(u, dw, t);
}, mw = Z("div", {
  name: "MuiChip",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e, {
      color: o,
      iconColor: a,
      clickable: s,
      onDelete: i,
      size: l,
      variant: c
    } = n;
    return [{
      [`& .${Ae.avatar}`]: t.avatar
    }, {
      [`& .${Ae.avatar}`]: t[`avatar${de(l)}`]
    }, {
      [`& .${Ae.avatar}`]: t[`avatarColor${de(o)}`]
    }, {
      [`& .${Ae.icon}`]: t.icon
    }, {
      [`& .${Ae.icon}`]: t[`icon${de(l)}`]
    }, {
      [`& .${Ae.icon}`]: t[`iconColor${de(a)}`]
    }, {
      [`& .${Ae.deleteIcon}`]: t.deleteIcon
    }, {
      [`& .${Ae.deleteIcon}`]: t[`deleteIcon${de(l)}`]
    }, {
      [`& .${Ae.deleteIcon}`]: t[`deleteIconColor${de(o)}`]
    }, {
      [`& .${Ae.deleteIcon}`]: t[`deleteIcon${de(c)}Color${de(o)}`]
    }, t.root, t[`size${de(l)}`], t[`color${de(o)}`], s && t.clickable, s && o !== "default" && t[`clickableColor${de(o)})`], i && t.deletable, i && o !== "default" && t[`deletableColor${de(o)}`], t[c], t[`${c}${de(o)}`]];
  }
})(({
  theme: e,
  ownerState: t
}) => {
  const n = e.palette.mode === "light" ? e.palette.grey[700] : e.palette.grey[300];
  return b({
    maxWidth: "100%",
    fontFamily: e.typography.fontFamily,
    fontSize: e.typography.pxToRem(13),
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    height: 32,
    color: (e.vars || e).palette.text.primary,
    backgroundColor: (e.vars || e).palette.action.selected,
    borderRadius: 32 / 2,
    whiteSpace: "nowrap",
    transition: e.transitions.create(["background-color", "box-shadow"]),
    // reset cursor explicitly in case ButtonBase is used
    cursor: "unset",
    // We disable the focus ring for mouse, touch and keyboard users.
    outline: 0,
    textDecoration: "none",
    border: 0,
    // Remove `button` border
    padding: 0,
    // Remove `button` padding
    verticalAlign: "middle",
    boxSizing: "border-box",
    [`&.${Ae.disabled}`]: {
      opacity: (e.vars || e).palette.action.disabledOpacity,
      pointerEvents: "none"
    },
    [`& .${Ae.avatar}`]: {
      marginLeft: 5,
      marginRight: -6,
      width: 24,
      height: 24,
      color: e.vars ? e.vars.palette.Chip.defaultAvatarColor : n,
      fontSize: e.typography.pxToRem(12)
    },
    [`& .${Ae.avatarColorPrimary}`]: {
      color: (e.vars || e).palette.primary.contrastText,
      backgroundColor: (e.vars || e).palette.primary.dark
    },
    [`& .${Ae.avatarColorSecondary}`]: {
      color: (e.vars || e).palette.secondary.contrastText,
      backgroundColor: (e.vars || e).palette.secondary.dark
    },
    [`& .${Ae.avatarSmall}`]: {
      marginLeft: 4,
      marginRight: -4,
      width: 18,
      height: 18,
      fontSize: e.typography.pxToRem(10)
    },
    [`& .${Ae.icon}`]: b({
      marginLeft: 5,
      marginRight: -6
    }, t.size === "small" && {
      fontSize: 18,
      marginLeft: 4,
      marginRight: -4
    }, t.iconColor === t.color && b({
      color: e.vars ? e.vars.palette.Chip.defaultIconColor : n
    }, t.color !== "default" && {
      color: "inherit"
    })),
    [`& .${Ae.deleteIcon}`]: b({
      WebkitTapHighlightColor: "transparent",
      color: e.vars ? `rgba(${e.vars.palette.text.primaryChannel} / 0.26)` : qe(e.palette.text.primary, 0.26),
      fontSize: 22,
      cursor: "pointer",
      margin: "0 5px 0 -6px",
      "&:hover": {
        color: e.vars ? `rgba(${e.vars.palette.text.primaryChannel} / 0.4)` : qe(e.palette.text.primary, 0.4)
      }
    }, t.size === "small" && {
      fontSize: 16,
      marginRight: 4,
      marginLeft: -4
    }, t.color !== "default" && {
      color: e.vars ? `rgba(${e.vars.palette[t.color].contrastTextChannel} / 0.7)` : qe(e.palette[t.color].contrastText, 0.7),
      "&:hover, &:active": {
        color: (e.vars || e).palette[t.color].contrastText
      }
    })
  }, t.size === "small" && {
    height: 24
  }, t.color !== "default" && {
    backgroundColor: (e.vars || e).palette[t.color].main,
    color: (e.vars || e).palette[t.color].contrastText
  }, t.onDelete && {
    [`&.${Ae.focusVisible}`]: {
      backgroundColor: e.vars ? `rgba(${e.vars.palette.action.selectedChannel} / calc(${e.vars.palette.action.selectedOpacity} + ${e.vars.palette.action.focusOpacity}))` : qe(e.palette.action.selected, e.palette.action.selectedOpacity + e.palette.action.focusOpacity)
    }
  }, t.onDelete && t.color !== "default" && {
    [`&.${Ae.focusVisible}`]: {
      backgroundColor: (e.vars || e).palette[t.color].dark
    }
  });
}, ({
  theme: e,
  ownerState: t
}) => b({}, t.clickable && {
  userSelect: "none",
  WebkitTapHighlightColor: "transparent",
  cursor: "pointer",
  "&:hover": {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.action.selectedChannel} / calc(${e.vars.palette.action.selectedOpacity} + ${e.vars.palette.action.hoverOpacity}))` : qe(e.palette.action.selected, e.palette.action.selectedOpacity + e.palette.action.hoverOpacity)
  },
  [`&.${Ae.focusVisible}`]: {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.action.selectedChannel} / calc(${e.vars.palette.action.selectedOpacity} + ${e.vars.palette.action.focusOpacity}))` : qe(e.palette.action.selected, e.palette.action.selectedOpacity + e.palette.action.focusOpacity)
  },
  "&:active": {
    boxShadow: (e.vars || e).shadows[1]
  }
}, t.clickable && t.color !== "default" && {
  [`&:hover, &.${Ae.focusVisible}`]: {
    backgroundColor: (e.vars || e).palette[t.color].dark
  }
}), ({
  theme: e,
  ownerState: t
}) => b({}, t.variant === "outlined" && {
  backgroundColor: "transparent",
  border: e.vars ? `1px solid ${e.vars.palette.Chip.defaultBorder}` : `1px solid ${e.palette.mode === "light" ? e.palette.grey[400] : e.palette.grey[700]}`,
  [`&.${Ae.clickable}:hover`]: {
    backgroundColor: (e.vars || e).palette.action.hover
  },
  [`&.${Ae.focusVisible}`]: {
    backgroundColor: (e.vars || e).palette.action.focus
  },
  [`& .${Ae.avatar}`]: {
    marginLeft: 4
  },
  [`& .${Ae.avatarSmall}`]: {
    marginLeft: 2
  },
  [`& .${Ae.icon}`]: {
    marginLeft: 4
  },
  [`& .${Ae.iconSmall}`]: {
    marginLeft: 2
  },
  [`& .${Ae.deleteIcon}`]: {
    marginRight: 5
  },
  [`& .${Ae.deleteIconSmall}`]: {
    marginRight: 3
  }
}, t.variant === "outlined" && t.color !== "default" && {
  color: (e.vars || e).palette[t.color].main,
  border: `1px solid ${e.vars ? `rgba(${e.vars.palette[t.color].mainChannel} / 0.7)` : qe(e.palette[t.color].main, 0.7)}`,
  [`&.${Ae.clickable}:hover`]: {
    backgroundColor: e.vars ? `rgba(${e.vars.palette[t.color].mainChannel} / ${e.vars.palette.action.hoverOpacity})` : qe(e.palette[t.color].main, e.palette.action.hoverOpacity)
  },
  [`&.${Ae.focusVisible}`]: {
    backgroundColor: e.vars ? `rgba(${e.vars.palette[t.color].mainChannel} / ${e.vars.palette.action.focusOpacity})` : qe(e.palette[t.color].main, e.palette.action.focusOpacity)
  },
  [`& .${Ae.deleteIcon}`]: {
    color: e.vars ? `rgba(${e.vars.palette[t.color].mainChannel} / 0.7)` : qe(e.palette[t.color].main, 0.7),
    "&:hover, &:active": {
      color: (e.vars || e).palette[t.color].main
    }
  }
})), hw = Z("span", {
  name: "MuiChip",
  slot: "Label",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e, {
      size: o
    } = n;
    return [t.label, t[`label${de(o)}`]];
  }
})(({
  ownerState: e
}) => b({
  overflow: "hidden",
  textOverflow: "ellipsis",
  paddingLeft: 12,
  paddingRight: 12,
  whiteSpace: "nowrap"
}, e.variant === "outlined" && {
  paddingLeft: 11,
  paddingRight: 11
}, e.size === "small" && {
  paddingLeft: 8,
  paddingRight: 8
}, e.size === "small" && e.variant === "outlined" && {
  paddingLeft: 7,
  paddingRight: 7
}));
function od(e) {
  return e.key === "Backspace" || e.key === "Delete";
}
const em = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiChip"
  }), {
    avatar: a,
    className: s,
    clickable: i,
    color: l = "default",
    component: c,
    deleteIcon: u,
    disabled: d = !1,
    icon: f,
    label: p,
    onClick: m,
    onDelete: v,
    onKeyDown: h,
    onKeyUp: y,
    size: w = "medium",
    variant: C = "filled",
    tabIndex: E,
    skipFocusWhenDisabled: O = !1
    // TODO v6: Rename to `focusableWhenDisabled`.
  } = o, T = ie(o, pw), P = g.useRef(null), S = Ke(P, n), j = (q) => {
    q.stopPropagation(), v && v(q);
  }, $ = (q) => {
    q.currentTarget === q.target && od(q) && q.preventDefault(), h && h(q);
  }, V = (q) => {
    q.currentTarget === q.target && (v && od(q) ? v(q) : q.key === "Escape" && P.current && P.current.blur()), y && y(q);
  }, _ = i !== !1 && m ? !0 : i, L = _ || v ? lr : c || "div", M = b({}, o, {
    component: L,
    disabled: d,
    size: w,
    color: l,
    iconColor: /* @__PURE__ */ g.isValidElement(f) && f.props.color || l,
    onDelete: !!v,
    clickable: _,
    variant: C
  }), R = fw(M), D = L === lr ? b({
    component: c || "div",
    focusVisibleClassName: R.focusVisible
  }, v && {
    disableRipple: !0
  }) : {};
  let F = null;
  v && (F = u && /* @__PURE__ */ g.isValidElement(u) ? /* @__PURE__ */ g.cloneElement(u, {
    className: pe(u.props.className, R.deleteIcon),
    onClick: j
  }) : /* @__PURE__ */ x.jsx(uw, {
    className: pe(R.deleteIcon),
    onClick: j
  }));
  let z = null;
  a && /* @__PURE__ */ g.isValidElement(a) && (z = /* @__PURE__ */ g.cloneElement(a, {
    className: pe(R.avatar, a.props.className)
  }));
  let N = null;
  return f && /* @__PURE__ */ g.isValidElement(f) && (N = /* @__PURE__ */ g.cloneElement(f, {
    className: pe(R.icon, f.props.className)
  })), process.env.NODE_ENV !== "production" && z && N && console.error("MUI: The Chip component can not handle the avatar and the icon prop at the same time. Pick one."), /* @__PURE__ */ x.jsxs(mw, b({
    as: L,
    className: pe(R.root, s),
    disabled: _ && d ? !0 : void 0,
    onClick: m,
    onKeyDown: $,
    onKeyUp: V,
    ref: S,
    tabIndex: O && d ? -1 : E,
    ownerState: M
  }, D, T, {
    children: [z || N, /* @__PURE__ */ x.jsx(hw, {
      className: pe(R.label),
      ownerState: M,
      children: p
    }), F]
  }));
});
process.env.NODE_ENV !== "production" && (em.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The Avatar element to display.
   */
  avatar: r.element,
  /**
   * This prop isn't supported.
   * Use the `component` prop if you need to change the children structure.
   */
  children: Ip,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * If `true`, the chip will appear clickable, and will raise when pressed,
   * even if the onClick prop is not defined.
   * If `false`, the chip will not appear clickable, even if onClick prop is defined.
   * This can be used, for example,
   * along with the component prop to indicate an anchor Chip is clickable.
   * Note: this controls the UI and does not affect the onClick event.
   */
  clickable: r.bool,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * @default 'default'
   */
  color: r.oneOfType([r.oneOf(["default", "primary", "secondary", "error", "info", "success", "warning"]), r.string]),
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * Override the default delete icon element. Shown only if `onDelete` is set.
   */
  deleteIcon: r.element,
  /**
   * If `true`, the component is disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * Icon element.
   */
  icon: r.element,
  /**
   * The content of the component.
   */
  label: r.node,
  /**
   * @ignore
   */
  onClick: r.func,
  /**
   * Callback fired when the delete icon is clicked.
   * If set, the delete icon will be shown.
   */
  onDelete: r.func,
  /**
   * @ignore
   */
  onKeyDown: r.func,
  /**
   * @ignore
   */
  onKeyUp: r.func,
  /**
   * The size of the component.
   * @default 'medium'
   */
  size: r.oneOfType([r.oneOf(["medium", "small"]), r.string]),
  /**
   * If `true`, allows the disabled chip to escape focus.
   * If `false`, allows the disabled chip to receive focus.
   * @default false
   */
  skipFocusWhenDisabled: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * @ignore
   */
  tabIndex: r.number,
  /**
   * The variant to use.
   * @default 'filled'
   */
  variant: r.oneOfType([r.oneOf(["filled", "outlined"]), r.string])
});
function fo({
  props: e,
  states: t,
  muiFormControl: n
}) {
  return t.reduce((o, a) => (o[a] = e[a], n && typeof e[a] > "u" && (o[a] = n[a]), o), {});
}
const ha = /* @__PURE__ */ g.createContext(void 0);
process.env.NODE_ENV !== "production" && (ha.displayName = "FormControlContext");
function fr() {
  return g.useContext(ha);
}
function tm(e) {
  return /* @__PURE__ */ x.jsx(Pf, b({}, e, {
    defaultTheme: _s,
    themeId: ca
  }));
}
process.env.NODE_ENV !== "production" && (tm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The styles you want to apply globally.
   */
  styles: r.oneOfType([r.array, r.func, r.number, r.object, r.string, r.bool])
});
function ad(e) {
  return e != null && !(Array.isArray(e) && e.length === 0);
}
function rs(e, t = !1) {
  return e && (ad(e.value) && e.value !== "" || t && ad(e.defaultValue) && e.defaultValue !== "");
}
function bw(e) {
  return e.startAdornment;
}
function gw(e) {
  return Pe("MuiInputBase", e);
}
const no = Ce("MuiInputBase", ["root", "formControl", "focused", "disabled", "adornedStart", "adornedEnd", "error", "sizeSmall", "multiline", "colorSecondary", "fullWidth", "hiddenLabel", "readOnly", "input", "inputSizeSmall", "inputMultiline", "inputTypeSearch", "inputAdornedStart", "inputAdornedEnd", "inputHiddenLabel"]), yw = ["aria-describedby", "autoComplete", "autoFocus", "className", "color", "components", "componentsProps", "defaultValue", "disabled", "disableInjectingGlobalStyles", "endAdornment", "error", "fullWidth", "id", "inputComponent", "inputProps", "inputRef", "margin", "maxRows", "minRows", "multiline", "name", "onBlur", "onChange", "onClick", "onFocus", "onKeyDown", "onKeyUp", "placeholder", "readOnly", "renderSuffix", "rows", "size", "slotProps", "slots", "startAdornment", "type", "value"], As = (e, t) => {
  const {
    ownerState: n
  } = e;
  return [t.root, n.formControl && t.formControl, n.startAdornment && t.adornedStart, n.endAdornment && t.adornedEnd, n.error && t.error, n.size === "small" && t.sizeSmall, n.multiline && t.multiline, n.color && t[`color${de(n.color)}`], n.fullWidth && t.fullWidth, n.hiddenLabel && t.hiddenLabel];
}, Fs = (e, t) => {
  const {
    ownerState: n
  } = e;
  return [t.input, n.size === "small" && t.inputSizeSmall, n.multiline && t.inputMultiline, n.type === "search" && t.inputTypeSearch, n.startAdornment && t.inputAdornedStart, n.endAdornment && t.inputAdornedEnd, n.hiddenLabel && t.inputHiddenLabel];
}, vw = (e) => {
  const {
    classes: t,
    color: n,
    disabled: o,
    error: a,
    endAdornment: s,
    focused: i,
    formControl: l,
    fullWidth: c,
    hiddenLabel: u,
    multiline: d,
    readOnly: f,
    size: p,
    startAdornment: m,
    type: v
  } = e, h = {
    root: ["root", `color${de(n)}`, o && "disabled", a && "error", c && "fullWidth", i && "focused", l && "formControl", p && p !== "medium" && `size${de(p)}`, d && "multiline", m && "adornedStart", s && "adornedEnd", u && "hiddenLabel", f && "readOnly"],
    input: ["input", o && "disabled", v === "search" && "inputTypeSearch", d && "inputMultiline", p === "small" && "inputSizeSmall", u && "inputHiddenLabel", m && "inputAdornedStart", s && "inputAdornedEnd", f && "readOnly"]
  };
  return Se(h, gw, t);
}, Vs = Z("div", {
  name: "MuiInputBase",
  slot: "Root",
  overridesResolver: As
})(({
  theme: e,
  ownerState: t
}) => b({}, e.typography.body1, {
  color: (e.vars || e).palette.text.primary,
  lineHeight: "1.4375em",
  // 23px
  boxSizing: "border-box",
  // Prevent padding issue with fullWidth.
  position: "relative",
  cursor: "text",
  display: "inline-flex",
  alignItems: "center",
  [`&.${no.disabled}`]: {
    color: (e.vars || e).palette.text.disabled,
    cursor: "default"
  }
}, t.multiline && b({
  padding: "4px 0 5px"
}, t.size === "small" && {
  paddingTop: 1
}), t.fullWidth && {
  width: "100%"
})), Ls = Z("input", {
  name: "MuiInputBase",
  slot: "Input",
  overridesResolver: Fs
})(({
  theme: e,
  ownerState: t
}) => {
  const n = e.palette.mode === "light", o = b({
    color: "currentColor"
  }, e.vars ? {
    opacity: e.vars.opacity.inputPlaceholder
  } : {
    opacity: n ? 0.42 : 0.5
  }, {
    transition: e.transitions.create("opacity", {
      duration: e.transitions.duration.shorter
    })
  }), a = {
    opacity: "0 !important"
  }, s = e.vars ? {
    opacity: e.vars.opacity.inputPlaceholder
  } : {
    opacity: n ? 0.42 : 0.5
  };
  return b({
    font: "inherit",
    letterSpacing: "inherit",
    color: "currentColor",
    padding: "4px 0 5px",
    border: 0,
    boxSizing: "content-box",
    background: "none",
    height: "1.4375em",
    // Reset 23pxthe native input line-height
    margin: 0,
    // Reset for Safari
    WebkitTapHighlightColor: "transparent",
    display: "block",
    // Make the flex item shrink with Firefox
    minWidth: 0,
    width: "100%",
    // Fix IE11 width issue
    animationName: "mui-auto-fill-cancel",
    animationDuration: "10ms",
    "&::-webkit-input-placeholder": o,
    "&::-moz-placeholder": o,
    // Firefox 19+
    "&:-ms-input-placeholder": o,
    // IE11
    "&::-ms-input-placeholder": o,
    // Edge
    "&:focus": {
      outline: 0
    },
    // Reset Firefox invalid required input style
    "&:invalid": {
      boxShadow: "none"
    },
    "&::-webkit-search-decoration": {
      // Remove the padding when type=search.
      WebkitAppearance: "none"
    },
    // Show and hide the placeholder logic
    [`label[data-shrink=false] + .${no.formControl} &`]: {
      "&::-webkit-input-placeholder": a,
      "&::-moz-placeholder": a,
      // Firefox 19+
      "&:-ms-input-placeholder": a,
      // IE11
      "&::-ms-input-placeholder": a,
      // Edge
      "&:focus::-webkit-input-placeholder": s,
      "&:focus::-moz-placeholder": s,
      // Firefox 19+
      "&:focus:-ms-input-placeholder": s,
      // IE11
      "&:focus::-ms-input-placeholder": s
      // Edge
    },
    [`&.${no.disabled}`]: {
      opacity: 1,
      // Reset iOS opacity
      WebkitTextFillColor: (e.vars || e).palette.text.disabled
      // Fix opacity Safari bug
    },
    "&:-webkit-autofill": {
      animationDuration: "5000s",
      animationName: "mui-auto-fill"
    }
  }, t.size === "small" && {
    paddingTop: 1
  }, t.multiline && {
    height: "auto",
    resize: "none",
    padding: 0,
    paddingTop: 0
  }, t.type === "search" && {
    // Improve type search style.
    MozAppearance: "textfield"
  });
}), xw = /* @__PURE__ */ x.jsx(tm, {
  styles: {
    "@keyframes mui-auto-fill": {
      from: {
        display: "block"
      }
    },
    "@keyframes mui-auto-fill-cancel": {
      from: {
        display: "block"
      }
    }
  }
}), nm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o;
  const a = Ee({
    props: t,
    name: "MuiInputBase"
  }), {
    "aria-describedby": s,
    autoComplete: i,
    autoFocus: l,
    className: c,
    components: u = {},
    componentsProps: d = {},
    defaultValue: f,
    disabled: p,
    disableInjectingGlobalStyles: m,
    endAdornment: v,
    fullWidth: h = !1,
    id: y,
    inputComponent: w = "input",
    inputProps: C = {},
    inputRef: E,
    maxRows: O,
    minRows: T,
    multiline: P = !1,
    name: S,
    onBlur: j,
    onChange: $,
    onClick: V,
    onFocus: _,
    onKeyDown: L,
    onKeyUp: M,
    placeholder: R,
    readOnly: D,
    renderSuffix: F,
    rows: z,
    slotProps: N = {},
    slots: q = {},
    startAdornment: A,
    type: H = "text",
    value: te
  } = a, re = ie(a, yw), B = C.value != null ? C.value : te, {
    current: G
  } = g.useRef(B != null), ee = g.useRef(), W = g.useCallback((xe) => {
    process.env.NODE_ENV !== "production" && xe && xe.nodeName !== "INPUT" && !xe.focus && console.error(["MUI: You have provided a `inputComponent` to the input component", "that does not correctly handle the `ref` prop.", "Make sure the `ref` prop is called with a HTMLInputElement."].join(`
`));
  }, []), J = Ke(ee, E, C.ref, W), [se, le] = g.useState(!1), X = fr();
  process.env.NODE_ENV !== "production" && g.useEffect(() => {
    if (X)
      return X.registerEffect();
  }, [X]);
  const U = fo({
    props: a,
    muiFormControl: X,
    states: ["color", "disabled", "error", "hiddenLabel", "size", "required", "filled"]
  });
  U.focused = X ? X.focused : se, g.useEffect(() => {
    !X && p && se && (le(!1), j && j());
  }, [X, p, se, j]);
  const K = X && X.onFilled, Y = X && X.onEmpty, he = g.useCallback((xe) => {
    rs(xe) ? K && K() : Y && Y();
  }, [K, Y]);
  ft(() => {
    G && he({
      value: B
    });
  }, [B, he, G]);
  const Oe = (xe) => {
    if (U.disabled) {
      xe.stopPropagation();
      return;
    }
    _ && _(xe), C.onFocus && C.onFocus(xe), X && X.onFocus ? X.onFocus(xe) : le(!0);
  }, Ne = (xe) => {
    j && j(xe), C.onBlur && C.onBlur(xe), X && X.onBlur ? X.onBlur(xe) : le(!1);
  }, fe = (xe, ...be) => {
    if (!G) {
      const _e = xe.target || ee.current;
      if (_e == null)
        throw new Error(process.env.NODE_ENV !== "production" ? "MUI: Expected valid input target. Did you use a custom `inputComponent` and forget to forward refs? See https://mui.com/r/input-component-ref-interface for more info." : xn(1));
      he({
        value: _e.value
      });
    }
    C.onChange && C.onChange(xe, ...be), $ && $(xe, ...be);
  };
  g.useEffect(() => {
    he(ee.current);
  }, []);
  const ve = (xe) => {
    ee.current && xe.currentTarget === xe.target && ee.current.focus(), V && V(xe);
  };
  let oe = w, ce = C;
  P && oe === "input" && (z ? (process.env.NODE_ENV !== "production" && (T || O) && console.warn("MUI: You can not use the `minRows` or `maxRows` props when the input `rows` prop is set."), ce = b({
    type: void 0,
    minRows: z,
    maxRows: z
  }, ce)) : ce = b({
    type: void 0,
    maxRows: O,
    minRows: T
  }, ce), oe = Zf);
  const I = (xe) => {
    he(xe.animationName === "mui-auto-fill-cancel" ? ee.current : {
      value: "x"
    });
  };
  g.useEffect(() => {
    X && X.setAdornedStart(!!A);
  }, [X, A]);
  const Q = b({}, a, {
    color: U.color || "primary",
    disabled: U.disabled,
    endAdornment: v,
    error: U.error,
    focused: U.focused,
    formControl: X,
    fullWidth: h,
    hiddenLabel: U.hiddenLabel,
    multiline: P,
    size: U.size,
    startAdornment: A,
    type: H
  }), ne = vw(Q), ue = q.root || u.Root || Vs, ge = N.root || d.root || {}, ye = q.input || u.Input || Ls;
  return ce = b({}, ce, (o = N.input) != null ? o : d.input), /* @__PURE__ */ x.jsxs(g.Fragment, {
    children: [!m && xw, /* @__PURE__ */ x.jsxs(ue, b({}, ge, !Zr(ue) && {
      ownerState: b({}, Q, ge.ownerState)
    }, {
      ref: n,
      onClick: ve
    }, re, {
      className: pe(ne.root, ge.className, c, D && "MuiInputBase-readOnly"),
      children: [A, /* @__PURE__ */ x.jsx(ha.Provider, {
        value: null,
        children: /* @__PURE__ */ x.jsx(ye, b({
          ownerState: Q,
          "aria-invalid": U.error,
          "aria-describedby": s,
          autoComplete: i,
          autoFocus: l,
          defaultValue: f,
          disabled: U.disabled,
          id: y,
          onAnimationStart: I,
          name: S,
          placeholder: R,
          readOnly: D,
          required: U.required,
          rows: z,
          value: B,
          onKeyDown: L,
          onKeyUp: M,
          type: H
        }, ce, !Zr(ye) && {
          as: oe,
          ownerState: b({}, Q, ce.ownerState)
        }, {
          ref: J,
          className: pe(ne.input, ce.className, D && "MuiInputBase-readOnly"),
          onBlur: Ne,
          onChange: fe,
          onFocus: Oe
        }))
      }), v, F ? F(b({}, U, {
        startAdornment: A
      })) : null]
    }))]
  });
});
process.env.NODE_ENV !== "production" && (nm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * @ignore
   */
  "aria-describedby": r.string,
  /**
   * This prop helps users to fill forms faster, especially on mobile devices.
   * The name can be confusing, as it's more like an autofill.
   * You can learn more about it [following the specification](https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofill).
   */
  autoComplete: r.string,
  /**
   * If `true`, the `input` element is focused during the first mount.
   */
  autoFocus: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * The prop defaults to the value (`'primary'`) inherited from the parent FormControl component.
   */
  color: r.oneOfType([r.oneOf(["primary", "secondary", "error", "info", "success", "warning"]), r.string]),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `slots` prop.
   * It's recommended to use the `slots` prop instead.
   *
   * @default {}
   */
  components: r.shape({
    Input: r.elementType,
    Root: r.elementType
  }),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `slotProps` prop.
   * It's recommended to use the `slotProps` prop instead, as `componentsProps` will be deprecated in the future.
   *
   * @default {}
   */
  componentsProps: r.shape({
    input: r.object,
    root: r.object
  }),
  /**
   * The default value. Use when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the component is disabled.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  disabled: r.bool,
  /**
   * If `true`, GlobalStyles for the auto-fill keyframes will not be injected/removed on mount/unmount. Make sure to inject them at the top of your application.
   * This option is intended to help with boosting the initial rendering performance if you are loading a big amount of Input components at once.
   * @default false
   */
  disableInjectingGlobalStyles: r.bool,
  /**
   * End `InputAdornment` for this component.
   */
  endAdornment: r.node,
  /**
   * If `true`, the `input` will indicate an error.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  error: r.bool,
  /**
   * If `true`, the `input` will take up the full width of its container.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * The id of the `input` element.
   */
  id: r.string,
  /**
   * The component used for the `input` element.
   * Either a string to use a HTML element or a component.
   * @default 'input'
   */
  inputComponent: hs,
  /**
   * [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Attributes) applied to the `input` element.
   * @default {}
   */
  inputProps: r.object,
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * If `dense`, will adjust vertical spacing. This is normally obtained via context from
   * FormControl.
   * The prop defaults to the value (`'none'`) inherited from the parent FormControl component.
   */
  margin: r.oneOf(["dense", "none"]),
  /**
   * Maximum number of rows to display when multiline option is set to true.
   */
  maxRows: r.oneOfType([r.number, r.string]),
  /**
   * Minimum number of rows to display when multiline option is set to true.
   */
  minRows: r.oneOfType([r.number, r.string]),
  /**
   * If `true`, a [TextareaAutosize](/material-ui/react-textarea-autosize/) element is rendered.
   * @default false
   */
  multiline: r.bool,
  /**
   * Name attribute of the `input` element.
   */
  name: r.string,
  /**
   * Callback fired when the `input` is blurred.
   *
   * Notice that the first argument (event) might be undefined.
   */
  onBlur: r.func,
  /**
   * Callback fired when the value is changed.
   *
   * @param {React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>} event The event source of the callback.
   * You can pull out the new value by accessing `event.target.value` (string).
   */
  onChange: r.func,
  /**
   * @ignore
   */
  onClick: r.func,
  /**
   * @ignore
   */
  onFocus: r.func,
  /**
   * Callback fired when the `input` doesn't satisfy its constraints.
   */
  onInvalid: r.func,
  /**
   * @ignore
   */
  onKeyDown: r.func,
  /**
   * @ignore
   */
  onKeyUp: r.func,
  /**
   * The short hint displayed in the `input` before the user enters a value.
   */
  placeholder: r.string,
  /**
   * It prevents the user from changing the value of the field
   * (not from interacting with the field).
   */
  readOnly: r.bool,
  /**
   * @ignore
   */
  renderSuffix: r.func,
  /**
   * If `true`, the `input` element is required.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  required: r.bool,
  /**
   * Number of rows to display when multiline option is set to true.
   */
  rows: r.oneOfType([r.number, r.string]),
  /**
   * The size of the component.
   */
  size: r.oneOfType([r.oneOf(["medium", "small"]), r.string]),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `componentsProps` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slotProps: r.shape({
    input: r.object,
    root: r.object
  }),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `components` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slots: r.shape({
    input: r.elementType,
    root: r.elementType
  }),
  /**
   * Start `InputAdornment` for this component.
   */
  startAdornment: r.node,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Type of the `input` element. It should be [a valid HTML5 input type](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Form_%3Cinput%3E_types).
   * @default 'text'
   */
  type: r.string,
  /**
   * The value of the `input` element, required for a controlled component.
   */
  value: r.any
});
const Xl = nm;
function Tw(e) {
  return Pe("MuiInput", e);
}
const Eo = b({}, no, Ce("MuiInput", ["root", "underline", "input"]));
function ww(e) {
  return Pe("MuiOutlinedInput", e);
}
const kn = b({}, no, Ce("MuiOutlinedInput", ["root", "notchedOutline", "input"]));
function Ew(e) {
  return Pe("MuiFilledInput", e);
}
const Xn = b({}, no, Ce("MuiFilledInput", ["root", "underline", "input"])), Cw = rn(/* @__PURE__ */ x.jsx("path", {
  d: "M7 10l5 5 5-5z"
}), "ArrowDropDown"), Ow = ["addEndListener", "appear", "children", "easing", "in", "onEnter", "onEntered", "onEntering", "onExit", "onExited", "onExiting", "style", "timeout", "TransitionComponent"], Sw = {
  entering: {
    opacity: 1
  },
  entered: {
    opacity: 1
  }
}, mr = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Zt(), a = {
    enter: o.transitions.duration.enteringScreen,
    exit: o.transitions.duration.leavingScreen
  }, {
    addEndListener: s,
    appear: i = !0,
    children: l,
    easing: c,
    in: u,
    onEnter: d,
    onEntered: f,
    onEntering: p,
    onExit: m,
    onExited: v,
    onExiting: h,
    style: y,
    timeout: w = a,
    // eslint-disable-next-line react/prop-types
    TransitionComponent: C = Jt
  } = t, E = ie(t, Ow), O = g.useRef(null), T = Ke(O, l.ref, n), P = (R) => (D) => {
    if (R) {
      const F = O.current;
      D === void 0 ? R(F) : R(F, D);
    }
  }, S = P(p), j = P((R, D) => {
    Mf(R);
    const F = es({
      style: y,
      timeout: w,
      easing: c
    }, {
      mode: "enter"
    });
    R.style.webkitTransition = o.transitions.create("opacity", F), R.style.transition = o.transitions.create("opacity", F), d && d(R, D);
  }), $ = P(f), V = P(h), _ = P((R) => {
    const D = es({
      style: y,
      timeout: w,
      easing: c
    }, {
      mode: "exit"
    });
    R.style.webkitTransition = o.transitions.create("opacity", D), R.style.transition = o.transitions.create("opacity", D), m && m(R);
  }), L = P(v), M = (R) => {
    s && s(O.current, R);
  };
  return /* @__PURE__ */ x.jsx(C, b({
    appear: i,
    in: u,
    nodeRef: O,
    onEnter: j,
    onEntered: $,
    onEntering: S,
    onExit: _,
    onExited: L,
    onExiting: V,
    addEndListener: M,
    timeout: w
  }, E, {
    children: (R, D) => /* @__PURE__ */ g.cloneElement(l, b({
      style: b({
        opacity: 0,
        visibility: R === "exited" && !u ? "hidden" : void 0
      }, Sw[R], y, l.props.style),
      ref: T
    }, D))
  }));
});
process.env.NODE_ENV !== "production" && (mr.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Add a custom transition end trigger. Called with the transitioning DOM
   * node and a done callback. Allows for more fine grained transition end
   * logic. Note: Timeouts are still used as a fallback if provided.
   */
  addEndListener: r.func,
  /**
   * Perform the enter transition when it first mounts if `in` is also `true`.
   * Set this to `false` to disable this behavior.
   * @default true
   */
  appear: r.bool,
  /**
   * A single child content element.
   */
  children: ao.isRequired,
  /**
   * The transition timing function.
   * You may specify a single easing or a object containing enter and exit values.
   */
  easing: r.oneOfType([r.shape({
    enter: r.string,
    exit: r.string
  }), r.string]),
  /**
   * If `true`, the component will transition in.
   */
  in: r.bool,
  /**
   * @ignore
   */
  onEnter: r.func,
  /**
   * @ignore
   */
  onEntered: r.func,
  /**
   * @ignore
   */
  onEntering: r.func,
  /**
   * @ignore
   */
  onExit: r.func,
  /**
   * @ignore
   */
  onExited: r.func,
  /**
   * @ignore
   */
  onExiting: r.func,
  /**
   * @ignore
   */
  style: r.object,
  /**
   * The duration for the transition, in milliseconds.
   * You may specify a single timeout for all transitions, or individually with an object.
   * @default {
   *   enter: theme.transitions.duration.enteringScreen,
   *   exit: theme.transitions.duration.leavingScreen,
   * }
   */
  timeout: r.oneOfType([r.number, r.shape({
    appear: r.number,
    enter: r.number,
    exit: r.number
  })])
});
function Pw(e) {
  return Pe("MuiBackdrop", e);
}
Ce("MuiBackdrop", ["root", "invisible"]);
const Rw = ["children", "className", "component", "components", "componentsProps", "invisible", "open", "slotProps", "slots", "TransitionComponent", "transitionDuration"], Dw = (e) => {
  const {
    classes: t,
    invisible: n
  } = e;
  return Se({
    root: ["root", n && "invisible"]
  }, Pw, t);
}, $w = Z("div", {
  name: "MuiBackdrop",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.invisible && t.invisible];
  }
})(({
  ownerState: e
}) => b({
  position: "fixed",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  right: 0,
  bottom: 0,
  top: 0,
  left: 0,
  backgroundColor: "rgba(0, 0, 0, 0.5)",
  WebkitTapHighlightColor: "transparent"
}, e.invisible && {
  backgroundColor: "transparent"
})), Zl = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s;
  const i = Ee({
    props: t,
    name: "MuiBackdrop"
  }), {
    children: l,
    className: c,
    component: u = "div",
    components: d = {},
    componentsProps: f = {},
    invisible: p = !1,
    open: m,
    slotProps: v = {},
    slots: h = {},
    TransitionComponent: y = mr,
    transitionDuration: w
  } = i, C = ie(i, Rw), E = b({}, i, {
    component: u,
    invisible: p
  }), O = Dw(E), T = (o = v.root) != null ? o : f.root;
  return /* @__PURE__ */ x.jsx(y, b({
    in: m,
    timeout: w
  }, C, {
    children: /* @__PURE__ */ x.jsx($w, b({
      "aria-hidden": !0
    }, T, {
      as: (a = (s = h.root) != null ? s : d.Root) != null ? a : u,
      className: pe(O.root, c, T == null ? void 0 : T.className),
      ownerState: b({}, E, T == null ? void 0 : T.ownerState),
      classes: O,
      ref: n,
      children: l
    }))
  }));
});
process.env.NODE_ENV !== "production" && (Zl.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `slots` prop.
   * It's recommended to use the `slots` prop instead.
   *
   * @default {}
   */
  components: r.shape({
    Root: r.elementType
  }),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `slotProps` prop.
   * It's recommended to use the `slotProps` prop instead, as `componentsProps` will be deprecated in the future.
   *
   * @default {}
   */
  componentsProps: r.shape({
    root: r.object
  }),
  /**
   * If `true`, the backdrop is invisible.
   * It can be used when rendering a popover or a custom select component.
   * @default false
   */
  invisible: r.bool,
  /**
   * If `true`, the component is shown.
   */
  open: r.bool.isRequired,
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `componentsProps` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slotProps: r.shape({
    root: r.object
  }),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `components` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slots: r.shape({
    root: r.elementType
  }),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The component used for the transition.
   * [Follow this guide](/material-ui/transitions/#transitioncomponent-prop) to learn more about the requirements for this component.
   * @default Fade
   */
  TransitionComponent: r.elementType,
  /**
   * The duration for the transition, in milliseconds.
   * You may specify a single timeout for all transitions, or individually with an object.
   */
  transitionDuration: r.oneOfType([r.number, r.shape({
    appear: r.number,
    enter: r.number,
    exit: r.number
  })])
});
const kw = Ce("MuiBox", ["root"]), _w = wf(), tn = Ox({
  themeId: ca,
  defaultTheme: _w,
  defaultClassName: kw.root,
  generateClassName: Tl.generate
});
process.env.NODE_ENV !== "production" && (tn.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * @ignore
   */
  children: r.node,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function Mw(e) {
  return Pe("MuiButton", e);
}
const Ra = Ce("MuiButton", ["root", "text", "textInherit", "textPrimary", "textSecondary", "textSuccess", "textError", "textInfo", "textWarning", "outlined", "outlinedInherit", "outlinedPrimary", "outlinedSecondary", "outlinedSuccess", "outlinedError", "outlinedInfo", "outlinedWarning", "contained", "containedInherit", "containedPrimary", "containedSecondary", "containedSuccess", "containedError", "containedInfo", "containedWarning", "disableElevation", "focusVisible", "disabled", "colorInherit", "colorPrimary", "colorSecondary", "colorSuccess", "colorError", "colorInfo", "colorWarning", "textSizeSmall", "textSizeMedium", "textSizeLarge", "outlinedSizeSmall", "outlinedSizeMedium", "outlinedSizeLarge", "containedSizeSmall", "containedSizeMedium", "containedSizeLarge", "sizeMedium", "sizeSmall", "sizeLarge", "fullWidth", "startIcon", "endIcon", "icon", "iconSizeSmall", "iconSizeMedium", "iconSizeLarge"]), rm = /* @__PURE__ */ g.createContext({});
process.env.NODE_ENV !== "production" && (rm.displayName = "ButtonGroupContext");
const om = /* @__PURE__ */ g.createContext(void 0);
process.env.NODE_ENV !== "production" && (om.displayName = "ButtonGroupButtonContext");
const Iw = ["children", "color", "component", "className", "disabled", "disableElevation", "disableFocusRipple", "endIcon", "focusVisibleClassName", "fullWidth", "size", "startIcon", "type", "variant"], Nw = (e) => {
  const {
    color: t,
    disableElevation: n,
    fullWidth: o,
    size: a,
    variant: s,
    classes: i
  } = e, l = {
    root: ["root", s, `${s}${de(t)}`, `size${de(a)}`, `${s}Size${de(a)}`, `color${de(t)}`, n && "disableElevation", o && "fullWidth"],
    label: ["label"],
    startIcon: ["icon", "startIcon", `iconSize${de(a)}`],
    endIcon: ["icon", "endIcon", `iconSize${de(a)}`]
  }, c = Se(l, Mw, i);
  return b({}, i, c);
}, am = (e) => b({}, e.size === "small" && {
  "& > *:nth-of-type(1)": {
    fontSize: 18
  }
}, e.size === "medium" && {
  "& > *:nth-of-type(1)": {
    fontSize: 20
  }
}, e.size === "large" && {
  "& > *:nth-of-type(1)": {
    fontSize: 22
  }
}), jw = Z(lr, {
  shouldForwardProp: (e) => nn(e) || e === "classes",
  name: "MuiButton",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, t[n.variant], t[`${n.variant}${de(n.color)}`], t[`size${de(n.size)}`], t[`${n.variant}Size${de(n.size)}`], n.color === "inherit" && t.colorInherit, n.disableElevation && t.disableElevation, n.fullWidth && t.fullWidth];
  }
})(({
  theme: e,
  ownerState: t
}) => {
  var n, o;
  const a = e.palette.mode === "light" ? e.palette.grey[300] : e.palette.grey[800], s = e.palette.mode === "light" ? e.palette.grey.A100 : e.palette.grey[700];
  return b({}, e.typography.button, {
    minWidth: 64,
    padding: "6px 16px",
    borderRadius: (e.vars || e).shape.borderRadius,
    transition: e.transitions.create(["background-color", "box-shadow", "border-color", "color"], {
      duration: e.transitions.duration.short
    }),
    "&:hover": b({
      textDecoration: "none",
      backgroundColor: e.vars ? `rgba(${e.vars.palette.text.primaryChannel} / ${e.vars.palette.action.hoverOpacity})` : qe(e.palette.text.primary, e.palette.action.hoverOpacity),
      // Reset on touch devices, it doesn't add specificity
      "@media (hover: none)": {
        backgroundColor: "transparent"
      }
    }, t.variant === "text" && t.color !== "inherit" && {
      backgroundColor: e.vars ? `rgba(${e.vars.palette[t.color].mainChannel} / ${e.vars.palette.action.hoverOpacity})` : qe(e.palette[t.color].main, e.palette.action.hoverOpacity),
      // Reset on touch devices, it doesn't add specificity
      "@media (hover: none)": {
        backgroundColor: "transparent"
      }
    }, t.variant === "outlined" && t.color !== "inherit" && {
      border: `1px solid ${(e.vars || e).palette[t.color].main}`,
      backgroundColor: e.vars ? `rgba(${e.vars.palette[t.color].mainChannel} / ${e.vars.palette.action.hoverOpacity})` : qe(e.palette[t.color].main, e.palette.action.hoverOpacity),
      // Reset on touch devices, it doesn't add specificity
      "@media (hover: none)": {
        backgroundColor: "transparent"
      }
    }, t.variant === "contained" && {
      backgroundColor: e.vars ? e.vars.palette.Button.inheritContainedHoverBg : s,
      boxShadow: (e.vars || e).shadows[4],
      // Reset on touch devices, it doesn't add specificity
      "@media (hover: none)": {
        boxShadow: (e.vars || e).shadows[2],
        backgroundColor: (e.vars || e).palette.grey[300]
      }
    }, t.variant === "contained" && t.color !== "inherit" && {
      backgroundColor: (e.vars || e).palette[t.color].dark,
      // Reset on touch devices, it doesn't add specificity
      "@media (hover: none)": {
        backgroundColor: (e.vars || e).palette[t.color].main
      }
    }),
    "&:active": b({}, t.variant === "contained" && {
      boxShadow: (e.vars || e).shadows[8]
    }),
    [`&.${Ra.focusVisible}`]: b({}, t.variant === "contained" && {
      boxShadow: (e.vars || e).shadows[6]
    }),
    [`&.${Ra.disabled}`]: b({
      color: (e.vars || e).palette.action.disabled
    }, t.variant === "outlined" && {
      border: `1px solid ${(e.vars || e).palette.action.disabledBackground}`
    }, t.variant === "contained" && {
      color: (e.vars || e).palette.action.disabled,
      boxShadow: (e.vars || e).shadows[0],
      backgroundColor: (e.vars || e).palette.action.disabledBackground
    })
  }, t.variant === "text" && {
    padding: "6px 8px"
  }, t.variant === "text" && t.color !== "inherit" && {
    color: (e.vars || e).palette[t.color].main
  }, t.variant === "outlined" && {
    padding: "5px 15px",
    border: "1px solid currentColor"
  }, t.variant === "outlined" && t.color !== "inherit" && {
    color: (e.vars || e).palette[t.color].main,
    border: e.vars ? `1px solid rgba(${e.vars.palette[t.color].mainChannel} / 0.5)` : `1px solid ${qe(e.palette[t.color].main, 0.5)}`
  }, t.variant === "contained" && {
    color: e.vars ? (
      // this is safe because grey does not change between default light/dark mode
      e.vars.palette.text.primary
    ) : (n = (o = e.palette).getContrastText) == null ? void 0 : n.call(o, e.palette.grey[300]),
    backgroundColor: e.vars ? e.vars.palette.Button.inheritContainedBg : a,
    boxShadow: (e.vars || e).shadows[2]
  }, t.variant === "contained" && t.color !== "inherit" && {
    color: (e.vars || e).palette[t.color].contrastText,
    backgroundColor: (e.vars || e).palette[t.color].main
  }, t.color === "inherit" && {
    color: "inherit",
    borderColor: "currentColor"
  }, t.size === "small" && t.variant === "text" && {
    padding: "4px 5px",
    fontSize: e.typography.pxToRem(13)
  }, t.size === "large" && t.variant === "text" && {
    padding: "8px 11px",
    fontSize: e.typography.pxToRem(15)
  }, t.size === "small" && t.variant === "outlined" && {
    padding: "3px 9px",
    fontSize: e.typography.pxToRem(13)
  }, t.size === "large" && t.variant === "outlined" && {
    padding: "7px 21px",
    fontSize: e.typography.pxToRem(15)
  }, t.size === "small" && t.variant === "contained" && {
    padding: "4px 10px",
    fontSize: e.typography.pxToRem(13)
  }, t.size === "large" && t.variant === "contained" && {
    padding: "8px 22px",
    fontSize: e.typography.pxToRem(15)
  }, t.fullWidth && {
    width: "100%"
  });
}, ({
  ownerState: e
}) => e.disableElevation && {
  boxShadow: "none",
  "&:hover": {
    boxShadow: "none"
  },
  [`&.${Ra.focusVisible}`]: {
    boxShadow: "none"
  },
  "&:active": {
    boxShadow: "none"
  },
  [`&.${Ra.disabled}`]: {
    boxShadow: "none"
  }
}), Aw = Z("span", {
  name: "MuiButton",
  slot: "StartIcon",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.startIcon, t[`iconSize${de(n.size)}`]];
  }
})(({
  ownerState: e
}) => b({
  display: "inherit",
  marginRight: 8,
  marginLeft: -4
}, e.size === "small" && {
  marginLeft: -2
}, am(e))), Fw = Z("span", {
  name: "MuiButton",
  slot: "EndIcon",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.endIcon, t[`iconSize${de(n.size)}`]];
  }
})(({
  ownerState: e
}) => b({
  display: "inherit",
  marginRight: -4,
  marginLeft: 8
}, e.size === "small" && {
  marginRight: -2
}, am(e))), Ar = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = g.useContext(rm), a = g.useContext(om), s = xl(o, t), i = Ee({
    props: s,
    name: "MuiButton"
  }), {
    children: l,
    color: c = "primary",
    component: u = "button",
    className: d,
    disabled: f = !1,
    disableElevation: p = !1,
    disableFocusRipple: m = !1,
    endIcon: v,
    focusVisibleClassName: h,
    fullWidth: y = !1,
    size: w = "medium",
    startIcon: C,
    type: E,
    variant: O = "text"
  } = i, T = ie(i, Iw), P = b({}, i, {
    color: c,
    component: u,
    disabled: f,
    disableElevation: p,
    disableFocusRipple: m,
    fullWidth: y,
    size: w,
    type: E,
    variant: O
  }), S = Nw(P), j = C && /* @__PURE__ */ x.jsx(Aw, {
    className: S.startIcon,
    ownerState: P,
    children: C
  }), $ = v && /* @__PURE__ */ x.jsx(Fw, {
    className: S.endIcon,
    ownerState: P,
    children: v
  }), V = a || "";
  return /* @__PURE__ */ x.jsxs(jw, b({
    ownerState: P,
    className: pe(o.className, S.root, d, V),
    component: u,
    disabled: f,
    focusRipple: !m,
    focusVisibleClassName: pe(S.focusVisible, h),
    ref: n,
    type: E
  }, T, {
    classes: S,
    children: [j, l, $]
  }));
});
process.env.NODE_ENV !== "production" && (Ar.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * @default 'primary'
   */
  color: r.oneOfType([r.oneOf(["inherit", "primary", "secondary", "success", "error", "info", "warning"]), r.string]),
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * If `true`, the component is disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, no elevation is used.
   * @default false
   */
  disableElevation: r.bool,
  /**
   * If `true`, the  keyboard focus ripple is disabled.
   * @default false
   */
  disableFocusRipple: r.bool,
  /**
   * If `true`, the ripple effect is disabled.
   *
   * ⚠️ Without a ripple there is no styling for :focus-visible by default. Be sure
   * to highlight the element by applying separate styles with the `.Mui-focusVisible` class.
   * @default false
   */
  disableRipple: r.bool,
  /**
   * Element placed after the children.
   */
  endIcon: r.node,
  /**
   * @ignore
   */
  focusVisibleClassName: r.string,
  /**
   * If `true`, the button will take up the full width of its container.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * The URL to link to when the button is clicked.
   * If defined, an `a` element will be used as the root node.
   */
  href: r.string,
  /**
   * The size of the component.
   * `small` is equivalent to the dense button styling.
   * @default 'medium'
   */
  size: r.oneOfType([r.oneOf(["small", "medium", "large"]), r.string]),
  /**
   * Element placed before the children.
   */
  startIcon: r.node,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * @ignore
   */
  type: r.oneOfType([r.oneOf(["button", "reset", "submit"]), r.string]),
  /**
   * The variant to use.
   * @default 'text'
   */
  variant: r.oneOfType([r.oneOf(["contained", "outlined", "text"]), r.string])
});
function Vw(e) {
  return Pe("MuiCircularProgress", e);
}
Ce("MuiCircularProgress", ["root", "determinate", "indeterminate", "colorPrimary", "colorSecondary", "svg", "circle", "circleDeterminate", "circleIndeterminate", "circleDisableShrink"]);
const Lw = ["className", "color", "disableShrink", "size", "style", "thickness", "value", "variant"];
let Bs = (e) => e, sd, id, ld, cd;
const _n = 44, Bw = io(sd || (sd = Bs`
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
`)), zw = io(id || (id = Bs`
  0% {
    stroke-dasharray: 1px, 200px;
    stroke-dashoffset: 0;
  }

  50% {
    stroke-dasharray: 100px, 200px;
    stroke-dashoffset: -15px;
  }

  100% {
    stroke-dasharray: 100px, 200px;
    stroke-dashoffset: -125px;
  }
`)), Ww = (e) => {
  const {
    classes: t,
    variant: n,
    color: o,
    disableShrink: a
  } = e, s = {
    root: ["root", n, `color${de(o)}`],
    svg: ["svg"],
    circle: ["circle", `circle${de(n)}`, a && "circleDisableShrink"]
  };
  return Se(s, Vw, t);
}, Uw = Z("span", {
  name: "MuiCircularProgress",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, t[n.variant], t[`color${de(n.color)}`]];
  }
})(({
  ownerState: e,
  theme: t
}) => b({
  display: "inline-block"
}, e.variant === "determinate" && {
  transition: t.transitions.create("transform")
}, e.color !== "inherit" && {
  color: (t.vars || t).palette[e.color].main
}), ({
  ownerState: e
}) => e.variant === "indeterminate" && ks(ld || (ld = Bs`
      animation: ${0} 1.4s linear infinite;
    `), Bw)), Hw = Z("svg", {
  name: "MuiCircularProgress",
  slot: "Svg",
  overridesResolver: (e, t) => t.svg
})({
  display: "block"
  // Keeps the progress centered
}), qw = Z("circle", {
  name: "MuiCircularProgress",
  slot: "Circle",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.circle, t[`circle${de(n.variant)}`], n.disableShrink && t.circleDisableShrink];
  }
})(({
  ownerState: e,
  theme: t
}) => b({
  stroke: "currentColor"
}, e.variant === "determinate" && {
  transition: t.transitions.create("stroke-dashoffset")
}, e.variant === "indeterminate" && {
  // Some default value that looks fine waiting for the animation to kicks in.
  strokeDasharray: "80px, 200px",
  strokeDashoffset: 0
  // Add the unit to fix a Edge 16 and below bug.
}), ({
  ownerState: e
}) => e.variant === "indeterminate" && !e.disableShrink && ks(cd || (cd = Bs`
      animation: ${0} 1.4s ease-in-out infinite;
    `), zw)), sm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiCircularProgress"
  }), {
    className: a,
    color: s = "primary",
    disableShrink: i = !1,
    size: l = 40,
    style: c,
    thickness: u = 3.6,
    value: d = 0,
    variant: f = "indeterminate"
  } = o, p = ie(o, Lw), m = b({}, o, {
    color: s,
    disableShrink: i,
    size: l,
    thickness: u,
    value: d,
    variant: f
  }), v = Ww(m), h = {}, y = {}, w = {};
  if (f === "determinate") {
    const C = 2 * Math.PI * ((_n - u) / 2);
    h.strokeDasharray = C.toFixed(3), w["aria-valuenow"] = Math.round(d), h.strokeDashoffset = `${((100 - d) / 100 * C).toFixed(3)}px`, y.transform = "rotate(-90deg)";
  }
  return /* @__PURE__ */ x.jsx(Uw, b({
    className: pe(v.root, a),
    style: b({
      width: l,
      height: l
    }, y, c),
    ownerState: m,
    ref: n,
    role: "progressbar"
  }, w, p, {
    children: /* @__PURE__ */ x.jsx(Hw, {
      className: v.svg,
      ownerState: m,
      viewBox: `${_n / 2} ${_n / 2} ${_n} ${_n}`,
      children: /* @__PURE__ */ x.jsx(qw, {
        className: v.circle,
        style: h,
        ownerState: m,
        cx: _n,
        cy: _n,
        r: (_n - u) / 2,
        fill: "none",
        strokeWidth: u
      })
    })
  }));
});
process.env.NODE_ENV !== "production" && (sm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * @default 'primary'
   */
  color: r.oneOfType([r.oneOf(["inherit", "primary", "secondary", "error", "info", "success", "warning"]), r.string]),
  /**
   * If `true`, the shrink animation is disabled.
   * This only works if variant is `indeterminate`.
   * @default false
   */
  disableShrink: Sn(r.bool, (e) => e.disableShrink && e.variant && e.variant !== "indeterminate" ? new Error("MUI: You have provided the `disableShrink` prop with a variant other than `indeterminate`. This will have no effect.") : null),
  /**
   * The size of the component.
   * If using a number, the pixel unit is assumed.
   * If using a string, you need to provide the CSS unit, for example '3rem'.
   * @default 40
   */
  size: r.oneOfType([r.number, r.string]),
  /**
   * @ignore
   */
  style: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The thickness of the circle.
   * @default 3.6
   */
  thickness: r.number,
  /**
   * The value of the progress indicator for the determinate variant.
   * Value between 0 and 100.
   * @default 0
   */
  value: r.number,
  /**
   * The variant to use.
   * Use indeterminate when there is no progress value.
   * @default 'indeterminate'
   */
  variant: r.oneOf(["determinate", "indeterminate"])
});
function Yw(e) {
  return Pe("MuiModal", e);
}
Ce("MuiModal", ["root", "hidden", "backdrop"]);
const Kw = ["BackdropComponent", "BackdropProps", "classes", "className", "closeAfterTransition", "children", "container", "component", "components", "componentsProps", "disableAutoFocus", "disableEnforceFocus", "disableEscapeKeyDown", "disablePortal", "disableRestoreFocus", "disableScrollLock", "hideBackdrop", "keepMounted", "onBackdropClick", "onClose", "onTransitionEnter", "onTransitionExited", "open", "slotProps", "slots", "theme"], Gw = (e) => {
  const {
    open: t,
    exited: n,
    classes: o
  } = e;
  return Se({
    root: ["root", !t && n && "hidden"],
    backdrop: ["backdrop"]
  }, Yw, o);
}, Xw = Z("div", {
  name: "MuiModal",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, !n.open && n.exited && t.hidden];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  position: "fixed",
  zIndex: (e.vars || e).zIndex.modal,
  right: 0,
  bottom: 0,
  top: 0,
  left: 0
}, !t.open && t.exited && {
  visibility: "hidden"
})), Zw = Z(Zl, {
  name: "MuiModal",
  slot: "Backdrop",
  overridesResolver: (e, t) => t.backdrop
})({
  zIndex: -1
}), Jl = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s, i, l, c;
  const u = Ee({
    name: "MuiModal",
    props: t
  }), {
    BackdropComponent: d = Zw,
    BackdropProps: f,
    className: p,
    closeAfterTransition: m = !1,
    children: v,
    container: h,
    component: y,
    components: w = {},
    componentsProps: C = {},
    disableAutoFocus: E = !1,
    disableEnforceFocus: O = !1,
    disableEscapeKeyDown: T = !1,
    disablePortal: P = !1,
    disableRestoreFocus: S = !1,
    disableScrollLock: j = !1,
    hideBackdrop: $ = !1,
    keepMounted: V = !1,
    onBackdropClick: _,
    open: L,
    slotProps: M,
    slots: R
    // eslint-disable-next-line react/prop-types
  } = u, D = ie(u, Kw), F = b({}, u, {
    closeAfterTransition: m,
    disableAutoFocus: E,
    disableEnforceFocus: O,
    disableEscapeKeyDown: T,
    disablePortal: P,
    disableRestoreFocus: S,
    disableScrollLock: j,
    hideBackdrop: $,
    keepMounted: V
  }), {
    getRootProps: z,
    getBackdropProps: N,
    getTransitionProps: q,
    portalRef: A,
    isTopModal: H,
    exited: te,
    hasTransition: re
  } = V0(b({}, F, {
    rootRef: n
  })), B = b({}, F, {
    exited: te
  }), G = Gw(B), ee = {};
  if (v.props.tabIndex === void 0 && (ee.tabIndex = "-1"), re) {
    const {
      onEnter: K,
      onExited: Y
    } = q();
    ee.onEnter = K, ee.onExited = Y;
  }
  const W = (o = (a = R == null ? void 0 : R.root) != null ? a : w.Root) != null ? o : Xw, J = (s = (i = R == null ? void 0 : R.backdrop) != null ? i : w.Backdrop) != null ? s : d, se = (l = M == null ? void 0 : M.root) != null ? l : C.root, le = (c = M == null ? void 0 : M.backdrop) != null ? c : C.backdrop, X = Ye({
    elementType: W,
    externalSlotProps: se,
    externalForwardedProps: D,
    getSlotProps: z,
    additionalProps: {
      ref: n,
      as: y
    },
    ownerState: B,
    className: pe(p, se == null ? void 0 : se.className, G == null ? void 0 : G.root, !B.open && B.exited && (G == null ? void 0 : G.hidden))
  }), U = Ye({
    elementType: J,
    externalSlotProps: le,
    additionalProps: f,
    getSlotProps: (K) => N(b({}, K, {
      onClick: (Y) => {
        _ && _(Y), K != null && K.onClick && K.onClick(Y);
      }
    })),
    className: pe(le == null ? void 0 : le.className, f == null ? void 0 : f.className, G == null ? void 0 : G.backdrop),
    ownerState: B
  });
  return !V && !L && (!re || te) ? null : /* @__PURE__ */ x.jsx(Go, {
    ref: A,
    container: h,
    disablePortal: P,
    children: /* @__PURE__ */ x.jsxs(W, b({}, X, {
      children: [!$ && d ? /* @__PURE__ */ x.jsx(J, b({}, U)) : null, /* @__PURE__ */ x.jsx(Ko, {
        disableEnforceFocus: O,
        disableAutoFocus: E,
        disableRestoreFocus: S,
        isEnabled: H,
        open: L,
        children: /* @__PURE__ */ g.cloneElement(v, ee)
      })]
    }))
  });
});
process.env.NODE_ENV !== "production" && (Jl.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * A backdrop component. This prop enables custom backdrop rendering.
   * @deprecated Use `slots.backdrop` instead. While this prop currently works, it will be removed in the next major version.
   * Use the `slots.backdrop` prop to make your application ready for the next version of Material UI.
   * @default styled(Backdrop, {
   *   name: 'MuiModal',
   *   slot: 'Backdrop',
   *   overridesResolver: (props, styles) => {
   *     return styles.backdrop;
   *   },
   * })({
   *   zIndex: -1,
   * })
   */
  BackdropComponent: r.elementType,
  /**
   * Props applied to the [`Backdrop`](/material-ui/api/backdrop/) element.
   * @deprecated Use `slotProps.backdrop` instead.
   */
  BackdropProps: r.object,
  /**
   * A single child content element.
   */
  children: ao.isRequired,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * When set to true the Modal waits until a nested Transition is completed before closing.
   * @default false
   */
  closeAfterTransition: r.bool,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `slots` prop.
   * It's recommended to use the `slots` prop instead.
   *
   * @default {}
   */
  components: r.shape({
    Backdrop: r.elementType,
    Root: r.elementType
  }),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `slotProps` prop.
   * It's recommended to use the `slotProps` prop instead, as `componentsProps` will be deprecated in the future.
   *
   * @default {}
   */
  componentsProps: r.shape({
    backdrop: r.oneOfType([r.func, r.object]),
    root: r.oneOfType([r.func, r.object])
  }),
  /**
   * An HTML element or function that returns one.
   * The `container` will have the portal children appended to it.
   *
   * You can also provide a callback, which is called in a React layout effect.
   * This lets you set the container from a ref, and also makes server-side rendering possible.
   *
   * By default, it uses the body of the top-level document object,
   * so it's simply `document.body` most of the time.
   */
  container: r.oneOfType([Tn, r.func]),
  /**
   * If `true`, the modal will not automatically shift focus to itself when it opens, and
   * replace it to the last focused element when it closes.
   * This also works correctly with any modal children that have the `disableAutoFocus` prop.
   *
   * Generally this should never be set to `true` as it makes the modal less
   * accessible to assistive technologies, like screen readers.
   * @default false
   */
  disableAutoFocus: r.bool,
  /**
   * If `true`, the modal will not prevent focus from leaving the modal while open.
   *
   * Generally this should never be set to `true` as it makes the modal less
   * accessible to assistive technologies, like screen readers.
   * @default false
   */
  disableEnforceFocus: r.bool,
  /**
   * If `true`, hitting escape will not fire the `onClose` callback.
   * @default false
   */
  disableEscapeKeyDown: r.bool,
  /**
   * The `children` will be under the DOM hierarchy of the parent component.
   * @default false
   */
  disablePortal: r.bool,
  /**
   * If `true`, the modal will not restore focus to previously focused element once
   * modal is hidden or unmounted.
   * @default false
   */
  disableRestoreFocus: r.bool,
  /**
   * Disable the scroll lock behavior.
   * @default false
   */
  disableScrollLock: r.bool,
  /**
   * If `true`, the backdrop is not rendered.
   * @default false
   */
  hideBackdrop: r.bool,
  /**
   * Always keep the children in the DOM.
   * This prop can be useful in SEO situation or
   * when you want to maximize the responsiveness of the Modal.
   * @default false
   */
  keepMounted: r.bool,
  /**
   * Callback fired when the backdrop is clicked.
   * @deprecated Use the `onClose` prop with the `reason` argument to handle the `backdropClick` events.
   */
  onBackdropClick: r.func,
  /**
   * Callback fired when the component requests to be closed.
   * The `reason` parameter can optionally be used to control the response to `onClose`.
   *
   * @param {object} event The event source of the callback.
   * @param {string} reason Can be: `"escapeKeyDown"`, `"backdropClick"`.
   */
  onClose: r.func,
  /**
   * A function called when a transition enters.
   */
  onTransitionEnter: r.func,
  /**
   * A function called when a transition has exited.
   */
  onTransitionExited: r.func,
  /**
   * If `true`, the component is shown.
   */
  open: r.bool.isRequired,
  /**
   * The props used for each slot inside the Modal.
   * @default {}
   */
  slotProps: r.shape({
    backdrop: r.oneOfType([r.func, r.object]),
    root: r.oneOfType([r.func, r.object])
  }),
  /**
   * The components used for each slot inside the Modal.
   * Either a string to use a HTML element or a component.
   * @default {}
   */
  slots: r.shape({
    backdrop: r.elementType,
    root: r.elementType
  }),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function Jw(e) {
  return Pe("MuiDialog", e);
}
const Ao = Ce("MuiDialog", ["root", "scrollPaper", "scrollBody", "container", "paper", "paperScrollPaper", "paperScrollBody", "paperWidthFalse", "paperWidthXs", "paperWidthSm", "paperWidthMd", "paperWidthLg", "paperWidthXl", "paperFullWidth", "paperFullScreen"]), Ql = /* @__PURE__ */ g.createContext({});
process.env.NODE_ENV !== "production" && (Ql.displayName = "DialogContext");
const Qw = ["aria-describedby", "aria-labelledby", "BackdropComponent", "BackdropProps", "children", "className", "disableEscapeKeyDown", "fullScreen", "fullWidth", "maxWidth", "onBackdropClick", "onClick", "onClose", "open", "PaperComponent", "PaperProps", "scroll", "TransitionComponent", "transitionDuration", "TransitionProps"], eE = Z(Zl, {
  name: "MuiDialog",
  slot: "Backdrop",
  overrides: (e, t) => t.backdrop
})({
  // Improve scrollable dialog support.
  zIndex: -1
}), tE = (e) => {
  const {
    classes: t,
    scroll: n,
    maxWidth: o,
    fullWidth: a,
    fullScreen: s
  } = e, i = {
    root: ["root"],
    container: ["container", `scroll${de(n)}`],
    paper: ["paper", `paperScroll${de(n)}`, `paperWidth${de(String(o))}`, a && "paperFullWidth", s && "paperFullScreen"]
  };
  return Se(i, Jw, t);
}, nE = Z(Jl, {
  name: "MuiDialog",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  "@media print": {
    // Use !important to override the Modal inline-style.
    position: "absolute !important"
  }
}), rE = Z("div", {
  name: "MuiDialog",
  slot: "Container",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.container, t[`scroll${de(n.scroll)}`]];
  }
})(({
  ownerState: e
}) => b({
  height: "100%",
  "@media print": {
    height: "auto"
  },
  // We disable the focus ring for mouse, touch and keyboard users.
  outline: 0
}, e.scroll === "paper" && {
  display: "flex",
  justifyContent: "center",
  alignItems: "center"
}, e.scroll === "body" && {
  overflowY: "auto",
  overflowX: "hidden",
  textAlign: "center",
  "&::after": {
    content: '""',
    display: "inline-block",
    verticalAlign: "middle",
    height: "100%",
    width: "0"
  }
})), oE = Z(pa, {
  name: "MuiDialog",
  slot: "Paper",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.paper, t[`scrollPaper${de(n.scroll)}`], t[`paperWidth${de(String(n.maxWidth))}`], n.fullWidth && t.paperFullWidth, n.fullScreen && t.paperFullScreen];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  margin: 32,
  position: "relative",
  overflowY: "auto",
  // Fix IE11 issue, to remove at some point.
  "@media print": {
    overflowY: "visible",
    boxShadow: "none"
  }
}, t.scroll === "paper" && {
  display: "flex",
  flexDirection: "column",
  maxHeight: "calc(100% - 64px)"
}, t.scroll === "body" && {
  display: "inline-block",
  verticalAlign: "middle",
  textAlign: "left"
  // 'initial' doesn't work on IE11
}, !t.maxWidth && {
  maxWidth: "calc(100% - 64px)"
}, t.maxWidth === "xs" && {
  maxWidth: e.breakpoints.unit === "px" ? Math.max(e.breakpoints.values.xs, 444) : `max(${e.breakpoints.values.xs}${e.breakpoints.unit}, 444px)`,
  [`&.${Ao.paperScrollBody}`]: {
    [e.breakpoints.down(Math.max(e.breakpoints.values.xs, 444) + 32 * 2)]: {
      maxWidth: "calc(100% - 64px)"
    }
  }
}, t.maxWidth && t.maxWidth !== "xs" && {
  maxWidth: `${e.breakpoints.values[t.maxWidth]}${e.breakpoints.unit}`,
  [`&.${Ao.paperScrollBody}`]: {
    [e.breakpoints.down(e.breakpoints.values[t.maxWidth] + 32 * 2)]: {
      maxWidth: "calc(100% - 64px)"
    }
  }
}, t.fullWidth && {
  width: "calc(100% - 64px)"
}, t.fullScreen && {
  margin: 0,
  width: "100%",
  maxWidth: "100%",
  height: "100%",
  maxHeight: "none",
  borderRadius: 0,
  [`&.${Ao.paperScrollBody}`]: {
    margin: 0,
    maxWidth: "100%"
  }
})), ec = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiDialog"
  }), a = Zt(), s = {
    enter: a.transitions.duration.enteringScreen,
    exit: a.transitions.duration.leavingScreen
  }, {
    "aria-describedby": i,
    "aria-labelledby": l,
    BackdropComponent: c,
    BackdropProps: u,
    children: d,
    className: f,
    disableEscapeKeyDown: p = !1,
    fullScreen: m = !1,
    fullWidth: v = !1,
    maxWidth: h = "sm",
    onBackdropClick: y,
    onClick: w,
    onClose: C,
    open: E,
    PaperComponent: O = pa,
    PaperProps: T = {},
    scroll: P = "paper",
    TransitionComponent: S = mr,
    transitionDuration: j = s,
    TransitionProps: $
  } = o, V = ie(o, Qw), _ = b({}, o, {
    disableEscapeKeyDown: p,
    fullScreen: m,
    fullWidth: v,
    maxWidth: h,
    scroll: P
  }), L = tE(_), M = g.useRef(), R = (N) => {
    M.current = N.target === N.currentTarget;
  }, D = (N) => {
    w && w(N), M.current && (M.current = null, y && y(N), C && C(N, "backdropClick"));
  }, F = Bn(l), z = g.useMemo(() => ({
    titleId: F
  }), [F]);
  return /* @__PURE__ */ x.jsx(nE, b({
    className: pe(L.root, f),
    closeAfterTransition: !0,
    components: {
      Backdrop: eE
    },
    componentsProps: {
      backdrop: b({
        transitionDuration: j,
        as: c
      }, u)
    },
    disableEscapeKeyDown: p,
    onClose: C,
    open: E,
    ref: n,
    onClick: D,
    ownerState: _
  }, V, {
    children: /* @__PURE__ */ x.jsx(S, b({
      appear: !0,
      in: E,
      timeout: j,
      role: "presentation"
    }, $, {
      children: /* @__PURE__ */ x.jsx(rE, {
        className: pe(L.container),
        onMouseDown: R,
        ownerState: _,
        children: /* @__PURE__ */ x.jsx(oE, b({
          as: O,
          elevation: 24,
          role: "dialog",
          "aria-describedby": i,
          "aria-labelledby": F
        }, T, {
          className: pe(L.paper, T.className),
          ownerState: _,
          children: /* @__PURE__ */ x.jsx(Ql.Provider, {
            value: z,
            children: d
          })
        }))
      })
    }))
  }));
});
process.env.NODE_ENV !== "production" && (ec.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The id(s) of the element(s) that describe the dialog.
   */
  "aria-describedby": r.string,
  /**
   * The id(s) of the element(s) that label the dialog.
   */
  "aria-labelledby": r.string,
  /**
   * A backdrop component. This prop enables custom backdrop rendering.
   * @deprecated Use `slots.backdrop` instead. While this prop currently works, it will be removed in the next major version.
   * Use the `slots.backdrop` prop to make your application ready for the next version of Material UI.
   * @default styled(Backdrop, {
   *   name: 'MuiModal',
   *   slot: 'Backdrop',
   *   overridesResolver: (props, styles) => {
   *     return styles.backdrop;
   *   },
   * })({
   *   zIndex: -1,
   * })
   */
  BackdropComponent: r.elementType,
  /**
   * @ignore
   */
  BackdropProps: r.object,
  /**
   * Dialog children, usually the included sub-components.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * If `true`, hitting escape will not fire the `onClose` callback.
   * @default false
   */
  disableEscapeKeyDown: r.bool,
  /**
   * If `true`, the dialog is full-screen.
   * @default false
   */
  fullScreen: r.bool,
  /**
   * If `true`, the dialog stretches to `maxWidth`.
   *
   * Notice that the dialog width grow is limited by the default margin.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * Determine the max-width of the dialog.
   * The dialog width grows with the size of the screen.
   * Set to `false` to disable `maxWidth`.
   * @default 'sm'
   */
  maxWidth: r.oneOfType([r.oneOf(["xs", "sm", "md", "lg", "xl", !1]), r.string]),
  /**
   * Callback fired when the backdrop is clicked.
   * @deprecated Use the `onClose` prop with the `reason` argument to handle the `backdropClick` events.
   */
  onBackdropClick: r.func,
  /**
   * @ignore
   */
  onClick: r.func,
  /**
   * Callback fired when the component requests to be closed.
   *
   * @param {object} event The event source of the callback.
   * @param {string} reason Can be: `"escapeKeyDown"`, `"backdropClick"`.
   */
  onClose: r.func,
  /**
   * If `true`, the component is shown.
   */
  open: r.bool.isRequired,
  /**
   * The component used to render the body of the dialog.
   * @default Paper
   */
  PaperComponent: r.elementType,
  /**
   * Props applied to the [`Paper`](/material-ui/api/paper/) element.
   * @default {}
   */
  PaperProps: r.object,
  /**
   * Determine the container for scrolling the dialog.
   * @default 'paper'
   */
  scroll: r.oneOf(["body", "paper"]),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The component used for the transition.
   * [Follow this guide](/material-ui/transitions/#transitioncomponent-prop) to learn more about the requirements for this component.
   * @default Fade
   */
  TransitionComponent: r.elementType,
  /**
   * The duration for the transition, in milliseconds.
   * You may specify a single timeout for all transitions, or individually with an object.
   * @default {
   *   enter: theme.transitions.duration.enteringScreen,
   *   exit: theme.transitions.duration.leavingScreen,
   * }
   */
  transitionDuration: r.oneOfType([r.number, r.shape({
    appear: r.number,
    enter: r.number,
    exit: r.number
  })]),
  /**
   * Props applied to the transition element.
   * By default, the element is based on this [`Transition`](https://reactcommunity.org/react-transition-group/transition/) component.
   */
  TransitionProps: r.object
});
function aE(e) {
  return Pe("MuiDialogActions", e);
}
Ce("MuiDialogActions", ["root", "spacing"]);
const sE = ["className", "disableSpacing"], iE = (e) => {
  const {
    classes: t,
    disableSpacing: n
  } = e;
  return Se({
    root: ["root", !n && "spacing"]
  }, aE, t);
}, lE = Z("div", {
  name: "MuiDialogActions",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, !n.disableSpacing && t.spacing];
  }
})(({
  ownerState: e
}) => b({
  display: "flex",
  alignItems: "center",
  padding: 8,
  justifyContent: "flex-end",
  flex: "0 0 auto"
}, !e.disableSpacing && {
  "& > :not(style) ~ :not(style)": {
    marginLeft: 8
  }
})), tc = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiDialogActions"
  }), {
    className: a,
    disableSpacing: s = !1
  } = o, i = ie(o, sE), l = b({}, o, {
    disableSpacing: s
  }), c = iE(l);
  return /* @__PURE__ */ x.jsx(lE, b({
    className: pe(c.root, a),
    ownerState: l,
    ref: n
  }, i));
});
process.env.NODE_ENV !== "production" && (tc.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * If `true`, the actions do not have additional margin.
   * @default false
   */
  disableSpacing: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function cE(e) {
  return Pe("MuiDialogContent", e);
}
Ce("MuiDialogContent", ["root", "dividers"]);
function uE(e) {
  return Pe("MuiDialogTitle", e);
}
const dE = Ce("MuiDialogTitle", ["root"]), pE = ["className", "dividers"], fE = (e) => {
  const {
    classes: t,
    dividers: n
  } = e;
  return Se({
    root: ["root", n && "dividers"]
  }, cE, t);
}, mE = Z("div", {
  name: "MuiDialogContent",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.dividers && t.dividers];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  flex: "1 1 auto",
  // Add iOS momentum scrolling for iOS < 13.0
  WebkitOverflowScrolling: "touch",
  overflowY: "auto",
  padding: "20px 24px"
}, t.dividers ? {
  padding: "16px 24px",
  borderTop: `1px solid ${(e.vars || e).palette.divider}`,
  borderBottom: `1px solid ${(e.vars || e).palette.divider}`
} : {
  [`.${dE.root} + &`]: {
    paddingTop: 0
  }
})), zs = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiDialogContent"
  }), {
    className: a,
    dividers: s = !1
  } = o, i = ie(o, pE), l = b({}, o, {
    dividers: s
  }), c = fE(l);
  return /* @__PURE__ */ x.jsx(mE, b({
    className: pe(c.root, a),
    ownerState: l,
    ref: n
  }, i));
});
process.env.NODE_ENV !== "production" && (zs.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * Display the top and bottom dividers.
   * @default false
   */
  dividers: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
const hE = ["className", "id"], bE = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"]
  }, uE, t);
}, gE = Z(Rt, {
  name: "MuiDialogTitle",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  padding: "16px 24px",
  flex: "0 0 auto"
}), nc = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiDialogTitle"
  }), {
    className: a,
    id: s
  } = o, i = ie(o, hE), l = o, c = bE(l), {
    titleId: u = s
  } = g.useContext(Ql);
  return /* @__PURE__ */ x.jsx(gE, b({
    component: "h2",
    className: pe(c.root, a),
    ownerState: l,
    ref: n,
    variant: "h6",
    id: s ?? u
  }, i));
});
process.env.NODE_ENV !== "production" && (nc.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * @ignore
   */
  id: r.string,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function yE(e) {
  return Pe("MuiDivider", e);
}
Ce("MuiDivider", ["root", "absolute", "fullWidth", "inset", "middle", "flexItem", "light", "vertical", "withChildren", "withChildrenVertical", "textAlignRight", "textAlignLeft", "wrapper", "wrapperVertical"]);
const vE = ["absolute", "children", "className", "component", "flexItem", "light", "orientation", "role", "textAlign", "variant"], xE = (e) => {
  const {
    absolute: t,
    children: n,
    classes: o,
    flexItem: a,
    light: s,
    orientation: i,
    textAlign: l,
    variant: c
  } = e;
  return Se({
    root: ["root", t && "absolute", c, s && "light", i === "vertical" && "vertical", a && "flexItem", n && "withChildren", n && i === "vertical" && "withChildrenVertical", l === "right" && i !== "vertical" && "textAlignRight", l === "left" && i !== "vertical" && "textAlignLeft"],
    wrapper: ["wrapper", i === "vertical" && "wrapperVertical"]
  }, yE, o);
}, TE = Z("div", {
  name: "MuiDivider",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.absolute && t.absolute, t[n.variant], n.light && t.light, n.orientation === "vertical" && t.vertical, n.flexItem && t.flexItem, n.children && t.withChildren, n.children && n.orientation === "vertical" && t.withChildrenVertical, n.textAlign === "right" && n.orientation !== "vertical" && t.textAlignRight, n.textAlign === "left" && n.orientation !== "vertical" && t.textAlignLeft];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  margin: 0,
  // Reset browser default style.
  flexShrink: 0,
  borderWidth: 0,
  borderStyle: "solid",
  borderColor: (e.vars || e).palette.divider,
  borderBottomWidth: "thin"
}, t.absolute && {
  position: "absolute",
  bottom: 0,
  left: 0,
  width: "100%"
}, t.light && {
  borderColor: e.vars ? `rgba(${e.vars.palette.dividerChannel} / 0.08)` : qe(e.palette.divider, 0.08)
}, t.variant === "inset" && {
  marginLeft: 72
}, t.variant === "middle" && t.orientation === "horizontal" && {
  marginLeft: e.spacing(2),
  marginRight: e.spacing(2)
}, t.variant === "middle" && t.orientation === "vertical" && {
  marginTop: e.spacing(1),
  marginBottom: e.spacing(1)
}, t.orientation === "vertical" && {
  height: "100%",
  borderBottomWidth: 0,
  borderRightWidth: "thin"
}, t.flexItem && {
  alignSelf: "stretch",
  height: "auto"
}), ({
  ownerState: e
}) => b({}, e.children && {
  display: "flex",
  whiteSpace: "nowrap",
  textAlign: "center",
  border: 0,
  "&::before, &::after": {
    content: '""',
    alignSelf: "center"
  }
}), ({
  theme: e,
  ownerState: t
}) => b({}, t.children && t.orientation !== "vertical" && {
  "&::before, &::after": {
    width: "100%",
    borderTop: `thin solid ${(e.vars || e).palette.divider}`
  }
}), ({
  theme: e,
  ownerState: t
}) => b({}, t.children && t.orientation === "vertical" && {
  flexDirection: "column",
  "&::before, &::after": {
    height: "100%",
    borderLeft: `thin solid ${(e.vars || e).palette.divider}`
  }
}), ({
  ownerState: e
}) => b({}, e.textAlign === "right" && e.orientation !== "vertical" && {
  "&::before": {
    width: "90%"
  },
  "&::after": {
    width: "10%"
  }
}, e.textAlign === "left" && e.orientation !== "vertical" && {
  "&::before": {
    width: "10%"
  },
  "&::after": {
    width: "90%"
  }
})), wE = Z("span", {
  name: "MuiDivider",
  slot: "Wrapper",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.wrapper, n.orientation === "vertical" && t.wrapperVertical];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  display: "inline-block",
  paddingLeft: `calc(${e.spacing(1)} * 1.2)`,
  paddingRight: `calc(${e.spacing(1)} * 1.2)`
}, t.orientation === "vertical" && {
  paddingTop: `calc(${e.spacing(1)} * 1.2)`,
  paddingBottom: `calc(${e.spacing(1)} * 1.2)`
})), Jo = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiDivider"
  }), {
    absolute: a = !1,
    children: s,
    className: i,
    component: l = s ? "div" : "hr",
    flexItem: c = !1,
    light: u = !1,
    orientation: d = "horizontal",
    role: f = l !== "hr" ? "separator" : void 0,
    textAlign: p = "center",
    variant: m = "fullWidth"
  } = o, v = ie(o, vE), h = b({}, o, {
    absolute: a,
    component: l,
    flexItem: c,
    light: u,
    orientation: d,
    role: f,
    textAlign: p,
    variant: m
  }), y = xE(h);
  return /* @__PURE__ */ x.jsx(TE, b({
    as: l,
    className: pe(y.root, i),
    role: f,
    ref: n,
    ownerState: h
  }, v, {
    children: s ? /* @__PURE__ */ x.jsx(wE, {
      className: y.wrapper,
      ownerState: h,
      children: s
    }) : null
  }));
});
Jo.muiSkipListHighlight = !0;
process.env.NODE_ENV !== "production" && (Jo.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Absolutely position the element.
   * @default false
   */
  absolute: r.bool,
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * If `true`, a vertical divider will have the correct height when used in flex container.
   * (By default, a vertical divider will have a calculated height of `0px` if it is the child of a flex container.)
   * @default false
   */
  flexItem: r.bool,
  /**
   * If `true`, the divider will have a lighter color.
   * @default false
   * @deprecated Use <Divider sx={{ opacity: 0.6 }} /> (or any opacity or color) instead. [How to migrate](/material-ui/migration/migrating-from-deprecated-apis/)
   */
  light: r.bool,
  /**
   * The component orientation.
   * @default 'horizontal'
   */
  orientation: r.oneOf(["horizontal", "vertical"]),
  /**
   * @ignore
   */
  role: r.string,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The text alignment.
   * @default 'center'
   */
  textAlign: r.oneOf(["center", "left", "right"]),
  /**
   * The variant to use.
   * @default 'fullWidth'
   */
  variant: r.oneOfType([r.oneOf(["fullWidth", "inset", "middle"]), r.string])
});
const EE = ["disableUnderline", "components", "componentsProps", "fullWidth", "hiddenLabel", "inputComponent", "multiline", "slotProps", "slots", "type"], CE = (e) => {
  const {
    classes: t,
    disableUnderline: n
  } = e, a = Se({
    root: ["root", !n && "underline"],
    input: ["input"]
  }, Ew, t);
  return b({}, t, a);
}, OE = Z(Vs, {
  shouldForwardProp: (e) => nn(e) || e === "classes",
  name: "MuiFilledInput",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [...As(e, t), !n.disableUnderline && t.underline];
  }
})(({
  theme: e,
  ownerState: t
}) => {
  var n;
  const o = e.palette.mode === "light", a = o ? "rgba(0, 0, 0, 0.42)" : "rgba(255, 255, 255, 0.7)", s = o ? "rgba(0, 0, 0, 0.06)" : "rgba(255, 255, 255, 0.09)", i = o ? "rgba(0, 0, 0, 0.09)" : "rgba(255, 255, 255, 0.13)", l = o ? "rgba(0, 0, 0, 0.12)" : "rgba(255, 255, 255, 0.12)";
  return b({
    position: "relative",
    backgroundColor: e.vars ? e.vars.palette.FilledInput.bg : s,
    borderTopLeftRadius: (e.vars || e).shape.borderRadius,
    borderTopRightRadius: (e.vars || e).shape.borderRadius,
    transition: e.transitions.create("background-color", {
      duration: e.transitions.duration.shorter,
      easing: e.transitions.easing.easeOut
    }),
    "&:hover": {
      backgroundColor: e.vars ? e.vars.palette.FilledInput.hoverBg : i,
      // Reset on touch devices, it doesn't add specificity
      "@media (hover: none)": {
        backgroundColor: e.vars ? e.vars.palette.FilledInput.bg : s
      }
    },
    [`&.${Xn.focused}`]: {
      backgroundColor: e.vars ? e.vars.palette.FilledInput.bg : s
    },
    [`&.${Xn.disabled}`]: {
      backgroundColor: e.vars ? e.vars.palette.FilledInput.disabledBg : l
    }
  }, !t.disableUnderline && {
    "&::after": {
      borderBottom: `2px solid ${(n = (e.vars || e).palette[t.color || "primary"]) == null ? void 0 : n.main}`,
      left: 0,
      bottom: 0,
      // Doing the other way around crash on IE11 "''" https://github.com/cssinjs/jss/issues/242
      content: '""',
      position: "absolute",
      right: 0,
      transform: "scaleX(0)",
      transition: e.transitions.create("transform", {
        duration: e.transitions.duration.shorter,
        easing: e.transitions.easing.easeOut
      }),
      pointerEvents: "none"
      // Transparent to the hover style.
    },
    [`&.${Xn.focused}:after`]: {
      // translateX(0) is a workaround for Safari transform scale bug
      // See https://github.com/mui/material-ui/issues/31766
      transform: "scaleX(1) translateX(0)"
    },
    [`&.${Xn.error}`]: {
      "&::before, &::after": {
        borderBottomColor: (e.vars || e).palette.error.main
      }
    },
    "&::before": {
      borderBottom: `1px solid ${e.vars ? `rgba(${e.vars.palette.common.onBackgroundChannel} / ${e.vars.opacity.inputUnderline})` : a}`,
      left: 0,
      bottom: 0,
      // Doing the other way around crash on IE11 "''" https://github.com/cssinjs/jss/issues/242
      content: '"\\00a0"',
      position: "absolute",
      right: 0,
      transition: e.transitions.create("border-bottom-color", {
        duration: e.transitions.duration.shorter
      }),
      pointerEvents: "none"
      // Transparent to the hover style.
    },
    [`&:hover:not(.${Xn.disabled}, .${Xn.error}):before`]: {
      borderBottom: `1px solid ${(e.vars || e).palette.text.primary}`
    },
    [`&.${Xn.disabled}:before`]: {
      borderBottomStyle: "dotted"
    }
  }, t.startAdornment && {
    paddingLeft: 12
  }, t.endAdornment && {
    paddingRight: 12
  }, t.multiline && b({
    padding: "25px 12px 8px"
  }, t.size === "small" && {
    paddingTop: 21,
    paddingBottom: 4
  }, t.hiddenLabel && {
    paddingTop: 16,
    paddingBottom: 17
  }, t.hiddenLabel && t.size === "small" && {
    paddingTop: 8,
    paddingBottom: 9
  }));
}), SE = Z(Ls, {
  name: "MuiFilledInput",
  slot: "Input",
  overridesResolver: Fs
})(({
  theme: e,
  ownerState: t
}) => b({
  paddingTop: 25,
  paddingRight: 12,
  paddingBottom: 8,
  paddingLeft: 12
}, !e.vars && {
  "&:-webkit-autofill": {
    WebkitBoxShadow: e.palette.mode === "light" ? null : "0 0 0 100px #266798 inset",
    WebkitTextFillColor: e.palette.mode === "light" ? null : "#fff",
    caretColor: e.palette.mode === "light" ? null : "#fff",
    borderTopLeftRadius: "inherit",
    borderTopRightRadius: "inherit"
  }
}, e.vars && {
  "&:-webkit-autofill": {
    borderTopLeftRadius: "inherit",
    borderTopRightRadius: "inherit"
  },
  [e.getColorSchemeSelector("dark")]: {
    "&:-webkit-autofill": {
      WebkitBoxShadow: "0 0 0 100px #266798 inset",
      WebkitTextFillColor: "#fff",
      caretColor: "#fff"
    }
  }
}, t.size === "small" && {
  paddingTop: 21,
  paddingBottom: 4
}, t.hiddenLabel && {
  paddingTop: 16,
  paddingBottom: 17
}, t.startAdornment && {
  paddingLeft: 0
}, t.endAdornment && {
  paddingRight: 0
}, t.hiddenLabel && t.size === "small" && {
  paddingTop: 8,
  paddingBottom: 9
}, t.multiline && {
  paddingTop: 0,
  paddingBottom: 0,
  paddingLeft: 0,
  paddingRight: 0
})), Ws = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s, i;
  const l = Ee({
    props: t,
    name: "MuiFilledInput"
  }), {
    components: c = {},
    componentsProps: u,
    fullWidth: d = !1,
    // declare here to prevent spreading to DOM
    inputComponent: f = "input",
    multiline: p = !1,
    slotProps: m,
    slots: v = {},
    type: h = "text"
  } = l, y = ie(l, EE), w = b({}, l, {
    fullWidth: d,
    inputComponent: f,
    multiline: p,
    type: h
  }), C = CE(l), E = {
    root: {
      ownerState: w
    },
    input: {
      ownerState: w
    }
  }, O = m ?? u ? kt(E, m ?? u) : E, T = (o = (a = v.root) != null ? a : c.Root) != null ? o : OE, P = (s = (i = v.input) != null ? i : c.Input) != null ? s : SE;
  return /* @__PURE__ */ x.jsx(Xl, b({
    slots: {
      root: T,
      input: P
    },
    componentsProps: O,
    fullWidth: d,
    inputComponent: f,
    multiline: p,
    ref: n,
    type: h
  }, y, {
    classes: C
  }));
});
process.env.NODE_ENV !== "production" && (Ws.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * This prop helps users to fill forms faster, especially on mobile devices.
   * The name can be confusing, as it's more like an autofill.
   * You can learn more about it [following the specification](https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofill).
   */
  autoComplete: r.string,
  /**
   * If `true`, the `input` element is focused during the first mount.
   */
  autoFocus: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * The prop defaults to the value (`'primary'`) inherited from the parent FormControl component.
   */
  color: r.oneOfType([r.oneOf(["primary", "secondary"]), r.string]),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `slots` prop.
   * It's recommended to use the `slots` prop instead.
   *
   * @default {}
   */
  components: r.shape({
    Input: r.elementType,
    Root: r.elementType
  }),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `slotProps` prop.
   * It's recommended to use the `slotProps` prop instead, as `componentsProps` will be deprecated in the future.
   *
   * @default {}
   */
  componentsProps: r.shape({
    input: r.object,
    root: r.object
  }),
  /**
   * The default value. Use when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the component is disabled.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  disabled: r.bool,
  /**
   * If `true`, the input will not have an underline.
   */
  disableUnderline: r.bool,
  /**
   * End `InputAdornment` for this component.
   */
  endAdornment: r.node,
  /**
   * If `true`, the `input` will indicate an error.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  error: r.bool,
  /**
   * If `true`, the `input` will take up the full width of its container.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * If `true`, the label is hidden.
   * This is used to increase density for a `FilledInput`.
   * Be sure to add `aria-label` to the `input` element.
   * @default false
   */
  hiddenLabel: r.bool,
  /**
   * The id of the `input` element.
   */
  id: r.string,
  /**
   * The component used for the `input` element.
   * Either a string to use a HTML element or a component.
   * @default 'input'
   */
  inputComponent: r.elementType,
  /**
   * [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Attributes) applied to the `input` element.
   * @default {}
   */
  inputProps: r.object,
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * If `dense`, will adjust vertical spacing. This is normally obtained via context from
   * FormControl.
   * The prop defaults to the value (`'none'`) inherited from the parent FormControl component.
   */
  margin: r.oneOf(["dense", "none"]),
  /**
   * Maximum number of rows to display when multiline option is set to true.
   */
  maxRows: r.oneOfType([r.number, r.string]),
  /**
   * Minimum number of rows to display when multiline option is set to true.
   */
  minRows: r.oneOfType([r.number, r.string]),
  /**
   * If `true`, a [TextareaAutosize](/material-ui/react-textarea-autosize/) element is rendered.
   * @default false
   */
  multiline: r.bool,
  /**
   * Name attribute of the `input` element.
   */
  name: r.string,
  /**
   * Callback fired when the value is changed.
   *
   * @param {React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>} event The event source of the callback.
   * You can pull out the new value by accessing `event.target.value` (string).
   */
  onChange: r.func,
  /**
   * The short hint displayed in the `input` before the user enters a value.
   */
  placeholder: r.string,
  /**
   * It prevents the user from changing the value of the field
   * (not from interacting with the field).
   */
  readOnly: r.bool,
  /**
   * If `true`, the `input` element is required.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  required: r.bool,
  /**
   * Number of rows to display when multiline option is set to true.
   */
  rows: r.oneOfType([r.number, r.string]),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `componentsProps` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slotProps: r.shape({
    input: r.object,
    root: r.object
  }),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `components` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slots: r.shape({
    input: r.elementType,
    root: r.elementType
  }),
  /**
   * Start `InputAdornment` for this component.
   */
  startAdornment: r.node,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Type of the `input` element. It should be [a valid HTML5 input type](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Form_%3Cinput%3E_types).
   * @default 'text'
   */
  type: r.string,
  /**
   * The value of the `input` element, required for a controlled component.
   */
  value: r.any
});
Ws.muiName = "Input";
function PE(e) {
  return Pe("MuiFormControl", e);
}
Ce("MuiFormControl", ["root", "marginNone", "marginNormal", "marginDense", "fullWidth", "disabled"]);
const RE = ["children", "className", "color", "component", "disabled", "error", "focused", "fullWidth", "hiddenLabel", "margin", "required", "size", "variant"], DE = (e) => {
  const {
    classes: t,
    margin: n,
    fullWidth: o
  } = e, a = {
    root: ["root", n !== "none" && `margin${de(n)}`, o && "fullWidth"]
  };
  return Se(a, PE, t);
}, $E = Z("div", {
  name: "MuiFormControl",
  slot: "Root",
  overridesResolver: ({
    ownerState: e
  }, t) => b({}, t.root, t[`margin${de(e.margin)}`], e.fullWidth && t.fullWidth)
})(({
  ownerState: e
}) => b({
  display: "inline-flex",
  flexDirection: "column",
  position: "relative",
  // Reset fieldset default style.
  minWidth: 0,
  padding: 0,
  margin: 0,
  border: 0,
  verticalAlign: "top"
}, e.margin === "normal" && {
  marginTop: 16,
  marginBottom: 8
}, e.margin === "dense" && {
  marginTop: 8,
  marginBottom: 4
}, e.fullWidth && {
  width: "100%"
})), im = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiFormControl"
  }), {
    children: a,
    className: s,
    color: i = "primary",
    component: l = "div",
    disabled: c = !1,
    error: u = !1,
    focused: d,
    fullWidth: f = !1,
    hiddenLabel: p = !1,
    margin: m = "none",
    required: v = !1,
    size: h = "medium",
    variant: y = "outlined"
  } = o, w = ie(o, RE), C = b({}, o, {
    color: i,
    component: l,
    disabled: c,
    error: u,
    fullWidth: f,
    hiddenLabel: p,
    margin: m,
    required: v,
    size: h,
    variant: y
  }), E = DE(C), [O, T] = g.useState(() => {
    let M = !1;
    return a && g.Children.forEach(a, (R) => {
      if (!Br(R, ["Input", "Select"]))
        return;
      const D = Br(R, ["Select"]) ? R.props.input : R;
      D && bw(D.props) && (M = !0);
    }), M;
  }), [P, S] = g.useState(() => {
    let M = !1;
    return a && g.Children.forEach(a, (R) => {
      Br(R, ["Input", "Select"]) && (rs(R.props, !0) || rs(R.props.inputProps, !0)) && (M = !0);
    }), M;
  }), [j, $] = g.useState(!1);
  c && j && $(!1);
  const V = d !== void 0 && !c ? d : j;
  let _;
  if (process.env.NODE_ENV !== "production") {
    const M = g.useRef(!1);
    _ = () => (M.current && console.error(["MUI: There are multiple `InputBase` components inside a FormControl.", "This creates visual inconsistencies, only use one `InputBase`."].join(`
`)), M.current = !0, () => {
      M.current = !1;
    });
  }
  const L = g.useMemo(() => ({
    adornedStart: O,
    setAdornedStart: T,
    color: i,
    disabled: c,
    error: u,
    filled: P,
    focused: V,
    fullWidth: f,
    hiddenLabel: p,
    size: h,
    onBlur: () => {
      $(!1);
    },
    onEmpty: () => {
      S(!1);
    },
    onFilled: () => {
      S(!0);
    },
    onFocus: () => {
      $(!0);
    },
    registerEffect: _,
    required: v,
    variant: y
  }), [O, i, c, u, P, V, f, p, _, v, h, y]);
  return /* @__PURE__ */ x.jsx(ha.Provider, {
    value: L,
    children: /* @__PURE__ */ x.jsx($E, b({
      as: l,
      ownerState: C,
      className: pe(E.root, s),
      ref: n
    }, w, {
      children: a
    }))
  });
});
process.env.NODE_ENV !== "production" && (im.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * @default 'primary'
   */
  color: r.oneOfType([r.oneOf(["primary", "secondary", "error", "info", "success", "warning"]), r.string]),
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * If `true`, the label, input and helper text should be displayed in a disabled state.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, the label is displayed in an error state.
   * @default false
   */
  error: r.bool,
  /**
   * If `true`, the component is displayed in focused state.
   */
  focused: r.bool,
  /**
   * If `true`, the component will take up the full width of its container.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * If `true`, the label is hidden.
   * This is used to increase density for a `FilledInput`.
   * Be sure to add `aria-label` to the `input` element.
   * @default false
   */
  hiddenLabel: r.bool,
  /**
   * If `dense` or `normal`, will adjust vertical spacing of this and contained components.
   * @default 'none'
   */
  margin: r.oneOf(["dense", "none", "normal"]),
  /**
   * If `true`, the label will indicate that the `input` is required.
   * @default false
   */
  required: r.bool,
  /**
   * The size of the component.
   * @default 'medium'
   */
  size: r.oneOfType([r.oneOf(["medium", "small"]), r.string]),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The variant to use.
   * @default 'outlined'
   */
  variant: r.oneOf(["filled", "outlined", "standard"])
});
function kE(e) {
  return Pe("MuiFormHelperText", e);
}
const ud = Ce("MuiFormHelperText", ["root", "error", "disabled", "sizeSmall", "sizeMedium", "contained", "focused", "filled", "required"]);
var dd;
const _E = ["children", "className", "component", "disabled", "error", "filled", "focused", "margin", "required", "variant"], ME = (e) => {
  const {
    classes: t,
    contained: n,
    size: o,
    disabled: a,
    error: s,
    filled: i,
    focused: l,
    required: c
  } = e, u = {
    root: ["root", a && "disabled", s && "error", o && `size${de(o)}`, n && "contained", l && "focused", i && "filled", c && "required"]
  };
  return Se(u, kE, t);
}, IE = Z("p", {
  name: "MuiFormHelperText",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.size && t[`size${de(n.size)}`], n.contained && t.contained, n.filled && t.filled];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  color: (e.vars || e).palette.text.secondary
}, e.typography.caption, {
  textAlign: "left",
  marginTop: 3,
  marginRight: 0,
  marginBottom: 0,
  marginLeft: 0,
  [`&.${ud.disabled}`]: {
    color: (e.vars || e).palette.text.disabled
  },
  [`&.${ud.error}`]: {
    color: (e.vars || e).palette.error.main
  }
}, t.size === "small" && {
  marginTop: 4
}, t.contained && {
  marginLeft: 14,
  marginRight: 14
})), rc = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiFormHelperText"
  }), {
    children: a,
    className: s,
    component: i = "p"
  } = o, l = ie(o, _E), c = fr(), u = fo({
    props: o,
    muiFormControl: c,
    states: ["variant", "size", "disabled", "error", "filled", "focused", "required"]
  }), d = b({}, o, {
    component: i,
    contained: u.variant === "filled" || u.variant === "outlined",
    variant: u.variant,
    size: u.size,
    disabled: u.disabled,
    error: u.error,
    filled: u.filled,
    focused: u.focused,
    required: u.required
  }), f = ME(d);
  return /* @__PURE__ */ x.jsx(IE, b({
    as: i,
    ownerState: d,
    className: pe(f.root, s),
    ref: n
  }, l, {
    children: a === " " ? (
      // notranslate needed while Google Translate will not fix zero-width space issue
      dd || (dd = /* @__PURE__ */ x.jsx("span", {
        className: "notranslate",
        children: "​"
      }))
    ) : a
  }));
});
process.env.NODE_ENV !== "production" && (rc.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   *
   * If `' '` is provided, the component reserves one line height for displaying a future message.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * If `true`, the helper text should be displayed in a disabled state.
   */
  disabled: r.bool,
  /**
   * If `true`, helper text should be displayed in an error state.
   */
  error: r.bool,
  /**
   * If `true`, the helper text should use filled classes key.
   */
  filled: r.bool,
  /**
   * If `true`, the helper text should use focused classes key.
   */
  focused: r.bool,
  /**
   * If `dense`, will adjust vertical spacing. This is normally obtained via context from
   * FormControl.
   */
  margin: r.oneOf(["dense"]),
  /**
   * If `true`, the helper text should use required classes key.
   */
  required: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The variant to use.
   */
  variant: r.oneOfType([r.oneOf(["filled", "outlined", "standard"]), r.string])
});
function NE(e) {
  return Pe("MuiFormLabel", e);
}
const Fo = Ce("MuiFormLabel", ["root", "colorSecondary", "focused", "disabled", "error", "filled", "required", "asterisk"]), jE = ["children", "className", "color", "component", "disabled", "error", "filled", "focused", "required"], AE = (e) => {
  const {
    classes: t,
    color: n,
    focused: o,
    disabled: a,
    error: s,
    filled: i,
    required: l
  } = e, c = {
    root: ["root", `color${de(n)}`, a && "disabled", s && "error", i && "filled", o && "focused", l && "required"],
    asterisk: ["asterisk", s && "error"]
  };
  return Se(c, NE, t);
}, FE = Z("label", {
  name: "MuiFormLabel",
  slot: "Root",
  overridesResolver: ({
    ownerState: e
  }, t) => b({}, t.root, e.color === "secondary" && t.colorSecondary, e.filled && t.filled)
})(({
  theme: e,
  ownerState: t
}) => b({
  color: (e.vars || e).palette.text.secondary
}, e.typography.body1, {
  lineHeight: "1.4375em",
  padding: 0,
  position: "relative",
  [`&.${Fo.focused}`]: {
    color: (e.vars || e).palette[t.color].main
  },
  [`&.${Fo.disabled}`]: {
    color: (e.vars || e).palette.text.disabled
  },
  [`&.${Fo.error}`]: {
    color: (e.vars || e).palette.error.main
  }
})), VE = Z("span", {
  name: "MuiFormLabel",
  slot: "Asterisk",
  overridesResolver: (e, t) => t.asterisk
})(({
  theme: e
}) => ({
  [`&.${Fo.error}`]: {
    color: (e.vars || e).palette.error.main
  }
})), lm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiFormLabel"
  }), {
    children: a,
    className: s,
    component: i = "label"
  } = o, l = ie(o, jE), c = fr(), u = fo({
    props: o,
    muiFormControl: c,
    states: ["color", "required", "focused", "disabled", "error", "filled"]
  }), d = b({}, o, {
    color: u.color || "primary",
    component: i,
    disabled: u.disabled,
    error: u.error,
    filled: u.filled,
    focused: u.focused,
    required: u.required
  }), f = AE(d);
  return /* @__PURE__ */ x.jsxs(FE, b({
    as: i,
    ownerState: d,
    className: pe(f.root, s),
    ref: n
  }, l, {
    children: [a, u.required && /* @__PURE__ */ x.jsxs(VE, {
      ownerState: d,
      "aria-hidden": !0,
      className: f.asterisk,
      children: [" ", "*"]
    })]
  }));
});
process.env.NODE_ENV !== "production" && (lm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   */
  color: r.oneOfType([r.oneOf(["error", "info", "primary", "secondary", "success", "warning"]), r.string]),
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * If `true`, the label should be displayed in a disabled state.
   */
  disabled: r.bool,
  /**
   * If `true`, the label is displayed in an error state.
   */
  error: r.bool,
  /**
   * If `true`, the label should use filled classes key.
   */
  filled: r.bool,
  /**
   * If `true`, the input of this label is focused (used by `FormGroup` components).
   */
  focused: r.bool,
  /**
   * If `true`, the label will indicate that the `input` is required.
   */
  required: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
const LE = ["addEndListener", "appear", "children", "easing", "in", "onEnter", "onEntered", "onEntering", "onExit", "onExited", "onExiting", "style", "timeout", "TransitionComponent"];
function Xi(e) {
  return `scale(${e}, ${e ** 2})`;
}
const BE = {
  entering: {
    opacity: 1,
    transform: Xi(1)
  },
  entered: {
    opacity: 1,
    transform: "none"
  }
}, wi = typeof navigator < "u" && /^((?!chrome|android).)*(safari|mobile)/i.test(navigator.userAgent) && /(os |version\/)15(.|_)4/i.test(navigator.userAgent), ro = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const {
    addEndListener: o,
    appear: a = !0,
    children: s,
    easing: i,
    in: l,
    onEnter: c,
    onEntered: u,
    onEntering: d,
    onExit: f,
    onExited: p,
    onExiting: m,
    style: v,
    timeout: h = "auto",
    // eslint-disable-next-line react/prop-types
    TransitionComponent: y = Jt
  } = t, w = ie(t, LE), C = jr(), E = g.useRef(), O = Zt(), T = g.useRef(null), P = Ke(T, s.ref, n), S = (D) => (F) => {
    if (D) {
      const z = T.current;
      F === void 0 ? D(z) : D(z, F);
    }
  }, j = S(d), $ = S((D, F) => {
    Mf(D);
    const {
      duration: z,
      delay: N,
      easing: q
    } = es({
      style: v,
      timeout: h,
      easing: i
    }, {
      mode: "enter"
    });
    let A;
    h === "auto" ? (A = O.transitions.getAutoHeightDuration(D.clientHeight), E.current = A) : A = z, D.style.transition = [O.transitions.create("opacity", {
      duration: A,
      delay: N
    }), O.transitions.create("transform", {
      duration: wi ? A : A * 0.666,
      delay: N,
      easing: q
    })].join(","), c && c(D, F);
  }), V = S(u), _ = S(m), L = S((D) => {
    const {
      duration: F,
      delay: z,
      easing: N
    } = es({
      style: v,
      timeout: h,
      easing: i
    }, {
      mode: "exit"
    });
    let q;
    h === "auto" ? (q = O.transitions.getAutoHeightDuration(D.clientHeight), E.current = q) : q = F, D.style.transition = [O.transitions.create("opacity", {
      duration: q,
      delay: z
    }), O.transitions.create("transform", {
      duration: wi ? q : q * 0.666,
      delay: wi ? z : z || q * 0.333,
      easing: N
    })].join(","), D.style.opacity = 0, D.style.transform = Xi(0.75), f && f(D);
  }), M = S(p), R = (D) => {
    h === "auto" && C.start(E.current || 0, D), o && o(T.current, D);
  };
  return /* @__PURE__ */ x.jsx(y, b({
    appear: a,
    in: l,
    nodeRef: T,
    onEnter: $,
    onEntered: V,
    onEntering: j,
    onExit: L,
    onExited: M,
    onExiting: _,
    addEndListener: R,
    timeout: h === "auto" ? null : h
  }, w, {
    children: (D, F) => /* @__PURE__ */ g.cloneElement(s, b({
      style: b({
        opacity: 0,
        transform: Xi(0.75),
        visibility: D === "exited" && !l ? "hidden" : void 0
      }, BE[D], v, s.props.style),
      ref: P
    }, F))
  }));
});
process.env.NODE_ENV !== "production" && (ro.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Add a custom transition end trigger. Called with the transitioning DOM
   * node and a done callback. Allows for more fine grained transition end
   * logic. Note: Timeouts are still used as a fallback if provided.
   */
  addEndListener: r.func,
  /**
   * Perform the enter transition when it first mounts if `in` is also `true`.
   * Set this to `false` to disable this behavior.
   * @default true
   */
  appear: r.bool,
  /**
   * A single child content element.
   */
  children: ao.isRequired,
  /**
   * The transition timing function.
   * You may specify a single easing or a object containing enter and exit values.
   */
  easing: r.oneOfType([r.shape({
    enter: r.string,
    exit: r.string
  }), r.string]),
  /**
   * If `true`, the component will transition in.
   */
  in: r.bool,
  /**
   * @ignore
   */
  onEnter: r.func,
  /**
   * @ignore
   */
  onEntered: r.func,
  /**
   * @ignore
   */
  onEntering: r.func,
  /**
   * @ignore
   */
  onExit: r.func,
  /**
   * @ignore
   */
  onExited: r.func,
  /**
   * @ignore
   */
  onExiting: r.func,
  /**
   * @ignore
   */
  style: r.object,
  /**
   * The duration for the transition, in milliseconds.
   * You may specify a single timeout for all transitions, or individually with an object.
   *
   * Set to 'auto' to automatically calculate transition time based on height.
   * @default 'auto'
   */
  timeout: r.oneOfType([r.oneOf(["auto"]), r.number, r.shape({
    appear: r.number,
    enter: r.number,
    exit: r.number
  })])
});
ro.muiSupportAuto = !0;
const zE = ["disableUnderline", "components", "componentsProps", "fullWidth", "inputComponent", "multiline", "slotProps", "slots", "type"], WE = (e) => {
  const {
    classes: t,
    disableUnderline: n
  } = e, a = Se({
    root: ["root", !n && "underline"],
    input: ["input"]
  }, Tw, t);
  return b({}, t, a);
}, UE = Z(Vs, {
  shouldForwardProp: (e) => nn(e) || e === "classes",
  name: "MuiInput",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [...As(e, t), !n.disableUnderline && t.underline];
  }
})(({
  theme: e,
  ownerState: t
}) => {
  let o = e.palette.mode === "light" ? "rgba(0, 0, 0, 0.42)" : "rgba(255, 255, 255, 0.7)";
  return e.vars && (o = `rgba(${e.vars.palette.common.onBackgroundChannel} / ${e.vars.opacity.inputUnderline})`), b({
    position: "relative"
  }, t.formControl && {
    "label + &": {
      marginTop: 16
    }
  }, !t.disableUnderline && {
    "&::after": {
      borderBottom: `2px solid ${(e.vars || e).palette[t.color].main}`,
      left: 0,
      bottom: 0,
      // Doing the other way around crash on IE11 "''" https://github.com/cssinjs/jss/issues/242
      content: '""',
      position: "absolute",
      right: 0,
      transform: "scaleX(0)",
      transition: e.transitions.create("transform", {
        duration: e.transitions.duration.shorter,
        easing: e.transitions.easing.easeOut
      }),
      pointerEvents: "none"
      // Transparent to the hover style.
    },
    [`&.${Eo.focused}:after`]: {
      // translateX(0) is a workaround for Safari transform scale bug
      // See https://github.com/mui/material-ui/issues/31766
      transform: "scaleX(1) translateX(0)"
    },
    [`&.${Eo.error}`]: {
      "&::before, &::after": {
        borderBottomColor: (e.vars || e).palette.error.main
      }
    },
    "&::before": {
      borderBottom: `1px solid ${o}`,
      left: 0,
      bottom: 0,
      // Doing the other way around crash on IE11 "''" https://github.com/cssinjs/jss/issues/242
      content: '"\\00a0"',
      position: "absolute",
      right: 0,
      transition: e.transitions.create("border-bottom-color", {
        duration: e.transitions.duration.shorter
      }),
      pointerEvents: "none"
      // Transparent to the hover style.
    },
    [`&:hover:not(.${Eo.disabled}, .${Eo.error}):before`]: {
      borderBottom: `2px solid ${(e.vars || e).palette.text.primary}`,
      // Reset on touch devices, it doesn't add specificity
      "@media (hover: none)": {
        borderBottom: `1px solid ${o}`
      }
    },
    [`&.${Eo.disabled}:before`]: {
      borderBottomStyle: "dotted"
    }
  });
}), HE = Z(Ls, {
  name: "MuiInput",
  slot: "Input",
  overridesResolver: Fs
})({}), Us = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s, i;
  const l = Ee({
    props: t,
    name: "MuiInput"
  }), {
    disableUnderline: c,
    components: u = {},
    componentsProps: d,
    fullWidth: f = !1,
    inputComponent: p = "input",
    multiline: m = !1,
    slotProps: v,
    slots: h = {},
    type: y = "text"
  } = l, w = ie(l, zE), C = WE(l), O = {
    root: {
      ownerState: {
        disableUnderline: c
      }
    }
  }, T = v ?? d ? kt(v ?? d, O) : O, P = (o = (a = h.root) != null ? a : u.Root) != null ? o : UE, S = (s = (i = h.input) != null ? i : u.Input) != null ? s : HE;
  return /* @__PURE__ */ x.jsx(Xl, b({
    slots: {
      root: P,
      input: S
    },
    slotProps: T,
    fullWidth: f,
    inputComponent: p,
    multiline: m,
    ref: n,
    type: y
  }, w, {
    classes: C
  }));
});
process.env.NODE_ENV !== "production" && (Us.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * This prop helps users to fill forms faster, especially on mobile devices.
   * The name can be confusing, as it's more like an autofill.
   * You can learn more about it [following the specification](https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofill).
   */
  autoComplete: r.string,
  /**
   * If `true`, the `input` element is focused during the first mount.
   */
  autoFocus: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * The prop defaults to the value (`'primary'`) inherited from the parent FormControl component.
   */
  color: r.oneOfType([r.oneOf(["primary", "secondary"]), r.string]),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `slots` prop.
   * It's recommended to use the `slots` prop instead.
   *
   * @default {}
   */
  components: r.shape({
    Input: r.elementType,
    Root: r.elementType
  }),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `slotProps` prop.
   * It's recommended to use the `slotProps` prop instead, as `componentsProps` will be deprecated in the future.
   *
   * @default {}
   */
  componentsProps: r.shape({
    input: r.object,
    root: r.object
  }),
  /**
   * The default value. Use when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the component is disabled.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  disabled: r.bool,
  /**
   * If `true`, the `input` will not have an underline.
   */
  disableUnderline: r.bool,
  /**
   * End `InputAdornment` for this component.
   */
  endAdornment: r.node,
  /**
   * If `true`, the `input` will indicate an error.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  error: r.bool,
  /**
   * If `true`, the `input` will take up the full width of its container.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * The id of the `input` element.
   */
  id: r.string,
  /**
   * The component used for the `input` element.
   * Either a string to use a HTML element or a component.
   * @default 'input'
   */
  inputComponent: r.elementType,
  /**
   * [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Attributes) applied to the `input` element.
   * @default {}
   */
  inputProps: r.object,
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * If `dense`, will adjust vertical spacing. This is normally obtained via context from
   * FormControl.
   * The prop defaults to the value (`'none'`) inherited from the parent FormControl component.
   */
  margin: r.oneOf(["dense", "none"]),
  /**
   * Maximum number of rows to display when multiline option is set to true.
   */
  maxRows: r.oneOfType([r.number, r.string]),
  /**
   * Minimum number of rows to display when multiline option is set to true.
   */
  minRows: r.oneOfType([r.number, r.string]),
  /**
   * If `true`, a [TextareaAutosize](/material-ui/react-textarea-autosize/) element is rendered.
   * @default false
   */
  multiline: r.bool,
  /**
   * Name attribute of the `input` element.
   */
  name: r.string,
  /**
   * Callback fired when the value is changed.
   *
   * @param {React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>} event The event source of the callback.
   * You can pull out the new value by accessing `event.target.value` (string).
   */
  onChange: r.func,
  /**
   * The short hint displayed in the `input` before the user enters a value.
   */
  placeholder: r.string,
  /**
   * It prevents the user from changing the value of the field
   * (not from interacting with the field).
   */
  readOnly: r.bool,
  /**
   * If `true`, the `input` element is required.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  required: r.bool,
  /**
   * Number of rows to display when multiline option is set to true.
   */
  rows: r.oneOfType([r.number, r.string]),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `componentsProps` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slotProps: r.shape({
    input: r.object,
    root: r.object
  }),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `components` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slots: r.shape({
    input: r.elementType,
    root: r.elementType
  }),
  /**
   * Start `InputAdornment` for this component.
   */
  startAdornment: r.node,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Type of the `input` element. It should be [a valid HTML5 input type](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Form_%3Cinput%3E_types).
   * @default 'text'
   */
  type: r.string,
  /**
   * The value of the `input` element, required for a controlled component.
   */
  value: r.any
});
Us.muiName = "Input";
function qE(e) {
  return Pe("MuiInputAdornment", e);
}
const pd = Ce("MuiInputAdornment", ["root", "filled", "standard", "outlined", "positionStart", "positionEnd", "disablePointerEvents", "hiddenLabel", "sizeSmall"]);
var fd;
const YE = ["children", "className", "component", "disablePointerEvents", "disableTypography", "position", "variant"], KE = (e, t) => {
  const {
    ownerState: n
  } = e;
  return [t.root, t[`position${de(n.position)}`], n.disablePointerEvents === !0 && t.disablePointerEvents, t[n.variant]];
}, GE = (e) => {
  const {
    classes: t,
    disablePointerEvents: n,
    hiddenLabel: o,
    position: a,
    size: s,
    variant: i
  } = e, l = {
    root: ["root", n && "disablePointerEvents", a && `position${de(a)}`, i, o && "hiddenLabel", s && `size${de(s)}`]
  };
  return Se(l, qE, t);
}, XE = Z("div", {
  name: "MuiInputAdornment",
  slot: "Root",
  overridesResolver: KE
})(({
  theme: e,
  ownerState: t
}) => b({
  display: "flex",
  height: "0.01em",
  // Fix IE11 flexbox alignment. To remove at some point.
  maxHeight: "2em",
  alignItems: "center",
  whiteSpace: "nowrap",
  color: (e.vars || e).palette.action.active
}, t.variant === "filled" && {
  // Styles applied to the root element if `variant="filled"`.
  [`&.${pd.positionStart}&:not(.${pd.hiddenLabel})`]: {
    marginTop: 16
  }
}, t.position === "start" && {
  // Styles applied to the root element if `position="start"`.
  marginRight: 8
}, t.position === "end" && {
  // Styles applied to the root element if `position="end"`.
  marginLeft: 8
}, t.disablePointerEvents === !0 && {
  // Styles applied to the root element if `disablePointerEvents={true}`.
  pointerEvents: "none"
})), oc = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiInputAdornment"
  }), {
    children: a,
    className: s,
    component: i = "div",
    disablePointerEvents: l = !1,
    disableTypography: c = !1,
    position: u,
    variant: d
  } = o, f = ie(o, YE), p = fr() || {};
  let m = d;
  d && p.variant && process.env.NODE_ENV !== "production" && d === p.variant && console.error("MUI: The `InputAdornment` variant infers the variant prop you do not have to provide one."), p && !m && (m = p.variant);
  const v = b({}, o, {
    hiddenLabel: p.hiddenLabel,
    size: p.size,
    disablePointerEvents: l,
    position: u,
    variant: m
  }), h = GE(v);
  return /* @__PURE__ */ x.jsx(ha.Provider, {
    value: null,
    children: /* @__PURE__ */ x.jsx(XE, b({
      as: i,
      ownerState: v,
      className: pe(h.root, s),
      ref: n
    }, f, {
      children: typeof a == "string" && !c ? /* @__PURE__ */ x.jsx(Rt, {
        color: "text.secondary",
        children: a
      }) : /* @__PURE__ */ x.jsxs(g.Fragment, {
        children: [u === "start" ? (
          /* notranslate needed while Google Translate will not fix zero-width space issue */
          fd || (fd = /* @__PURE__ */ x.jsx("span", {
            className: "notranslate",
            children: "​"
          }))
        ) : null, a]
      })
    }))
  });
});
process.env.NODE_ENV !== "production" && (oc.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component, normally an `IconButton` or string.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * Disable pointer events on the root.
   * This allows for the content of the adornment to focus the `input` on click.
   * @default false
   */
  disablePointerEvents: r.bool,
  /**
   * If children is a string then disable wrapping in a Typography component.
   * @default false
   */
  disableTypography: r.bool,
  /**
   * The position this adornment should appear relative to the `Input`.
   */
  position: r.oneOf(["end", "start"]).isRequired,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The variant to use.
   * Note: If you are using the `TextField` component or the `FormControl` component
   * you do not have to set this manually.
   */
  variant: r.oneOf(["filled", "outlined", "standard"])
});
function ZE(e) {
  return Pe("MuiInputLabel", e);
}
Ce("MuiInputLabel", ["root", "focused", "disabled", "error", "required", "asterisk", "formControl", "sizeSmall", "shrink", "animated", "standard", "filled", "outlined"]);
const JE = ["disableAnimation", "margin", "shrink", "variant", "className"], QE = (e) => {
  const {
    classes: t,
    formControl: n,
    size: o,
    shrink: a,
    disableAnimation: s,
    variant: i,
    required: l
  } = e, c = {
    root: ["root", n && "formControl", !s && "animated", a && "shrink", o && o !== "normal" && `size${de(o)}`, i],
    asterisk: [l && "asterisk"]
  }, u = Se(c, ZE, t);
  return b({}, t, u);
}, eC = Z(lm, {
  shouldForwardProp: (e) => nn(e) || e === "classes",
  name: "MuiInputLabel",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [{
      [`& .${Fo.asterisk}`]: t.asterisk
    }, t.root, n.formControl && t.formControl, n.size === "small" && t.sizeSmall, n.shrink && t.shrink, !n.disableAnimation && t.animated, n.focused && t.focused, t[n.variant]];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  display: "block",
  transformOrigin: "top left",
  whiteSpace: "nowrap",
  overflow: "hidden",
  textOverflow: "ellipsis",
  maxWidth: "100%"
}, t.formControl && {
  position: "absolute",
  left: 0,
  top: 0,
  // slight alteration to spec spacing to match visual spec result
  transform: "translate(0, 20px) scale(1)"
}, t.size === "small" && {
  // Compensation for the `Input.inputSizeSmall` style.
  transform: "translate(0, 17px) scale(1)"
}, t.shrink && {
  transform: "translate(0, -1.5px) scale(0.75)",
  transformOrigin: "top left",
  maxWidth: "133%"
}, !t.disableAnimation && {
  transition: e.transitions.create(["color", "transform", "max-width"], {
    duration: e.transitions.duration.shorter,
    easing: e.transitions.easing.easeOut
  })
}, t.variant === "filled" && b({
  // Chrome's autofill feature gives the input field a yellow background.
  // Since the input field is behind the label in the HTML tree,
  // the input field is drawn last and hides the label with an opaque background color.
  // zIndex: 1 will raise the label above opaque background-colors of input.
  zIndex: 1,
  pointerEvents: "none",
  transform: "translate(12px, 16px) scale(1)",
  maxWidth: "calc(100% - 24px)"
}, t.size === "small" && {
  transform: "translate(12px, 13px) scale(1)"
}, t.shrink && b({
  userSelect: "none",
  pointerEvents: "auto",
  transform: "translate(12px, 7px) scale(0.75)",
  maxWidth: "calc(133% - 24px)"
}, t.size === "small" && {
  transform: "translate(12px, 4px) scale(0.75)"
})), t.variant === "outlined" && b({
  // see comment above on filled.zIndex
  zIndex: 1,
  pointerEvents: "none",
  transform: "translate(14px, 16px) scale(1)",
  maxWidth: "calc(100% - 24px)"
}, t.size === "small" && {
  transform: "translate(14px, 9px) scale(1)"
}, t.shrink && {
  userSelect: "none",
  pointerEvents: "auto",
  // Theoretically, we should have (8+5)*2/0.75 = 34px
  // but it feels a better when it bleeds a bit on the left, so 32px.
  maxWidth: "calc(133% - 32px)",
  transform: "translate(14px, -9px) scale(0.75)"
}))), cm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    name: "MuiInputLabel",
    props: t
  }), {
    disableAnimation: a = !1,
    shrink: s,
    className: i
  } = o, l = ie(o, JE), c = fr();
  let u = s;
  typeof u > "u" && c && (u = c.filled || c.focused || c.adornedStart);
  const d = fo({
    props: o,
    muiFormControl: c,
    states: ["size", "variant", "required", "focused"]
  }), f = b({}, o, {
    disableAnimation: a,
    formControl: c,
    shrink: u,
    size: d.size,
    variant: d.variant,
    required: d.required,
    focused: d.focused
  }), p = QE(f);
  return /* @__PURE__ */ x.jsx(eC, b({
    "data-shrink": u,
    ownerState: f,
    ref: n,
    className: pe(p.root, i)
  }, l, {
    classes: p
  }));
});
process.env.NODE_ENV !== "production" && (cm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   */
  color: r.oneOfType([r.oneOf(["error", "info", "primary", "secondary", "success", "warning"]), r.string]),
  /**
   * If `true`, the transition animation is disabled.
   * @default false
   */
  disableAnimation: r.bool,
  /**
   * If `true`, the component is disabled.
   */
  disabled: r.bool,
  /**
   * If `true`, the label is displayed in an error state.
   */
  error: r.bool,
  /**
   * If `true`, the `input` of this label is focused.
   */
  focused: r.bool,
  /**
   * If `dense`, will adjust vertical spacing. This is normally obtained via context from
   * FormControl.
   */
  margin: r.oneOf(["dense"]),
  /**
   * if `true`, the label will indicate that the `input` is required.
   */
  required: r.bool,
  /**
   * If `true`, the label is shrunk.
   */
  shrink: r.bool,
  /**
   * The size of the component.
   * @default 'normal'
   */
  size: r.oneOfType([r.oneOf(["normal", "small"]), r.string]),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The variant to use.
   */
  variant: r.oneOf(["filled", "outlined", "standard"])
});
const Ur = /* @__PURE__ */ g.createContext({});
process.env.NODE_ENV !== "production" && (Ur.displayName = "ListContext");
function tC(e) {
  return Pe("MuiList", e);
}
Ce("MuiList", ["root", "padding", "dense", "subheader"]);
const nC = ["children", "className", "component", "dense", "disablePadding", "subheader"], rC = (e) => {
  const {
    classes: t,
    disablePadding: n,
    dense: o,
    subheader: a
  } = e;
  return Se({
    root: ["root", !n && "padding", o && "dense", a && "subheader"]
  }, tC, t);
}, oC = Z("ul", {
  name: "MuiList",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, !n.disablePadding && t.padding, n.dense && t.dense, n.subheader && t.subheader];
  }
})(({
  ownerState: e
}) => b({
  listStyle: "none",
  margin: 0,
  padding: 0,
  position: "relative"
}, !e.disablePadding && {
  paddingTop: 8,
  paddingBottom: 8
}, e.subheader && {
  paddingTop: 0
})), ac = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiList"
  }), {
    children: a,
    className: s,
    component: i = "ul",
    dense: l = !1,
    disablePadding: c = !1,
    subheader: u
  } = o, d = ie(o, nC), f = g.useMemo(() => ({
    dense: l
  }), [l]), p = b({}, o, {
    component: i,
    dense: l,
    disablePadding: c
  }), m = rC(p);
  return /* @__PURE__ */ x.jsx(Ur.Provider, {
    value: f,
    children: /* @__PURE__ */ x.jsxs(oC, b({
      as: i,
      className: pe(m.root, s),
      ref: n,
      ownerState: p
    }, d, {
      children: [u, a]
    }))
  });
});
process.env.NODE_ENV !== "production" && (ac.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * If `true`, compact vertical padding designed for keyboard and mouse input is used for
   * the list and list items.
   * The prop is available to descendant components as the `dense` context.
   * @default false
   */
  dense: r.bool,
  /**
   * If `true`, vertical padding is removed from the list.
   * @default false
   */
  disablePadding: r.bool,
  /**
   * The content of the subheader, normally `ListSubheader`.
   */
  subheader: r.node,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function aC(e) {
  return Pe("MuiListItem", e);
}
const Nr = Ce("MuiListItem", ["root", "container", "focusVisible", "dense", "alignItemsFlexStart", "disabled", "divider", "gutters", "padding", "button", "secondaryAction", "selected"]), sC = Ce("MuiListItemButton", ["root", "focusVisible", "dense", "alignItemsFlexStart", "disabled", "divider", "gutters", "selected"]);
function iC(e) {
  return Pe("MuiListItemSecondaryAction", e);
}
Ce("MuiListItemSecondaryAction", ["root", "disableGutters"]);
const lC = ["className"], cC = (e) => {
  const {
    disableGutters: t,
    classes: n
  } = e;
  return Se({
    root: ["root", t && "disableGutters"]
  }, iC, n);
}, uC = Z("div", {
  name: "MuiListItemSecondaryAction",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.disableGutters && t.disableGutters];
  }
})(({
  ownerState: e
}) => b({
  position: "absolute",
  right: 16,
  top: "50%",
  transform: "translateY(-50%)"
}, e.disableGutters && {
  right: 0
})), sc = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiListItemSecondaryAction"
  }), {
    className: a
  } = o, s = ie(o, lC), i = g.useContext(Ur), l = b({}, o, {
    disableGutters: i.disableGutters
  }), c = cC(l);
  return /* @__PURE__ */ x.jsx(uC, b({
    className: pe(c.root, a),
    ownerState: l,
    ref: n
  }, s));
});
process.env.NODE_ENV !== "production" && (sc.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component, normally an `IconButton` or selection control.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
sc.muiName = "ListItemSecondaryAction";
const dC = ["className"], pC = ["alignItems", "autoFocus", "button", "children", "className", "component", "components", "componentsProps", "ContainerComponent", "ContainerProps", "dense", "disabled", "disableGutters", "disablePadding", "divider", "focusVisibleClassName", "secondaryAction", "selected", "slotProps", "slots"], fC = (e, t) => {
  const {
    ownerState: n
  } = e;
  return [t.root, n.dense && t.dense, n.alignItems === "flex-start" && t.alignItemsFlexStart, n.divider && t.divider, !n.disableGutters && t.gutters, !n.disablePadding && t.padding, n.button && t.button, n.hasSecondaryAction && t.secondaryAction];
}, mC = (e) => {
  const {
    alignItems: t,
    button: n,
    classes: o,
    dense: a,
    disabled: s,
    disableGutters: i,
    disablePadding: l,
    divider: c,
    hasSecondaryAction: u,
    selected: d
  } = e;
  return Se({
    root: ["root", a && "dense", !i && "gutters", !l && "padding", c && "divider", s && "disabled", n && "button", t === "flex-start" && "alignItemsFlexStart", u && "secondaryAction", d && "selected"],
    container: ["container"]
  }, aC, o);
}, hC = Z("div", {
  name: "MuiListItem",
  slot: "Root",
  overridesResolver: fC
})(({
  theme: e,
  ownerState: t
}) => b({
  display: "flex",
  justifyContent: "flex-start",
  alignItems: "center",
  position: "relative",
  textDecoration: "none",
  width: "100%",
  boxSizing: "border-box",
  textAlign: "left"
}, !t.disablePadding && b({
  paddingTop: 8,
  paddingBottom: 8
}, t.dense && {
  paddingTop: 4,
  paddingBottom: 4
}, !t.disableGutters && {
  paddingLeft: 16,
  paddingRight: 16
}, !!t.secondaryAction && {
  // Add some space to avoid collision as `ListItemSecondaryAction`
  // is absolutely positioned.
  paddingRight: 48
}), !!t.secondaryAction && {
  [`& > .${sC.root}`]: {
    paddingRight: 48
  }
}, {
  [`&.${Nr.focusVisible}`]: {
    backgroundColor: (e.vars || e).palette.action.focus
  },
  [`&.${Nr.selected}`]: {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.primary.mainChannel} / ${e.vars.palette.action.selectedOpacity})` : qe(e.palette.primary.main, e.palette.action.selectedOpacity),
    [`&.${Nr.focusVisible}`]: {
      backgroundColor: e.vars ? `rgba(${e.vars.palette.primary.mainChannel} / calc(${e.vars.palette.action.selectedOpacity} + ${e.vars.palette.action.focusOpacity}))` : qe(e.palette.primary.main, e.palette.action.selectedOpacity + e.palette.action.focusOpacity)
    }
  },
  [`&.${Nr.disabled}`]: {
    opacity: (e.vars || e).palette.action.disabledOpacity
  }
}, t.alignItems === "flex-start" && {
  alignItems: "flex-start"
}, t.divider && {
  borderBottom: `1px solid ${(e.vars || e).palette.divider}`,
  backgroundClip: "padding-box"
}, t.button && {
  transition: e.transitions.create("background-color", {
    duration: e.transitions.duration.shortest
  }),
  "&:hover": {
    textDecoration: "none",
    backgroundColor: (e.vars || e).palette.action.hover,
    // Reset on touch devices, it doesn't add specificity
    "@media (hover: none)": {
      backgroundColor: "transparent"
    }
  },
  [`&.${Nr.selected}:hover`]: {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.primary.mainChannel} / calc(${e.vars.palette.action.selectedOpacity} + ${e.vars.palette.action.hoverOpacity}))` : qe(e.palette.primary.main, e.palette.action.selectedOpacity + e.palette.action.hoverOpacity),
    // Reset on touch devices, it doesn't add specificity
    "@media (hover: none)": {
      backgroundColor: e.vars ? `rgba(${e.vars.palette.primary.mainChannel} / ${e.vars.palette.action.selectedOpacity})` : qe(e.palette.primary.main, e.palette.action.selectedOpacity)
    }
  }
}, t.hasSecondaryAction && {
  // Add some space to avoid collision as `ListItemSecondaryAction`
  // is absolutely positioned.
  paddingRight: 48
})), bC = Z("li", {
  name: "MuiListItem",
  slot: "Container",
  overridesResolver: (e, t) => t.container
})({
  position: "relative"
}), um = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiListItem"
  }), {
    alignItems: a = "center",
    autoFocus: s = !1,
    button: i = !1,
    children: l,
    className: c,
    component: u,
    components: d = {},
    componentsProps: f = {},
    ContainerComponent: p = "li",
    ContainerProps: {
      className: m
    } = {},
    dense: v = !1,
    disabled: h = !1,
    disableGutters: y = !1,
    disablePadding: w = !1,
    divider: C = !1,
    focusVisibleClassName: E,
    secondaryAction: O,
    selected: T = !1,
    slotProps: P = {},
    slots: S = {}
  } = o, j = ie(o.ContainerProps, dC), $ = ie(o, pC), V = g.useContext(Ur), _ = g.useMemo(() => ({
    dense: v || V.dense || !1,
    alignItems: a,
    disableGutters: y
  }), [a, V.dense, v, y]), L = g.useRef(null);
  ft(() => {
    s && (L.current ? L.current.focus() : process.env.NODE_ENV !== "production" && console.error("MUI: Unable to set focus to a ListItem whose component has not been rendered."));
  }, [s]);
  const M = g.Children.toArray(l), R = M.length && Br(M[M.length - 1], ["ListItemSecondaryAction"]), D = b({}, o, {
    alignItems: a,
    autoFocus: s,
    button: i,
    dense: _.dense,
    disabled: h,
    disableGutters: y,
    disablePadding: w,
    divider: C,
    hasSecondaryAction: R,
    selected: T
  }), F = mC(D), z = Ke(L, n), N = S.root || d.Root || hC, q = P.root || f.root || {}, A = b({
    className: pe(F.root, q.className, c),
    disabled: h
  }, $);
  let H = u || "li";
  return i && (A.component = u || "div", A.focusVisibleClassName = pe(Nr.focusVisible, E), H = lr), R ? (H = !A.component && !u ? "div" : H, p === "li" && (H === "li" ? H = "div" : A.component === "li" && (A.component = "div")), /* @__PURE__ */ x.jsx(Ur.Provider, {
    value: _,
    children: /* @__PURE__ */ x.jsxs(bC, b({
      as: p,
      className: pe(F.container, m),
      ref: z,
      ownerState: D
    }, j, {
      children: [/* @__PURE__ */ x.jsx(N, b({}, q, !Zr(N) && {
        as: H,
        ownerState: b({}, D, q.ownerState)
      }, A, {
        children: M
      })), M.pop()]
    }))
  })) : /* @__PURE__ */ x.jsx(Ur.Provider, {
    value: _,
    children: /* @__PURE__ */ x.jsxs(N, b({}, q, {
      as: H,
      ref: z
    }, !Zr(N) && {
      ownerState: b({}, D, q.ownerState)
    }, A, {
      children: [M, O && /* @__PURE__ */ x.jsx(sc, {
        children: O
      })]
    }))
  });
});
process.env.NODE_ENV !== "production" && (um.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Defines the `align-items` style property.
   * @default 'center'
   */
  alignItems: r.oneOf(["center", "flex-start"]),
  /**
   * If `true`, the list item is focused during the first mount.
   * Focus will also be triggered if the value changes from false to true.
   * @default false
   * @deprecated checkout [ListItemButton](/material-ui/api/list-item-button/) instead
   */
  autoFocus: r.bool,
  /**
   * If `true`, the list item is a button (using `ButtonBase`). Props intended
   * for `ButtonBase` can then be applied to `ListItem`.
   * @default false
   * @deprecated checkout [ListItemButton](/material-ui/api/list-item-button/) instead
   */
  button: r.bool,
  /**
   * The content of the component if a `ListItemSecondaryAction` is used it must
   * be the last child.
   */
  children: Sn(r.node, (e) => {
    const t = g.Children.toArray(e.children);
    let n = -1;
    for (let o = t.length - 1; o >= 0; o -= 1) {
      const a = t[o];
      if (Br(a, ["ListItemSecondaryAction"])) {
        n = o;
        break;
      }
    }
    return n !== -1 && n !== t.length - 1 ? new Error("MUI: You used an element after ListItemSecondaryAction. For ListItem to detect that it has a secondary action you must pass it as the last child to ListItem.") : null;
  }),
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `slots` prop.
   * It's recommended to use the `slots` prop instead.
   *
   * @default {}
   */
  components: r.shape({
    Root: r.elementType
  }),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `slotProps` prop.
   * It's recommended to use the `slotProps` prop instead, as `componentsProps` will be deprecated in the future.
   *
   * @default {}
   */
  componentsProps: r.shape({
    root: r.object
  }),
  /**
   * The container component used when a `ListItemSecondaryAction` is the last child.
   * @default 'li'
   * @deprecated
   */
  ContainerComponent: hs,
  /**
   * Props applied to the container component if used.
   * @default {}
   * @deprecated
   */
  ContainerProps: r.object,
  /**
   * If `true`, compact vertical padding designed for keyboard and mouse input is used.
   * The prop defaults to the value inherited from the parent List component.
   * @default false
   */
  dense: r.bool,
  /**
   * If `true`, the component is disabled.
   * @default false
   * @deprecated checkout [ListItemButton](/material-ui/api/list-item-button/) instead
   */
  disabled: r.bool,
  /**
   * If `true`, the left and right padding is removed.
   * @default false
   */
  disableGutters: r.bool,
  /**
   * If `true`, all padding is removed.
   * @default false
   */
  disablePadding: r.bool,
  /**
   * If `true`, a 1px light border is added to the bottom of the list item.
   * @default false
   */
  divider: r.bool,
  /**
   * @ignore
   */
  focusVisibleClassName: r.string,
  /**
   * The element to display at the end of ListItem.
   */
  secondaryAction: r.node,
  /**
   * Use to apply selected styling.
   * @default false
   * @deprecated checkout [ListItemButton](/material-ui/api/list-item-button/) instead
   */
  selected: r.bool,
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `componentsProps` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slotProps: r.shape({
    root: r.object
  }),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `components` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slots: r.shape({
    root: r.elementType
  }),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
const gC = ["actions", "autoFocus", "autoFocusItem", "children", "className", "disabledItemsFocusable", "disableListWrap", "onKeyDown", "variant"];
function Ei(e, t, n) {
  return e === t ? e.firstChild : t && t.nextElementSibling ? t.nextElementSibling : n ? null : e.firstChild;
}
function md(e, t, n) {
  return e === t ? n ? e.firstChild : e.lastChild : t && t.previousElementSibling ? t.previousElementSibling : n ? null : e.lastChild;
}
function dm(e, t) {
  if (t === void 0)
    return !0;
  let n = e.innerText;
  return n === void 0 && (n = e.textContent), n = n.trim().toLowerCase(), n.length === 0 ? !1 : t.repeating ? n[0] === t.keys[0] : n.indexOf(t.keys.join("")) === 0;
}
function Co(e, t, n, o, a, s) {
  let i = !1, l = a(e, t, t ? n : !1);
  for (; l; ) {
    if (l === e.firstChild) {
      if (i)
        return !1;
      i = !0;
    }
    const c = o ? !1 : l.disabled || l.getAttribute("aria-disabled") === "true";
    if (!l.hasAttribute("tabindex") || !dm(l, s) || c)
      l = a(e, l, n);
    else
      return l.focus(), !0;
  }
  return !1;
}
const pm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const {
    // private
    // eslint-disable-next-line react/prop-types
    actions: o,
    autoFocus: a = !1,
    autoFocusItem: s = !1,
    children: i,
    className: l,
    disabledItemsFocusable: c = !1,
    disableListWrap: u = !1,
    onKeyDown: d,
    variant: f = "selectedMenu"
  } = t, p = ie(t, gC), m = g.useRef(null), v = g.useRef({
    keys: [],
    repeating: !0,
    previousKeyMatched: !0,
    lastTime: null
  });
  ft(() => {
    a && m.current.focus();
  }, [a]), g.useImperativeHandle(o, () => ({
    adjustStyleForScrollbar: (E, {
      direction: O
    }) => {
      const T = !m.current.style.width;
      if (E.clientHeight < m.current.clientHeight && T) {
        const P = `${Np(dt(E))}px`;
        m.current.style[O === "rtl" ? "paddingLeft" : "paddingRight"] = P, m.current.style.width = `calc(100% + ${P})`;
      }
      return m.current;
    }
  }), []);
  const h = (E) => {
    const O = m.current, T = E.key, P = dt(O).activeElement;
    if (T === "ArrowDown")
      E.preventDefault(), Co(O, P, u, c, Ei);
    else if (T === "ArrowUp")
      E.preventDefault(), Co(O, P, u, c, md);
    else if (T === "Home")
      E.preventDefault(), Co(O, null, u, c, Ei);
    else if (T === "End")
      E.preventDefault(), Co(O, null, u, c, md);
    else if (T.length === 1) {
      const S = v.current, j = T.toLowerCase(), $ = performance.now();
      S.keys.length > 0 && ($ - S.lastTime > 500 ? (S.keys = [], S.repeating = !0, S.previousKeyMatched = !0) : S.repeating && j !== S.keys[0] && (S.repeating = !1)), S.lastTime = $, S.keys.push(j);
      const V = P && !S.repeating && dm(P, S);
      S.previousKeyMatched && (V || Co(O, P, !1, c, Ei, S)) ? E.preventDefault() : S.previousKeyMatched = !1;
    }
    d && d(E);
  }, y = Ke(m, n);
  let w = -1;
  g.Children.forEach(i, (E, O) => {
    if (!/* @__PURE__ */ g.isValidElement(E)) {
      w === O && (w += 1, w >= i.length && (w = -1));
      return;
    }
    process.env.NODE_ENV !== "production" && zo.isFragment(E) && console.error(["MUI: The Menu component doesn't accept a Fragment as a child.", "Consider providing an array instead."].join(`
`)), E.props.disabled || (f === "selectedMenu" && E.props.selected || w === -1) && (w = O), w === O && (E.props.disabled || E.props.muiSkipListHighlight || E.type.muiSkipListHighlight) && (w += 1, w >= i.length && (w = -1));
  });
  const C = g.Children.map(i, (E, O) => {
    if (O === w) {
      const T = {};
      return s && (T.autoFocus = !0), E.props.tabIndex === void 0 && f === "selectedMenu" && (T.tabIndex = 0), /* @__PURE__ */ g.cloneElement(E, T);
    }
    return E;
  });
  return /* @__PURE__ */ x.jsx(ac, b({
    role: "menu",
    ref: y,
    className: l,
    onKeyDown: h,
    tabIndex: a ? 0 : -1
  }, p, {
    children: C
  }));
});
process.env.NODE_ENV !== "production" && (pm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * If `true`, will focus the `[role="menu"]` container and move into tab order.
   * @default false
   */
  autoFocus: r.bool,
  /**
   * If `true`, will focus the first menuitem if `variant="menu"` or selected item
   * if `variant="selectedMenu"`.
   * @default false
   */
  autoFocusItem: r.bool,
  /**
   * MenuList contents, normally `MenuItem`s.
   */
  children: r.node,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * If `true`, will allow focus on disabled items.
   * @default false
   */
  disabledItemsFocusable: r.bool,
  /**
   * If `true`, the menu items will not wrap focus.
   * @default false
   */
  disableListWrap: r.bool,
  /**
   * @ignore
   */
  onKeyDown: r.func,
  /**
   * The variant to use. Use `menu` to prevent selected items from impacting the initial focus
   * and the vertical alignment relative to the anchor element.
   * @default 'selectedMenu'
   */
  variant: r.oneOf(["menu", "selectedMenu"])
});
function yC(e) {
  return Pe("MuiPopover", e);
}
Ce("MuiPopover", ["root", "paper"]);
const vC = ["onEntering"], xC = ["action", "anchorEl", "anchorOrigin", "anchorPosition", "anchorReference", "children", "className", "container", "elevation", "marginThreshold", "open", "PaperProps", "slots", "slotProps", "transformOrigin", "TransitionComponent", "transitionDuration", "TransitionProps", "disableScrollLock"], TC = ["slotProps"];
function hd(e, t) {
  let n = 0;
  return typeof t == "number" ? n = t : t === "center" ? n = e.height / 2 : t === "bottom" && (n = e.height), n;
}
function bd(e, t) {
  let n = 0;
  return typeof t == "number" ? n = t : t === "center" ? n = e.width / 2 : t === "right" && (n = e.width), n;
}
function gd(e) {
  return [e.horizontal, e.vertical].map((t) => typeof t == "number" ? `${t}px` : t).join(" ");
}
function Ha(e) {
  return typeof e == "function" ? e() : e;
}
const wC = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"],
    paper: ["paper"]
  }, yC, t);
}, EC = Z(Jl, {
  name: "MuiPopover",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({}), fm = Z(pa, {
  name: "MuiPopover",
  slot: "Paper",
  overridesResolver: (e, t) => t.paper
})({
  position: "absolute",
  overflowY: "auto",
  overflowX: "hidden",
  // So we see the popover when it's empty.
  // It's most likely on issue on userland.
  minWidth: 16,
  minHeight: 16,
  maxWidth: "calc(100% - 32px)",
  maxHeight: "calc(100% - 32px)",
  // We disable the focus ring for mouse, touch and keyboard users.
  outline: 0
}), mm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s;
  const i = Ee({
    props: t,
    name: "MuiPopover"
  }), {
    action: l,
    anchorEl: c,
    anchorOrigin: u = {
      vertical: "top",
      horizontal: "left"
    },
    anchorPosition: d,
    anchorReference: f = "anchorEl",
    children: p,
    className: m,
    container: v,
    elevation: h = 8,
    marginThreshold: y = 16,
    open: w,
    PaperProps: C = {},
    slots: E,
    slotProps: O,
    transformOrigin: T = {
      vertical: "top",
      horizontal: "left"
    },
    TransitionComponent: P = ro,
    transitionDuration: S = "auto",
    TransitionProps: {
      onEntering: j
    } = {},
    disableScrollLock: $ = !1
  } = i, V = ie(i.TransitionProps, vC), _ = ie(i, xC), L = (o = O == null ? void 0 : O.paper) != null ? o : C, M = g.useRef(), R = Ke(M, L.ref), D = b({}, i, {
    anchorOrigin: u,
    anchorReference: f,
    elevation: h,
    marginThreshold: y,
    externalPaperSlotProps: L,
    transformOrigin: T,
    TransitionComponent: P,
    transitionDuration: S,
    TransitionProps: V
  }), F = wC(D), z = g.useCallback(() => {
    if (f === "anchorPosition")
      return process.env.NODE_ENV !== "production" && (d || console.error('MUI: You need to provide a `anchorPosition` prop when using <Popover anchorReference="anchorPosition" />.')), d;
    const K = Ha(c), Y = K && K.nodeType === 1 ? K : dt(M.current).body, he = Y.getBoundingClientRect();
    if (process.env.NODE_ENV !== "production") {
      const Oe = Y.getBoundingClientRect();
      process.env.NODE_ENV !== "test" && Oe.top === 0 && Oe.left === 0 && Oe.right === 0 && Oe.bottom === 0 && console.warn(["MUI: The `anchorEl` prop provided to the component is invalid.", "The anchor element should be part of the document layout.", "Make sure the element is present in the document or that it's not display none."].join(`
`));
    }
    return {
      top: he.top + hd(he, u.vertical),
      left: he.left + bd(he, u.horizontal)
    };
  }, [c, u.horizontal, u.vertical, d, f]), N = g.useCallback((K) => ({
    vertical: hd(K, T.vertical),
    horizontal: bd(K, T.horizontal)
  }), [T.horizontal, T.vertical]), q = g.useCallback((K) => {
    const Y = {
      width: K.offsetWidth,
      height: K.offsetHeight
    }, he = N(Y);
    if (f === "none")
      return {
        top: null,
        left: null,
        transformOrigin: gd(he)
      };
    const Oe = z();
    let Ne = Oe.top - he.vertical, fe = Oe.left - he.horizontal;
    const ve = Ne + Y.height, oe = fe + Y.width, ce = Fn(Ha(c)), I = ce.innerHeight - y, Q = ce.innerWidth - y;
    if (y !== null && Ne < y) {
      const ne = Ne - y;
      Ne -= ne, he.vertical += ne;
    } else if (y !== null && ve > I) {
      const ne = ve - I;
      Ne -= ne, he.vertical += ne;
    }
    if (process.env.NODE_ENV !== "production" && Y.height > I && Y.height && I && console.error(["MUI: The popover component is too tall.", `Some part of it can not be seen on the screen (${Y.height - I}px).`, "Please consider adding a `max-height` to improve the user-experience."].join(`
`)), y !== null && fe < y) {
      const ne = fe - y;
      fe -= ne, he.horizontal += ne;
    } else if (oe > Q) {
      const ne = oe - Q;
      fe -= ne, he.horizontal += ne;
    }
    return {
      top: `${Math.round(Ne)}px`,
      left: `${Math.round(fe)}px`,
      transformOrigin: gd(he)
    };
  }, [c, f, z, N, y]), [A, H] = g.useState(w), te = g.useCallback(() => {
    const K = M.current;
    if (!K)
      return;
    const Y = q(K);
    Y.top !== null && (K.style.top = Y.top), Y.left !== null && (K.style.left = Y.left), K.style.transformOrigin = Y.transformOrigin, H(!0);
  }, [q]);
  g.useEffect(() => ($ && window.addEventListener("scroll", te), () => window.removeEventListener("scroll", te)), [c, $, te]);
  const re = (K, Y) => {
    j && j(K, Y), te();
  }, B = () => {
    H(!1);
  };
  g.useEffect(() => {
    w && te();
  }), g.useImperativeHandle(l, () => w ? {
    updatePosition: () => {
      te();
    }
  } : null, [w, te]), g.useEffect(() => {
    if (!w)
      return;
    const K = yl(() => {
      te();
    }), Y = Fn(c);
    return Y.addEventListener("resize", K), () => {
      K.clear(), Y.removeEventListener("resize", K);
    };
  }, [c, w, te]);
  let G = S;
  S === "auto" && !P.muiSupportAuto && (G = void 0);
  const ee = v || (c ? dt(Ha(c)).body : void 0), W = (a = E == null ? void 0 : E.root) != null ? a : EC, J = (s = E == null ? void 0 : E.paper) != null ? s : fm, se = Ye({
    elementType: J,
    externalSlotProps: b({}, L, {
      style: A ? L.style : b({}, L.style, {
        opacity: 0
      })
    }),
    additionalProps: {
      elevation: h,
      ref: R
    },
    ownerState: D,
    className: pe(F.paper, L == null ? void 0 : L.className)
  }), le = Ye({
    elementType: W,
    externalSlotProps: (O == null ? void 0 : O.root) || {},
    externalForwardedProps: _,
    additionalProps: {
      ref: n,
      slotProps: {
        backdrop: {
          invisible: !0
        }
      },
      container: ee,
      open: w
    },
    ownerState: D,
    className: pe(F.root, m)
  }), {
    slotProps: X
  } = le, U = ie(le, TC);
  return /* @__PURE__ */ x.jsx(W, b({}, U, !Zr(W) && {
    slotProps: X,
    disableScrollLock: $
  }, {
    children: /* @__PURE__ */ x.jsx(P, b({
      appear: !0,
      in: w,
      onEntering: re,
      onExited: B,
      timeout: G
    }, V, {
      children: /* @__PURE__ */ x.jsx(J, b({}, se, {
        children: p
      }))
    }))
  }));
});
process.env.NODE_ENV !== "production" && (mm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * A ref for imperative actions.
   * It currently only supports updatePosition() action.
   */
  action: vt,
  /**
   * An HTML element, [PopoverVirtualElement](/material-ui/react-popover/#virtual-element),
   * or a function that returns either.
   * It's used to set the position of the popover.
   */
  anchorEl: Sn(r.oneOfType([Tn, r.func]), (e) => {
    if (e.open && (!e.anchorReference || e.anchorReference === "anchorEl")) {
      const t = Ha(e.anchorEl);
      if (t && t.nodeType === 1) {
        const n = t.getBoundingClientRect();
        if (process.env.NODE_ENV !== "test" && n.top === 0 && n.left === 0 && n.right === 0 && n.bottom === 0)
          return new Error(["MUI: The `anchorEl` prop provided to the component is invalid.", "The anchor element should be part of the document layout.", "Make sure the element is present in the document or that it's not display none."].join(`
`));
      } else
        return new Error(["MUI: The `anchorEl` prop provided to the component is invalid.", `It should be an Element or PopoverVirtualElement instance but it's \`${t}\` instead.`].join(`
`));
    }
    return null;
  }),
  /**
   * This is the point on the anchor where the popover's
   * `anchorEl` will attach to. This is not used when the
   * anchorReference is 'anchorPosition'.
   *
   * Options:
   * vertical: [top, center, bottom];
   * horizontal: [left, center, right].
   * @default {
   *   vertical: 'top',
   *   horizontal: 'left',
   * }
   */
  anchorOrigin: r.shape({
    horizontal: r.oneOfType([r.oneOf(["center", "left", "right"]), r.number]).isRequired,
    vertical: r.oneOfType([r.oneOf(["bottom", "center", "top"]), r.number]).isRequired
  }),
  /**
   * This is the position that may be used to set the position of the popover.
   * The coordinates are relative to the application's client area.
   */
  anchorPosition: r.shape({
    left: r.number.isRequired,
    top: r.number.isRequired
  }),
  /**
   * This determines which anchor prop to refer to when setting
   * the position of the popover.
   * @default 'anchorEl'
   */
  anchorReference: r.oneOf(["anchorEl", "anchorPosition", "none"]),
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * An HTML element, component instance, or function that returns either.
   * The `container` will passed to the Modal component.
   *
   * By default, it uses the body of the anchorEl's top-level document object,
   * so it's simply `document.body` most of the time.
   */
  container: r.oneOfType([Tn, r.func]),
  /**
   * Disable the scroll lock behavior.
   * @default false
   */
  disableScrollLock: r.bool,
  /**
   * The elevation of the popover.
   * @default 8
   */
  elevation: Fp,
  /**
   * Specifies how close to the edge of the window the popover can appear.
   * If null, the popover will not be constrained by the window.
   * @default 16
   */
  marginThreshold: r.number,
  /**
   * Callback fired when the component requests to be closed.
   * The `reason` parameter can optionally be used to control the response to `onClose`.
   */
  onClose: r.func,
  /**
   * If `true`, the component is shown.
   */
  open: r.bool.isRequired,
  /**
   * Props applied to the [`Paper`](/material-ui/api/paper/) element.
   *
   * This prop is an alias for `slotProps.paper` and will be overriden by it if both are used.
   * @deprecated Use `slotProps.paper` instead.
   *
   * @default {}
   */
  PaperProps: r.shape({
    component: hs
  }),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * @default {}
   */
  slotProps: r.shape({
    paper: r.oneOfType([r.func, r.object]),
    root: r.oneOfType([r.func, r.object])
  }),
  /**
   * The components used for each slot inside.
   *
   * @default {}
   */
  slots: r.shape({
    paper: r.elementType,
    root: r.elementType
  }),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * This is the point on the popover which
   * will attach to the anchor's origin.
   *
   * Options:
   * vertical: [top, center, bottom, x(px)];
   * horizontal: [left, center, right, x(px)].
   * @default {
   *   vertical: 'top',
   *   horizontal: 'left',
   * }
   */
  transformOrigin: r.shape({
    horizontal: r.oneOfType([r.oneOf(["center", "left", "right"]), r.number]).isRequired,
    vertical: r.oneOfType([r.oneOf(["bottom", "center", "top"]), r.number]).isRequired
  }),
  /**
   * The component used for the transition.
   * [Follow this guide](/material-ui/transitions/#transitioncomponent-prop) to learn more about the requirements for this component.
   * @default Grow
   */
  TransitionComponent: r.elementType,
  /**
   * Set to 'auto' to automatically calculate transition time based on height.
   * @default 'auto'
   */
  transitionDuration: r.oneOfType([r.oneOf(["auto"]), r.number, r.shape({
    appear: r.number,
    enter: r.number,
    exit: r.number
  })]),
  /**
   * Props applied to the transition element.
   * By default, the element is based on this [`Transition`](https://reactcommunity.org/react-transition-group/transition/) component.
   * @default {}
   */
  TransitionProps: r.object
});
function CC(e) {
  return Pe("MuiMenu", e);
}
Ce("MuiMenu", ["root", "paper", "list"]);
const OC = ["onEntering"], SC = ["autoFocus", "children", "className", "disableAutoFocusItem", "MenuListProps", "onClose", "open", "PaperProps", "PopoverClasses", "transitionDuration", "TransitionProps", "variant", "slots", "slotProps"], PC = {
  vertical: "top",
  horizontal: "right"
}, RC = {
  vertical: "top",
  horizontal: "left"
}, DC = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"],
    paper: ["paper"],
    list: ["list"]
  }, CC, t);
}, $C = Z(mm, {
  shouldForwardProp: (e) => nn(e) || e === "classes",
  name: "MuiMenu",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({}), kC = Z(fm, {
  name: "MuiMenu",
  slot: "Paper",
  overridesResolver: (e, t) => t.paper
})({
  // specZ: The maximum height of a simple menu should be one or more rows less than the view
  // height. This ensures a tappable area outside of the simple menu with which to dismiss
  // the menu.
  maxHeight: "calc(100% - 96px)",
  // Add iOS momentum scrolling for iOS < 13.0
  WebkitOverflowScrolling: "touch"
}), _C = Z(pm, {
  name: "MuiMenu",
  slot: "List",
  overridesResolver: (e, t) => t.list
})({
  // We disable the focus ring for mouse, touch and keyboard users.
  outline: 0
}), hm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a;
  const s = Ee({
    props: t,
    name: "MuiMenu"
  }), {
    autoFocus: i = !0,
    children: l,
    className: c,
    disableAutoFocusItem: u = !1,
    MenuListProps: d = {},
    onClose: f,
    open: p,
    PaperProps: m = {},
    PopoverClasses: v,
    transitionDuration: h = "auto",
    TransitionProps: {
      onEntering: y
    } = {},
    variant: w = "selectedMenu",
    slots: C = {},
    slotProps: E = {}
  } = s, O = ie(s.TransitionProps, OC), T = ie(s, SC), P = kf(), S = b({}, s, {
    autoFocus: i,
    disableAutoFocusItem: u,
    MenuListProps: d,
    onEntering: y,
    PaperProps: m,
    transitionDuration: h,
    TransitionProps: O,
    variant: w
  }), j = DC(S), $ = i && !u && p, V = g.useRef(null), _ = (N, q) => {
    V.current && V.current.adjustStyleForScrollbar(N, {
      direction: P ? "rtl" : "ltr"
    }), y && y(N, q);
  }, L = (N) => {
    N.key === "Tab" && (N.preventDefault(), f && f(N, "tabKeyDown"));
  };
  let M = -1;
  g.Children.map(l, (N, q) => {
    /* @__PURE__ */ g.isValidElement(N) && (process.env.NODE_ENV !== "production" && zo.isFragment(N) && console.error(["MUI: The Menu component doesn't accept a Fragment as a child.", "Consider providing an array instead."].join(`
`)), N.props.disabled || (w === "selectedMenu" && N.props.selected || M === -1) && (M = q));
  });
  const R = (o = C.paper) != null ? o : kC, D = (a = E.paper) != null ? a : m, F = Ye({
    elementType: C.root,
    externalSlotProps: E.root,
    ownerState: S,
    className: [j.root, c]
  }), z = Ye({
    elementType: R,
    externalSlotProps: D,
    ownerState: S,
    className: j.paper
  });
  return /* @__PURE__ */ x.jsx($C, b({
    onClose: f,
    anchorOrigin: {
      vertical: "bottom",
      horizontal: P ? "right" : "left"
    },
    transformOrigin: P ? PC : RC,
    slots: {
      paper: R,
      root: C.root
    },
    slotProps: {
      root: F,
      paper: z
    },
    open: p,
    ref: n,
    transitionDuration: h,
    TransitionProps: b({
      onEntering: _
    }, O),
    ownerState: S
  }, T, {
    classes: v,
    children: /* @__PURE__ */ x.jsx(_C, b({
      onKeyDown: L,
      actions: V,
      autoFocus: i && (M === -1 || u),
      autoFocusItem: $,
      variant: w
    }, d, {
      className: pe(j.list, d.className),
      children: l
    }))
  }));
});
process.env.NODE_ENV !== "production" && (hm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * An HTML element, or a function that returns one.
   * It's used to set the position of the menu.
   */
  anchorEl: r.oneOfType([Tn, r.func]),
  /**
   * If `true` (Default) will focus the `[role="menu"]` if no focusable child is found. Disabled
   * children are not focusable. If you set this prop to `false` focus will be placed
   * on the parent modal container. This has severe accessibility implications
   * and should only be considered if you manage focus otherwise.
   * @default true
   */
  autoFocus: r.bool,
  /**
   * Menu contents, normally `MenuItem`s.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * When opening the menu will not focus the active item but the `[role="menu"]`
   * unless `autoFocus` is also set to `false`. Not using the default means not
   * following WAI-ARIA authoring practices. Please be considerate about possible
   * accessibility implications.
   * @default false
   */
  disableAutoFocusItem: r.bool,
  /**
   * Props applied to the [`MenuList`](/material-ui/api/menu-list/) element.
   * @default {}
   */
  MenuListProps: r.object,
  /**
   * Callback fired when the component requests to be closed.
   *
   * @param {object} event The event source of the callback.
   * @param {string} reason Can be: `"escapeKeyDown"`, `"backdropClick"`, `"tabKeyDown"`.
   */
  onClose: r.func,
  /**
   * If `true`, the component is shown.
   */
  open: r.bool.isRequired,
  /**
   * @ignore
   */
  PaperProps: r.object,
  /**
   * `classes` prop applied to the [`Popover`](/material-ui/api/popover/) element.
   */
  PopoverClasses: r.object,
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * @default {}
   */
  slotProps: r.shape({
    paper: r.oneOfType([r.func, r.object]),
    root: r.oneOfType([r.func, r.object])
  }),
  /**
   * The components used for each slot inside.
   *
   * @default {}
   */
  slots: r.shape({
    paper: r.elementType,
    root: r.elementType
  }),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The length of the transition in `ms`, or 'auto'
   * @default 'auto'
   */
  transitionDuration: r.oneOfType([r.oneOf(["auto"]), r.number, r.shape({
    appear: r.number,
    enter: r.number,
    exit: r.number
  })]),
  /**
   * Props applied to the transition element.
   * By default, the element is based on this [`Transition`](https://reactcommunity.org/react-transition-group/transition/) component.
   * @default {}
   */
  TransitionProps: r.object,
  /**
   * The variant to use. Use `menu` to prevent selected items from impacting the initial focus.
   * @default 'selectedMenu'
   */
  variant: r.oneOf(["menu", "selectedMenu"])
});
function MC(e) {
  return Pe("MuiNativeSelect", e);
}
const ic = Ce("MuiNativeSelect", ["root", "select", "multiple", "filled", "outlined", "standard", "disabled", "icon", "iconOpen", "iconFilled", "iconOutlined", "iconStandard", "nativeInput", "error"]), IC = ["className", "disabled", "error", "IconComponent", "inputRef", "variant"], NC = (e) => {
  const {
    classes: t,
    variant: n,
    disabled: o,
    multiple: a,
    open: s,
    error: i
  } = e, l = {
    select: ["select", n, o && "disabled", a && "multiple", i && "error"],
    icon: ["icon", `icon${de(n)}`, s && "iconOpen", o && "disabled"]
  };
  return Se(l, MC, t);
}, bm = ({
  ownerState: e,
  theme: t
}) => b({
  MozAppearance: "none",
  // Reset
  WebkitAppearance: "none",
  // Reset
  // When interacting quickly, the text can end up selected.
  // Native select can't be selected either.
  userSelect: "none",
  borderRadius: 0,
  // Reset
  cursor: "pointer",
  "&:focus": b({}, t.vars ? {
    backgroundColor: `rgba(${t.vars.palette.common.onBackgroundChannel} / 0.05)`
  } : {
    backgroundColor: t.palette.mode === "light" ? "rgba(0, 0, 0, 0.05)" : "rgba(255, 255, 255, 0.05)"
  }, {
    borderRadius: 0
    // Reset Chrome style
  }),
  // Remove IE11 arrow
  "&::-ms-expand": {
    display: "none"
  },
  [`&.${ic.disabled}`]: {
    cursor: "default"
  },
  "&[multiple]": {
    height: "auto"
  },
  "&:not([multiple]) option, &:not([multiple]) optgroup": {
    backgroundColor: (t.vars || t).palette.background.paper
  },
  // Bump specificity to allow extending custom inputs
  "&&&": {
    paddingRight: 24,
    minWidth: 16
    // So it doesn't collapse.
  }
}, e.variant === "filled" && {
  "&&&": {
    paddingRight: 32
  }
}, e.variant === "outlined" && {
  borderRadius: (t.vars || t).shape.borderRadius,
  "&:focus": {
    borderRadius: (t.vars || t).shape.borderRadius
    // Reset the reset for Chrome style
  },
  "&&&": {
    paddingRight: 32
  }
}), jC = Z("select", {
  name: "MuiNativeSelect",
  slot: "Select",
  shouldForwardProp: nn,
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.select, t[n.variant], n.error && t.error, {
      [`&.${ic.multiple}`]: t.multiple
    }];
  }
})(bm), gm = ({
  ownerState: e,
  theme: t
}) => b({
  // We use a position absolute over a flexbox in order to forward the pointer events
  // to the input and to support wrapping tags..
  position: "absolute",
  right: 0,
  top: "calc(50% - .5em)",
  // Center vertically, height is 1em
  pointerEvents: "none",
  // Don't block pointer events on the select under the icon.
  color: (t.vars || t).palette.action.active,
  [`&.${ic.disabled}`]: {
    color: (t.vars || t).palette.action.disabled
  }
}, e.open && {
  transform: "rotate(180deg)"
}, e.variant === "filled" && {
  right: 7
}, e.variant === "outlined" && {
  right: 7
}), AC = Z("svg", {
  name: "MuiNativeSelect",
  slot: "Icon",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.icon, n.variant && t[`icon${de(n.variant)}`], n.open && t.iconOpen];
  }
})(gm), ym = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const {
    className: o,
    disabled: a,
    error: s,
    IconComponent: i,
    inputRef: l,
    variant: c = "standard"
  } = t, u = ie(t, IC), d = b({}, t, {
    disabled: a,
    variant: c,
    error: s
  }), f = NC(d);
  return /* @__PURE__ */ x.jsxs(g.Fragment, {
    children: [/* @__PURE__ */ x.jsx(jC, b({
      ownerState: d,
      className: pe(f.select, o),
      disabled: a,
      ref: l || n
    }, u)), t.multiple ? null : /* @__PURE__ */ x.jsx(AC, {
      as: i,
      ownerState: d,
      className: f.icon
    })]
  });
});
process.env.NODE_ENV !== "production" && (ym.propTypes = {
  /**
   * The option elements to populate the select with.
   * Can be some `<option>` elements.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * The CSS class name of the select element.
   */
  className: r.string,
  /**
   * If `true`, the select is disabled.
   */
  disabled: r.bool,
  /**
   * If `true`, the `select input` will indicate an error.
   */
  error: r.bool,
  /**
   * The icon that displays the arrow.
   */
  IconComponent: r.elementType.isRequired,
  /**
   * Use that prop to pass a ref to the native select element.
   * @deprecated
   */
  inputRef: vt,
  /**
   * @ignore
   */
  multiple: r.bool,
  /**
   * Name attribute of the `select` or hidden `input` element.
   */
  name: r.string,
  /**
   * Callback fired when a menu item is selected.
   *
   * @param {object} event The event source of the callback.
   * You can pull out the new value by accessing `event.target.value` (string).
   */
  onChange: r.func,
  /**
   * The input value.
   */
  value: r.any,
  /**
   * The variant to use.
   */
  variant: r.oneOf(["standard", "outlined", "filled"])
});
var yd;
const FC = ["children", "classes", "className", "label", "notched"], VC = Z("fieldset", {
  shouldForwardProp: nn
})({
  textAlign: "left",
  position: "absolute",
  bottom: 0,
  right: 0,
  top: -5,
  left: 0,
  margin: 0,
  padding: "0 8px",
  pointerEvents: "none",
  borderRadius: "inherit",
  borderStyle: "solid",
  borderWidth: 1,
  overflow: "hidden",
  minWidth: "0%"
}), LC = Z("legend", {
  shouldForwardProp: nn
})(({
  ownerState: e,
  theme: t
}) => b({
  float: "unset",
  // Fix conflict with bootstrap
  width: "auto",
  // Fix conflict with bootstrap
  overflow: "hidden"
}, !e.withLabel && {
  padding: 0,
  lineHeight: "11px",
  // sync with `height` in `legend` styles
  transition: t.transitions.create("width", {
    duration: 150,
    easing: t.transitions.easing.easeOut
  })
}, e.withLabel && b({
  display: "block",
  // Fix conflict with normalize.css and sanitize.css
  padding: 0,
  height: 11,
  // sync with `lineHeight` in `legend` styles
  fontSize: "0.75em",
  visibility: "hidden",
  maxWidth: 0.01,
  transition: t.transitions.create("max-width", {
    duration: 50,
    easing: t.transitions.easing.easeOut
  }),
  whiteSpace: "nowrap",
  "& > span": {
    paddingLeft: 5,
    paddingRight: 5,
    display: "inline-block",
    opacity: 0,
    visibility: "visible"
  }
}, e.notched && {
  maxWidth: "100%",
  transition: t.transitions.create("max-width", {
    duration: 100,
    easing: t.transitions.easing.easeOut,
    delay: 50
  })
})));
function vm(e) {
  const {
    className: t,
    label: n,
    notched: o
  } = e, a = ie(e, FC), s = n != null && n !== "", i = b({}, e, {
    notched: o,
    withLabel: s
  });
  return /* @__PURE__ */ x.jsx(VC, b({
    "aria-hidden": !0,
    className: t,
    ownerState: i
  }, a, {
    children: /* @__PURE__ */ x.jsx(LC, {
      ownerState: i,
      children: s ? /* @__PURE__ */ x.jsx("span", {
        children: n
      }) : (
        // notranslate needed while Google Translate will not fix zero-width space issue
        yd || (yd = /* @__PURE__ */ x.jsx("span", {
          className: "notranslate",
          children: "​"
        }))
      )
    })
  }));
}
process.env.NODE_ENV !== "production" && (vm.propTypes = {
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The label.
   */
  label: r.node,
  /**
   * If `true`, the outline is notched to accommodate the label.
   */
  notched: r.bool.isRequired,
  /**
   * @ignore
   */
  style: r.object
});
const BC = ["components", "fullWidth", "inputComponent", "label", "multiline", "notched", "slots", "type"], zC = (e) => {
  const {
    classes: t
  } = e, o = Se({
    root: ["root"],
    notchedOutline: ["notchedOutline"],
    input: ["input"]
  }, ww, t);
  return b({}, t, o);
}, WC = Z(Vs, {
  shouldForwardProp: (e) => nn(e) || e === "classes",
  name: "MuiOutlinedInput",
  slot: "Root",
  overridesResolver: As
})(({
  theme: e,
  ownerState: t
}) => {
  const n = e.palette.mode === "light" ? "rgba(0, 0, 0, 0.23)" : "rgba(255, 255, 255, 0.23)";
  return b({
    position: "relative",
    borderRadius: (e.vars || e).shape.borderRadius,
    [`&:hover .${kn.notchedOutline}`]: {
      borderColor: (e.vars || e).palette.text.primary
    },
    // Reset on touch devices, it doesn't add specificity
    "@media (hover: none)": {
      [`&:hover .${kn.notchedOutline}`]: {
        borderColor: e.vars ? `rgba(${e.vars.palette.common.onBackgroundChannel} / 0.23)` : n
      }
    },
    [`&.${kn.focused} .${kn.notchedOutline}`]: {
      borderColor: (e.vars || e).palette[t.color].main,
      borderWidth: 2
    },
    [`&.${kn.error} .${kn.notchedOutline}`]: {
      borderColor: (e.vars || e).palette.error.main
    },
    [`&.${kn.disabled} .${kn.notchedOutline}`]: {
      borderColor: (e.vars || e).palette.action.disabled
    }
  }, t.startAdornment && {
    paddingLeft: 14
  }, t.endAdornment && {
    paddingRight: 14
  }, t.multiline && b({
    padding: "16.5px 14px"
  }, t.size === "small" && {
    padding: "8.5px 14px"
  }));
}), UC = Z(vm, {
  name: "MuiOutlinedInput",
  slot: "NotchedOutline",
  overridesResolver: (e, t) => t.notchedOutline
})(({
  theme: e
}) => {
  const t = e.palette.mode === "light" ? "rgba(0, 0, 0, 0.23)" : "rgba(255, 255, 255, 0.23)";
  return {
    borderColor: e.vars ? `rgba(${e.vars.palette.common.onBackgroundChannel} / 0.23)` : t
  };
}), HC = Z(Ls, {
  name: "MuiOutlinedInput",
  slot: "Input",
  overridesResolver: Fs
})(({
  theme: e,
  ownerState: t
}) => b({
  padding: "16.5px 14px"
}, !e.vars && {
  "&:-webkit-autofill": {
    WebkitBoxShadow: e.palette.mode === "light" ? null : "0 0 0 100px #266798 inset",
    WebkitTextFillColor: e.palette.mode === "light" ? null : "#fff",
    caretColor: e.palette.mode === "light" ? null : "#fff",
    borderRadius: "inherit"
  }
}, e.vars && {
  "&:-webkit-autofill": {
    borderRadius: "inherit"
  },
  [e.getColorSchemeSelector("dark")]: {
    "&:-webkit-autofill": {
      WebkitBoxShadow: "0 0 0 100px #266798 inset",
      WebkitTextFillColor: "#fff",
      caretColor: "#fff"
    }
  }
}, t.size === "small" && {
  padding: "8.5px 14px"
}, t.multiline && {
  padding: 0
}, t.startAdornment && {
  paddingLeft: 0
}, t.endAdornment && {
  paddingRight: 0
})), ba = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s, i, l;
  const c = Ee({
    props: t,
    name: "MuiOutlinedInput"
  }), {
    components: u = {},
    fullWidth: d = !1,
    inputComponent: f = "input",
    label: p,
    multiline: m = !1,
    notched: v,
    slots: h = {},
    type: y = "text"
  } = c, w = ie(c, BC), C = zC(c), E = fr(), O = fo({
    props: c,
    muiFormControl: E,
    states: ["color", "disabled", "error", "focused", "hiddenLabel", "size", "required"]
  }), T = b({}, c, {
    color: O.color || "primary",
    disabled: O.disabled,
    error: O.error,
    focused: O.focused,
    formControl: E,
    fullWidth: d,
    hiddenLabel: O.hiddenLabel,
    multiline: m,
    size: O.size,
    type: y
  }), P = (o = (a = h.root) != null ? a : u.Root) != null ? o : WC, S = (s = (i = h.input) != null ? i : u.Input) != null ? s : HC;
  return /* @__PURE__ */ x.jsx(Xl, b({
    slots: {
      root: P,
      input: S
    },
    renderSuffix: (j) => /* @__PURE__ */ x.jsx(UC, {
      ownerState: T,
      className: C.notchedOutline,
      label: p != null && p !== "" && O.required ? l || (l = /* @__PURE__ */ x.jsxs(g.Fragment, {
        children: [p, " ", "*"]
      })) : p,
      notched: typeof v < "u" ? v : !!(j.startAdornment || j.filled || j.focused)
    }),
    fullWidth: d,
    inputComponent: f,
    multiline: m,
    ref: n,
    type: y
  }, w, {
    classes: b({}, C, {
      notchedOutline: null
    })
  }));
});
process.env.NODE_ENV !== "production" && (ba.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * This prop helps users to fill forms faster, especially on mobile devices.
   * The name can be confusing, as it's more like an autofill.
   * You can learn more about it [following the specification](https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofill).
   */
  autoComplete: r.string,
  /**
   * If `true`, the `input` element is focused during the first mount.
   */
  autoFocus: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * The prop defaults to the value (`'primary'`) inherited from the parent FormControl component.
   */
  color: r.oneOfType([r.oneOf(["primary", "secondary"]), r.string]),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `slots` prop.
   * It's recommended to use the `slots` prop instead.
   *
   * @default {}
   */
  components: r.shape({
    Input: r.elementType,
    Root: r.elementType
  }),
  /**
   * The default value. Use when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the component is disabled.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  disabled: r.bool,
  /**
   * End `InputAdornment` for this component.
   */
  endAdornment: r.node,
  /**
   * If `true`, the `input` will indicate an error.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  error: r.bool,
  /**
   * If `true`, the `input` will take up the full width of its container.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * The id of the `input` element.
   */
  id: r.string,
  /**
   * The component used for the `input` element.
   * Either a string to use a HTML element or a component.
   * @default 'input'
   */
  inputComponent: r.elementType,
  /**
   * [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Attributes) applied to the `input` element.
   * @default {}
   */
  inputProps: r.object,
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * The label of the `input`. It is only used for layout. The actual labelling
   * is handled by `InputLabel`.
   */
  label: r.node,
  /**
   * If `dense`, will adjust vertical spacing. This is normally obtained via context from
   * FormControl.
   * The prop defaults to the value (`'none'`) inherited from the parent FormControl component.
   */
  margin: r.oneOf(["dense", "none"]),
  /**
   * Maximum number of rows to display when multiline option is set to true.
   */
  maxRows: r.oneOfType([r.number, r.string]),
  /**
   * Minimum number of rows to display when multiline option is set to true.
   */
  minRows: r.oneOfType([r.number, r.string]),
  /**
   * If `true`, a [TextareaAutosize](/material-ui/react-textarea-autosize/) element is rendered.
   * @default false
   */
  multiline: r.bool,
  /**
   * Name attribute of the `input` element.
   */
  name: r.string,
  /**
   * If `true`, the outline is notched to accommodate the label.
   */
  notched: r.bool,
  /**
   * Callback fired when the value is changed.
   *
   * @param {React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>} event The event source of the callback.
   * You can pull out the new value by accessing `event.target.value` (string).
   */
  onChange: r.func,
  /**
   * The short hint displayed in the `input` before the user enters a value.
   */
  placeholder: r.string,
  /**
   * It prevents the user from changing the value of the field
   * (not from interacting with the field).
   */
  readOnly: r.bool,
  /**
   * If `true`, the `input` element is required.
   * The prop defaults to the value (`false`) inherited from the parent FormControl component.
   */
  required: r.bool,
  /**
   * Number of rows to display when multiline option is set to true.
   */
  rows: r.oneOfType([r.number, r.string]),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `components` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slots: r.shape({
    input: r.elementType,
    root: r.elementType
  }),
  /**
   * Start `InputAdornment` for this component.
   */
  startAdornment: r.node,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Type of the `input` element. It should be [a valid HTML5 input type](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Form_%3Cinput%3E_types).
   * @default 'text'
   */
  type: r.string,
  /**
   * The value of the `input` element, required for a controlled component.
   */
  value: r.any
});
ba.muiName = "Input";
function qC(e) {
  return Pe("MuiSelect", e);
}
const Oo = Ce("MuiSelect", ["root", "select", "multiple", "filled", "outlined", "standard", "disabled", "focused", "icon", "iconOpen", "iconFilled", "iconOutlined", "iconStandard", "nativeInput", "error"]);
var vd;
const YC = ["aria-describedby", "aria-label", "autoFocus", "autoWidth", "children", "className", "defaultOpen", "defaultValue", "disabled", "displayEmpty", "error", "IconComponent", "inputRef", "labelId", "MenuProps", "multiple", "name", "onBlur", "onChange", "onClose", "onFocus", "onOpen", "open", "readOnly", "renderValue", "SelectDisplayProps", "tabIndex", "type", "value", "variant"], KC = Z("div", {
  name: "MuiSelect",
  slot: "Select",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [
      // Win specificity over the input base
      {
        [`&.${Oo.select}`]: t.select
      },
      {
        [`&.${Oo.select}`]: t[n.variant]
      },
      {
        [`&.${Oo.error}`]: t.error
      },
      {
        [`&.${Oo.multiple}`]: t.multiple
      }
    ];
  }
})(bm, {
  // Win specificity over the input base
  [`&.${Oo.select}`]: {
    height: "auto",
    // Resets for multiple select with chips
    minHeight: "1.4375em",
    // Required for select\text-field height consistency
    textOverflow: "ellipsis",
    whiteSpace: "nowrap",
    overflow: "hidden"
  }
}), GC = Z("svg", {
  name: "MuiSelect",
  slot: "Icon",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.icon, n.variant && t[`icon${de(n.variant)}`], n.open && t.iconOpen];
  }
})(gm), XC = Z("input", {
  shouldForwardProp: (e) => Of(e) && e !== "classes",
  name: "MuiSelect",
  slot: "NativeInput",
  overridesResolver: (e, t) => t.nativeInput
})({
  bottom: 0,
  left: 0,
  position: "absolute",
  opacity: 0,
  pointerEvents: "none",
  width: "100%",
  boxSizing: "border-box"
});
function xd(e, t) {
  return typeof t == "object" && t !== null ? e === t : String(e) === String(t);
}
function ZC(e) {
  return e == null || typeof e == "string" && !e.trim();
}
const JC = (e) => {
  const {
    classes: t,
    variant: n,
    disabled: o,
    multiple: a,
    open: s,
    error: i
  } = e, l = {
    select: ["select", n, o && "disabled", a && "multiple", i && "error"],
    icon: ["icon", `icon${de(n)}`, s && "iconOpen", o && "disabled"],
    nativeInput: ["nativeInput"]
  };
  return Se(l, qC, t);
}, xm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o;
  const {
    "aria-describedby": a,
    "aria-label": s,
    autoFocus: i,
    autoWidth: l,
    children: c,
    className: u,
    defaultOpen: d,
    defaultValue: f,
    disabled: p,
    displayEmpty: m,
    error: v = !1,
    IconComponent: h,
    inputRef: y,
    labelId: w,
    MenuProps: C = {},
    multiple: E,
    name: O,
    onBlur: T,
    onChange: P,
    onClose: S,
    onFocus: j,
    onOpen: $,
    open: V,
    readOnly: _,
    renderValue: L,
    SelectDisplayProps: M = {},
    tabIndex: R,
    value: D,
    variant: F = "standard"
  } = t, z = ie(t, YC), [N, q] = Ht({
    controlled: D,
    default: f,
    name: "Select"
  }), [A, H] = Ht({
    controlled: V,
    default: d,
    name: "Select"
  }), te = g.useRef(null), re = g.useRef(null), [B, G] = g.useState(null), {
    current: ee
  } = g.useRef(V != null), [W, J] = g.useState(), se = Ke(n, y), le = g.useCallback((Te) => {
    re.current = Te, Te && G(Te);
  }, []), X = B == null ? void 0 : B.parentNode;
  g.useImperativeHandle(se, () => ({
    focus: () => {
      re.current.focus();
    },
    node: te.current,
    value: N
  }), [N]), g.useEffect(() => {
    d && A && B && !ee && (J(l ? null : X.clientWidth), re.current.focus());
  }, [B, l]), g.useEffect(() => {
    i && re.current.focus();
  }, [i]), g.useEffect(() => {
    if (!w)
      return;
    const Te = dt(re.current).getElementById(w);
    if (Te) {
      const $e = () => {
        getSelection().isCollapsed && re.current.focus();
      };
      return Te.addEventListener("click", $e), () => {
        Te.removeEventListener("click", $e);
      };
    }
  }, [w]);
  const U = (Te, $e) => {
    Te ? $ && $($e) : S && S($e), ee || (J(l ? null : X.clientWidth), H(Te));
  }, K = (Te) => {
    Te.button === 0 && (Te.preventDefault(), re.current.focus(), U(!0, Te));
  }, Y = (Te) => {
    U(!1, Te);
  }, he = g.Children.toArray(c), Oe = (Te) => {
    const $e = he.find((Ge) => Ge.props.value === Te.target.value);
    $e !== void 0 && (q($e.props.value), P && P(Te, $e));
  }, Ne = (Te) => ($e) => {
    let Ge;
    if ($e.currentTarget.hasAttribute("tabindex")) {
      if (E) {
        Ge = Array.isArray(N) ? N.slice() : [];
        const xt = N.indexOf(Te.props.value);
        xt === -1 ? Ge.push(Te.props.value) : Ge.splice(xt, 1);
      } else
        Ge = Te.props.value;
      if (Te.props.onClick && Te.props.onClick($e), N !== Ge && (q(Ge), P)) {
        const xt = $e.nativeEvent || $e, Qt = new xt.constructor(xt.type, xt);
        Object.defineProperty(Qt, "target", {
          writable: !0,
          value: {
            value: Ge,
            name: O
          }
        }), P(Qt, Te);
      }
      E || U(!1, $e);
    }
  }, fe = (Te) => {
    _ || [
      " ",
      "ArrowUp",
      "ArrowDown",
      // The native select doesn't respond to enter on macOS, but it's recommended by
      // https://www.w3.org/WAI/ARIA/apg/patterns/combobox/examples/combobox-select-only/
      "Enter"
    ].indexOf(Te.key) !== -1 && (Te.preventDefault(), U(!0, Te));
  }, ve = B !== null && A, oe = (Te) => {
    !ve && T && (Object.defineProperty(Te, "target", {
      writable: !0,
      value: {
        value: N,
        name: O
      }
    }), T(Te));
  };
  delete z["aria-invalid"];
  let ce, I;
  const Q = [];
  let ne = !1, ue = !1;
  (rs({
    value: N
  }) || m) && (L ? ce = L(N) : ne = !0);
  const ge = he.map((Te) => {
    if (!/* @__PURE__ */ g.isValidElement(Te))
      return null;
    process.env.NODE_ENV !== "production" && zo.isFragment(Te) && console.error(["MUI: The Select component doesn't accept a Fragment as a child.", "Consider providing an array instead."].join(`
`));
    let $e;
    if (E) {
      if (!Array.isArray(N))
        throw new Error(process.env.NODE_ENV !== "production" ? "MUI: The `value` prop must be an array when using the `Select` component with `multiple`." : xn(2));
      $e = N.some((Ge) => xd(Ge, Te.props.value)), $e && ne && Q.push(Te.props.children);
    } else
      $e = xd(N, Te.props.value), $e && ne && (I = Te.props.children);
    return $e && (ue = !0), /* @__PURE__ */ g.cloneElement(Te, {
      "aria-selected": $e ? "true" : "false",
      onClick: Ne(Te),
      onKeyUp: (Ge) => {
        Ge.key === " " && Ge.preventDefault(), Te.props.onKeyUp && Te.props.onKeyUp(Ge);
      },
      role: "option",
      selected: $e,
      value: void 0,
      // The value is most likely not a valid HTML attribute.
      "data-value": Te.props.value
      // Instead, we provide it as a data attribute.
    });
  });
  process.env.NODE_ENV !== "production" && g.useEffect(() => {
    if (!ue && !E && N !== "") {
      const Te = he.map(($e) => $e.props.value);
      console.warn([`MUI: You have provided an out-of-range value \`${N}\` for the select ${O ? `(name="${O}") ` : ""}component.`, "Consider providing a value that matches one of the available options or ''.", `The available values are ${Te.filter(($e) => $e != null).map(($e) => `\`${$e}\``).join(", ") || '""'}.`].join(`
`));
    }
  }, [ue, he, E, O, N]), ne && (E ? Q.length === 0 ? ce = null : ce = Q.reduce((Te, $e, Ge) => (Te.push($e), Ge < Q.length - 1 && Te.push(", "), Te), []) : ce = I);
  let ye = W;
  !l && ee && B && (ye = X.clientWidth);
  let xe;
  typeof R < "u" ? xe = R : xe = p ? null : 0;
  const be = M.id || (O ? `mui-component-select-${O}` : void 0), _e = b({}, t, {
    variant: F,
    value: N,
    open: ve,
    error: v
  }), st = JC(_e), rt = b({}, C.PaperProps, (o = C.slotProps) == null ? void 0 : o.paper), Qe = Bn();
  return /* @__PURE__ */ x.jsxs(g.Fragment, {
    children: [/* @__PURE__ */ x.jsx(KC, b({
      ref: le,
      tabIndex: xe,
      role: "combobox",
      "aria-controls": Qe,
      "aria-disabled": p ? "true" : void 0,
      "aria-expanded": ve ? "true" : "false",
      "aria-haspopup": "listbox",
      "aria-label": s,
      "aria-labelledby": [w, be].filter(Boolean).join(" ") || void 0,
      "aria-describedby": a,
      onKeyDown: fe,
      onMouseDown: p || _ ? null : K,
      onBlur: oe,
      onFocus: j
    }, M, {
      ownerState: _e,
      className: pe(M.className, st.select, u),
      id: be,
      children: ZC(ce) ? (
        // notranslate needed while Google Translate will not fix zero-width space issue
        vd || (vd = /* @__PURE__ */ x.jsx("span", {
          className: "notranslate",
          children: "​"
        }))
      ) : ce
    })), /* @__PURE__ */ x.jsx(XC, b({
      "aria-invalid": v,
      value: Array.isArray(N) ? N.join(",") : N,
      name: O,
      ref: te,
      "aria-hidden": !0,
      onChange: Oe,
      tabIndex: -1,
      disabled: p,
      className: st.nativeInput,
      autoFocus: i,
      ownerState: _e
    }, z)), /* @__PURE__ */ x.jsx(GC, {
      as: h,
      className: st.icon,
      ownerState: _e
    }), /* @__PURE__ */ x.jsx(hm, b({
      id: `menu-${O || ""}`,
      anchorEl: X,
      open: ve,
      onClose: Y,
      anchorOrigin: {
        vertical: "bottom",
        horizontal: "center"
      },
      transformOrigin: {
        vertical: "top",
        horizontal: "center"
      }
    }, C, {
      MenuListProps: b({
        "aria-labelledby": w,
        role: "listbox",
        "aria-multiselectable": E ? "true" : void 0,
        disableListWrap: !0,
        id: Qe
      }, C.MenuListProps),
      slotProps: b({}, C.slotProps, {
        paper: b({}, rt, {
          style: b({
            minWidth: ye
          }, rt != null ? rt.style : null)
        })
      }),
      children: ge
    }))]
  });
});
process.env.NODE_ENV !== "production" && (xm.propTypes = {
  /**
   * @ignore
   */
  "aria-describedby": r.string,
  /**
   * @ignore
   */
  "aria-label": r.string,
  /**
   * @ignore
   */
  autoFocus: r.bool,
  /**
   * If `true`, the width of the popover will automatically be set according to the items inside the
   * menu, otherwise it will be at least the width of the select input.
   */
  autoWidth: r.bool,
  /**
   * The option elements to populate the select with.
   * Can be some `<MenuItem>` elements.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * The CSS class name of the select element.
   */
  className: r.string,
  /**
   * If `true`, the component is toggled on mount. Use when the component open state is not controlled.
   * You can only use it when the `native` prop is `false` (default).
   */
  defaultOpen: r.bool,
  /**
   * The default value. Use when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the select is disabled.
   */
  disabled: r.bool,
  /**
   * If `true`, the selected item is displayed even if its value is empty.
   */
  displayEmpty: r.bool,
  /**
   * If `true`, the `select input` will indicate an error.
   */
  error: r.bool,
  /**
   * The icon that displays the arrow.
   */
  IconComponent: r.elementType.isRequired,
  /**
   * Imperative handle implementing `{ value: T, node: HTMLElement, focus(): void }`
   * Equivalent to `ref`
   */
  inputRef: vt,
  /**
   * The ID of an element that acts as an additional label. The Select will
   * be labelled by the additional label and the selected value.
   */
  labelId: r.string,
  /**
   * Props applied to the [`Menu`](/material-ui/api/menu/) element.
   */
  MenuProps: r.object,
  /**
   * If `true`, `value` must be an array and the menu will support multiple selections.
   */
  multiple: r.bool,
  /**
   * Name attribute of the `select` or hidden `input` element.
   */
  name: r.string,
  /**
   * @ignore
   */
  onBlur: r.func,
  /**
   * Callback fired when a menu item is selected.
   *
   * @param {object} event The event source of the callback.
   * You can pull out the new value by accessing `event.target.value` (any).
   * @param {object} [child] The react element that was selected.
   */
  onChange: r.func,
  /**
   * Callback fired when the component requests to be closed.
   * Use in controlled mode (see open).
   *
   * @param {object} event The event source of the callback.
   */
  onClose: r.func,
  /**
   * @ignore
   */
  onFocus: r.func,
  /**
   * Callback fired when the component requests to be opened.
   * Use in controlled mode (see open).
   *
   * @param {object} event The event source of the callback.
   */
  onOpen: r.func,
  /**
   * If `true`, the component is shown.
   */
  open: r.bool,
  /**
   * @ignore
   */
  readOnly: r.bool,
  /**
   * Render the selected value.
   *
   * @param {any} value The `value` provided to the component.
   * @returns {ReactNode}
   */
  renderValue: r.func,
  /**
   * Props applied to the clickable div element.
   */
  SelectDisplayProps: r.object,
  /**
   * @ignore
   */
  tabIndex: r.oneOfType([r.number, r.string]),
  /**
   * @ignore
   */
  type: r.any,
  /**
   * The input value.
   */
  value: r.any,
  /**
   * The variant to use.
   */
  variant: r.oneOf(["standard", "outlined", "filled"])
});
const QC = ["autoWidth", "children", "classes", "className", "defaultOpen", "displayEmpty", "IconComponent", "id", "input", "inputProps", "label", "labelId", "MenuProps", "multiple", "native", "onClose", "onOpen", "open", "renderValue", "SelectDisplayProps", "variant"], eO = ["root"], tO = (e) => {
  const {
    classes: t
  } = e;
  return t;
}, lc = {
  name: "MuiSelect",
  overridesResolver: (e, t) => t.root,
  shouldForwardProp: (e) => nn(e) && e !== "variant",
  slot: "Root"
}, nO = Z(Us, lc)(""), rO = Z(ba, lc)(""), oO = Z(Ws, lc)(""), cc = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    name: "MuiSelect",
    props: t
  }), {
    autoWidth: a = !1,
    children: s,
    classes: i = {},
    className: l,
    defaultOpen: c = !1,
    displayEmpty: u = !1,
    IconComponent: d = Cw,
    id: f,
    input: p,
    inputProps: m,
    label: v,
    labelId: h,
    MenuProps: y,
    multiple: w = !1,
    native: C = !1,
    onClose: E,
    onOpen: O,
    open: T,
    renderValue: P,
    SelectDisplayProps: S,
    variant: j = "outlined"
  } = o, $ = ie(o, QC), V = C ? ym : xm, _ = fr(), L = fo({
    props: o,
    muiFormControl: _,
    states: ["variant", "error"]
  }), M = L.variant || j, R = b({}, o, {
    variant: M,
    classes: i
  }), D = tO(R), F = ie(D, eO), z = p || {
    standard: /* @__PURE__ */ x.jsx(nO, {
      ownerState: R
    }),
    outlined: /* @__PURE__ */ x.jsx(rO, {
      label: v,
      ownerState: R
    }),
    filled: /* @__PURE__ */ x.jsx(oO, {
      ownerState: R
    })
  }[M], N = Ke(n, z.ref);
  return /* @__PURE__ */ x.jsx(g.Fragment, {
    children: /* @__PURE__ */ g.cloneElement(z, b({
      // Most of the logic is implemented in `SelectInput`.
      // The `Select` component is a simple API wrapper to expose something better to play with.
      inputComponent: V,
      inputProps: b({
        children: s,
        error: L.error,
        IconComponent: d,
        variant: M,
        type: void 0,
        // We render a select. We can ignore the type provided by the `Input`.
        multiple: w
      }, C ? {
        id: f
      } : {
        autoWidth: a,
        defaultOpen: c,
        displayEmpty: u,
        labelId: h,
        MenuProps: y,
        onClose: E,
        onOpen: O,
        open: T,
        renderValue: P,
        SelectDisplayProps: b({
          id: f
        }, S)
      }, m, {
        classes: m ? kt(F, m.classes) : F
      }, p ? p.props.inputProps : {})
    }, (w && C || u) && M === "outlined" ? {
      notched: !0
    } : {}, {
      ref: N,
      className: pe(z.props.className, l, D.root)
    }, !p && {
      variant: M
    }, $))
  });
});
process.env.NODE_ENV !== "production" && (cc.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * If `true`, the width of the popover will automatically be set according to the items inside the
   * menu, otherwise it will be at least the width of the select input.
   * @default false
   */
  autoWidth: r.bool,
  /**
   * The option elements to populate the select with.
   * Can be some `MenuItem` when `native` is false and `option` when `native` is true.
   *
   * ⚠️The `MenuItem` elements **must** be direct descendants when `native` is false.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   * @default {}
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * If `true`, the component is initially open. Use when the component open state is not controlled (i.e. the `open` prop is not defined).
   * You can only use it when the `native` prop is `false` (default).
   * @default false
   */
  defaultOpen: r.bool,
  /**
   * The default value. Use when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, a value is displayed even if no items are selected.
   *
   * In order to display a meaningful value, a function can be passed to the `renderValue` prop which
   * returns the value to be displayed when no items are selected.
   *
   * ⚠️ When using this prop, make sure the label doesn't overlap with the empty displayed value.
   * The label should either be hidden or forced to a shrunk state.
   * @default false
   */
  displayEmpty: r.bool,
  /**
   * The icon that displays the arrow.
   * @default ArrowDropDownIcon
   */
  IconComponent: r.elementType,
  /**
   * The `id` of the wrapper element or the `select` element when `native`.
   */
  id: r.string,
  /**
   * An `Input` element; does not have to be a material-ui specific `Input`.
   */
  input: r.element,
  /**
   * [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Attributes) applied to the `input` element.
   * When `native` is `true`, the attributes are applied on the `select` element.
   */
  inputProps: r.object,
  /**
   * See [OutlinedInput#label](/material-ui/api/outlined-input/#props)
   */
  label: r.node,
  /**
   * The ID of an element that acts as an additional label. The Select will
   * be labelled by the additional label and the selected value.
   */
  labelId: r.string,
  /**
   * Props applied to the [`Menu`](/material-ui/api/menu/) element.
   */
  MenuProps: r.object,
  /**
   * If `true`, `value` must be an array and the menu will support multiple selections.
   * @default false
   */
  multiple: r.bool,
  /**
   * If `true`, the component uses a native `select` element.
   * @default false
   */
  native: r.bool,
  /**
   * Callback fired when a menu item is selected.
   *
   * @param {SelectChangeEvent<Value>} event The event source of the callback.
   * You can pull out the new value by accessing `event.target.value` (any).
   * **Warning**: This is a generic event, not a change event, unless the change event is caused by browser autofill.
   * @param {object} [child] The react element that was selected when `native` is `false` (default).
   */
  onChange: r.func,
  /**
   * Callback fired when the component requests to be closed.
   * Use it in either controlled (see the `open` prop), or uncontrolled mode (to detect when the Select collapses).
   *
   * @param {object} event The event source of the callback.
   */
  onClose: r.func,
  /**
   * Callback fired when the component requests to be opened.
   * Use it in either controlled (see the `open` prop), or uncontrolled mode (to detect when the Select expands).
   *
   * @param {object} event The event source of the callback.
   */
  onOpen: r.func,
  /**
   * If `true`, the component is shown.
   * You can only use it when the `native` prop is `false` (default).
   */
  open: r.bool,
  /**
   * Render the selected value.
   * You can only use it when the `native` prop is `false` (default).
   *
   * @param {any} value The `value` provided to the component.
   * @returns {ReactNode}
   */
  renderValue: r.func,
  /**
   * Props applied to the clickable div element.
   */
  SelectDisplayProps: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * The `input` value. Providing an empty string will select no options.
   * Set to an empty string `''` if you don't want any of the available options to be selected.
   *
   * If the value is an object it must have reference equality with the option in order to be selected.
   * If the value is not an object, the string representation must match with the string representation of the option in order to be selected.
   */
  value: r.oneOfType([r.oneOf([""]), r.any]),
  /**
   * The variant to use.
   * @default 'outlined'
   */
  variant: r.oneOf(["filled", "outlined", "standard"])
});
cc.muiName = "Select";
function aO(e) {
  return Pe("MuiTooltip", e);
}
const In = Ce("MuiTooltip", ["popper", "popperInteractive", "popperArrow", "popperClose", "tooltip", "tooltipArrow", "touch", "tooltipPlacementLeft", "tooltipPlacementRight", "tooltipPlacementTop", "tooltipPlacementBottom", "arrow"]), sO = ["arrow", "children", "classes", "components", "componentsProps", "describeChild", "disableFocusListener", "disableHoverListener", "disableInteractive", "disableTouchListener", "enterDelay", "enterNextDelay", "enterTouchDelay", "followCursor", "id", "leaveDelay", "leaveTouchDelay", "onClose", "onOpen", "open", "placement", "PopperComponent", "PopperProps", "slotProps", "slots", "title", "TransitionComponent", "TransitionProps"];
function iO(e) {
  return Math.round(e * 1e5) / 1e5;
}
const lO = (e) => {
  const {
    classes: t,
    disableInteractive: n,
    arrow: o,
    touch: a,
    placement: s
  } = e, i = {
    popper: ["popper", !n && "popperInteractive", o && "popperArrow"],
    tooltip: ["tooltip", o && "tooltipArrow", a && "touch", `tooltipPlacement${de(s.split("-")[0])}`],
    arrow: ["arrow"]
  };
  return Se(i, aO, t);
}, cO = Z(js, {
  name: "MuiTooltip",
  slot: "Popper",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.popper, !n.disableInteractive && t.popperInteractive, n.arrow && t.popperArrow, !n.open && t.popperClose];
  }
})(({
  theme: e,
  ownerState: t,
  open: n
}) => b({
  zIndex: (e.vars || e).zIndex.tooltip,
  pointerEvents: "none"
}, !t.disableInteractive && {
  pointerEvents: "auto"
}, !n && {
  pointerEvents: "none"
}, t.arrow && {
  [`&[data-popper-placement*="bottom"] .${In.arrow}`]: {
    top: 0,
    marginTop: "-0.71em",
    "&::before": {
      transformOrigin: "0 100%"
    }
  },
  [`&[data-popper-placement*="top"] .${In.arrow}`]: {
    bottom: 0,
    marginBottom: "-0.71em",
    "&::before": {
      transformOrigin: "100% 0"
    }
  },
  [`&[data-popper-placement*="right"] .${In.arrow}`]: b({}, t.isRtl ? {
    right: 0,
    marginRight: "-0.71em"
  } : {
    left: 0,
    marginLeft: "-0.71em"
  }, {
    height: "1em",
    width: "0.71em",
    "&::before": {
      transformOrigin: "100% 100%"
    }
  }),
  [`&[data-popper-placement*="left"] .${In.arrow}`]: b({}, t.isRtl ? {
    left: 0,
    marginLeft: "-0.71em"
  } : {
    right: 0,
    marginRight: "-0.71em"
  }, {
    height: "1em",
    width: "0.71em",
    "&::before": {
      transformOrigin: "0 0"
    }
  })
})), uO = Z("div", {
  name: "MuiTooltip",
  slot: "Tooltip",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.tooltip, n.touch && t.touch, n.arrow && t.tooltipArrow, t[`tooltipPlacement${de(n.placement.split("-")[0])}`]];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  backgroundColor: e.vars ? e.vars.palette.Tooltip.bg : qe(e.palette.grey[700], 0.92),
  borderRadius: (e.vars || e).shape.borderRadius,
  color: (e.vars || e).palette.common.white,
  fontFamily: e.typography.fontFamily,
  padding: "4px 8px",
  fontSize: e.typography.pxToRem(11),
  maxWidth: 300,
  margin: 2,
  wordWrap: "break-word",
  fontWeight: e.typography.fontWeightMedium
}, t.arrow && {
  position: "relative",
  margin: 0
}, t.touch && {
  padding: "8px 16px",
  fontSize: e.typography.pxToRem(14),
  lineHeight: `${iO(16 / 14)}em`,
  fontWeight: e.typography.fontWeightRegular
}, {
  [`.${In.popper}[data-popper-placement*="left"] &`]: b({
    transformOrigin: "right center"
  }, t.isRtl ? b({
    marginLeft: "14px"
  }, t.touch && {
    marginLeft: "24px"
  }) : b({
    marginRight: "14px"
  }, t.touch && {
    marginRight: "24px"
  })),
  [`.${In.popper}[data-popper-placement*="right"] &`]: b({
    transformOrigin: "left center"
  }, t.isRtl ? b({
    marginRight: "14px"
  }, t.touch && {
    marginRight: "24px"
  }) : b({
    marginLeft: "14px"
  }, t.touch && {
    marginLeft: "24px"
  })),
  [`.${In.popper}[data-popper-placement*="top"] &`]: b({
    transformOrigin: "center bottom",
    marginBottom: "14px"
  }, t.touch && {
    marginBottom: "24px"
  }),
  [`.${In.popper}[data-popper-placement*="bottom"] &`]: b({
    transformOrigin: "center top",
    marginTop: "14px"
  }, t.touch && {
    marginTop: "24px"
  })
})), dO = Z("span", {
  name: "MuiTooltip",
  slot: "Arrow",
  overridesResolver: (e, t) => t.arrow
})(({
  theme: e
}) => ({
  overflow: "hidden",
  position: "absolute",
  width: "1em",
  height: "0.71em",
  boxSizing: "border-box",
  color: e.vars ? e.vars.palette.Tooltip.bg : qe(e.palette.grey[700], 0.9),
  "&::before": {
    content: '""',
    margin: "auto",
    display: "block",
    width: "100%",
    height: "100%",
    backgroundColor: "currentColor",
    transform: "rotate(45deg)"
  }
}));
let Da = !1;
const Td = new ra();
let So = {
  x: 0,
  y: 0
};
function $a(e, t) {
  return (n, ...o) => {
    t && t(n, ...o), e(n, ...o);
  };
}
const Nn = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s, i, l, c, u, d, f, p, m, v, h, y, w, C, E, O, T;
  const P = Ee({
    props: t,
    name: "MuiTooltip"
  }), {
    arrow: S = !1,
    children: j,
    components: $ = {},
    componentsProps: V = {},
    describeChild: _ = !1,
    disableFocusListener: L = !1,
    disableHoverListener: M = !1,
    disableInteractive: R = !1,
    disableTouchListener: D = !1,
    enterDelay: F = 100,
    enterNextDelay: z = 0,
    enterTouchDelay: N = 700,
    followCursor: q = !1,
    id: A,
    leaveDelay: H = 0,
    leaveTouchDelay: te = 1500,
    onClose: re,
    onOpen: B,
    open: G,
    placement: ee = "bottom",
    PopperComponent: W,
    PopperProps: J = {},
    slotProps: se = {},
    slots: le = {},
    title: X,
    TransitionComponent: U = ro,
    TransitionProps: K
  } = P, Y = ie(P, sO), he = /* @__PURE__ */ g.isValidElement(j) ? j : /* @__PURE__ */ x.jsx("span", {
    children: j
  }), Oe = Zt(), Ne = kf(), [fe, ve] = g.useState(), [oe, ce] = g.useState(null), I = g.useRef(!1), Q = R || q, ne = jr(), ue = jr(), ge = jr(), ye = jr(), [xe, be] = Ht({
    controlled: G,
    default: !1,
    name: "Tooltip",
    state: "open"
  });
  let _e = xe;
  if (process.env.NODE_ENV !== "production") {
    const {
      current: Re
    } = g.useRef(G !== void 0);
    g.useEffect(() => {
      fe && fe.disabled && !Re && X !== "" && fe.tagName.toLowerCase() === "button" && console.error(["MUI: You are providing a disabled `button` child to the Tooltip component.", "A disabled element does not fire events.", "Tooltip needs to listen to the child element's events to display the title.", "", "Add a simple wrapper element, such as a `span`."].join(`
`));
    }, [X, fe, Re]);
  }
  const st = Bn(A), rt = g.useRef(), Qe = we(() => {
    rt.current !== void 0 && (document.body.style.WebkitUserSelect = rt.current, rt.current = void 0), ye.clear();
  });
  g.useEffect(() => Qe, [Qe]);
  const Te = (Re) => {
    Td.clear(), Da = !0, be(!0), B && !_e && B(Re);
  }, $e = we(
    /**
     * @param {React.SyntheticEvent | Event} event
     */
    (Re) => {
      Td.start(800 + H, () => {
        Da = !1;
      }), be(!1), re && _e && re(Re), ne.start(Oe.transitions.duration.shortest, () => {
        I.current = !1;
      });
    }
  ), Ge = (Re) => {
    I.current && Re.type !== "touchstart" || (fe && fe.removeAttribute("title"), ue.clear(), ge.clear(), F || Da && z ? ue.start(Da ? z : F, () => {
      Te(Re);
    }) : Te(Re));
  }, xt = (Re) => {
    ue.clear(), ge.start(H, () => {
      $e(Re);
    });
  }, {
    isFocusVisibleRef: Qt,
    onBlur: Rn,
    onFocus: Dn,
    ref: It
  } = vl(), [, on] = g.useState(!1), $n = (Re) => {
    Rn(Re), Qt.current === !1 && (on(!1), xt(Re));
  }, Ue = (Re) => {
    fe || ve(Re.currentTarget), Dn(Re), Qt.current === !0 && (on(!0), Ge(Re));
  }, gt = (Re) => {
    I.current = !0;
    const Ie = he.props;
    Ie.onTouchStart && Ie.onTouchStart(Re);
  }, an = (Re) => {
    gt(Re), ge.clear(), ne.clear(), Qe(), rt.current = document.body.style.WebkitUserSelect, document.body.style.WebkitUserSelect = "none", ye.start(N, () => {
      document.body.style.WebkitUserSelect = rt.current, Ge(Re);
    });
  }, Nt = (Re) => {
    he.props.onTouchEnd && he.props.onTouchEnd(Re), Qe(), ge.start(te, () => {
      $e(Re);
    });
  };
  g.useEffect(() => {
    if (!_e)
      return;
    function Re(Ie) {
      (Ie.key === "Escape" || Ie.key === "Esc") && $e(Ie);
    }
    return document.addEventListener("keydown", Re), () => {
      document.removeEventListener("keydown", Re);
    };
  }, [$e, _e]);
  const wa = Ke(he.ref, It, ve, n);
  !X && X !== 0 && (_e = !1);
  const Er = g.useRef(), oi = (Re) => {
    const Ie = he.props;
    Ie.onMouseMove && Ie.onMouseMove(Re), So = {
      x: Re.clientX,
      y: Re.clientY
    }, Er.current && Er.current.update();
  }, qn = {}, Cr = typeof X == "string";
  _ ? (qn.title = !_e && Cr && !M ? X : null, qn["aria-describedby"] = _e ? st : null) : (qn["aria-label"] = Cr ? X : null, qn["aria-labelledby"] = _e && !Cr ? st : null);
  const yt = b({}, qn, Y, he.props, {
    className: pe(Y.className, he.props.className),
    onTouchStart: gt,
    ref: wa
  }, q ? {
    onMouseMove: oi
  } : {});
  process.env.NODE_ENV !== "production" && (yt["data-mui-internal-clone-element"] = !0, g.useEffect(() => {
    fe && !fe.getAttribute("data-mui-internal-clone-element") && console.error(["MUI: The `children` component of the Tooltip is not forwarding its props correctly.", "Please make sure that props are spread on the same element that the ref is applied to."].join(`
`));
  }, [fe]));
  const Yn = {};
  D || (yt.onTouchStart = an, yt.onTouchEnd = Nt), M || (yt.onMouseOver = $a(Ge, yt.onMouseOver), yt.onMouseLeave = $a(xt, yt.onMouseLeave), Q || (Yn.onMouseOver = Ge, Yn.onMouseLeave = xt)), L || (yt.onFocus = $a(Ue, yt.onFocus), yt.onBlur = $a($n, yt.onBlur), Q || (Yn.onFocus = Ue, Yn.onBlur = $n)), process.env.NODE_ENV !== "production" && he.props.title && console.error(["MUI: You have provided a `title` prop to the child of <Tooltip />.", `Remove this title prop \`${he.props.title}\` or the Tooltip component.`].join(`
`));
  const ai = g.useMemo(() => {
    var Re;
    let Ie = [{
      name: "arrow",
      enabled: !!oe,
      options: {
        element: oe,
        padding: 4
      }
    }];
    return (Re = J.popperOptions) != null && Re.modifiers && (Ie = Ie.concat(J.popperOptions.modifiers)), b({}, J.popperOptions, {
      modifiers: Ie
    });
  }, [oe, J]), Kn = b({}, P, {
    isRtl: Ne,
    arrow: S,
    disableInteractive: Q,
    placement: ee,
    PopperComponentProp: W,
    touch: I.current
  }), bo = lO(Kn), k = (o = (a = le.popper) != null ? a : $.Popper) != null ? o : cO, ae = (s = (i = (l = le.transition) != null ? l : $.Transition) != null ? i : U) != null ? s : ro, me = (c = (u = le.tooltip) != null ? u : $.Tooltip) != null ? c : uO, De = (d = (f = le.arrow) != null ? f : $.Arrow) != null ? d : dO, je = _o(k, b({}, J, (p = se.popper) != null ? p : V.popper, {
    className: pe(bo.popper, J == null ? void 0 : J.className, (m = (v = se.popper) != null ? v : V.popper) == null ? void 0 : m.className)
  }), Kn), He = _o(ae, b({}, K, (h = se.transition) != null ? h : V.transition), Kn), Me = _o(me, b({}, (y = se.tooltip) != null ? y : V.tooltip, {
    className: pe(bo.tooltip, (w = (C = se.tooltip) != null ? C : V.tooltip) == null ? void 0 : w.className)
  }), Kn), ke = _o(De, b({}, (E = se.arrow) != null ? E : V.arrow, {
    className: pe(bo.arrow, (O = (T = se.arrow) != null ? T : V.arrow) == null ? void 0 : O.className)
  }), Kn);
  return /* @__PURE__ */ x.jsxs(g.Fragment, {
    children: [/* @__PURE__ */ g.cloneElement(he, yt), /* @__PURE__ */ x.jsx(k, b({
      as: W ?? js,
      placement: ee,
      anchorEl: q ? {
        getBoundingClientRect: () => ({
          top: So.y,
          left: So.x,
          right: So.x,
          bottom: So.y,
          width: 0,
          height: 0
        })
      } : fe,
      popperRef: Er,
      open: fe ? _e : !1,
      id: st,
      transition: !0
    }, Yn, je, {
      popperOptions: ai,
      children: ({
        TransitionProps: Re
      }) => /* @__PURE__ */ x.jsx(ae, b({
        timeout: Oe.transitions.duration.shorter
      }, Re, He, {
        children: /* @__PURE__ */ x.jsxs(me, b({}, Me, {
          children: [X, S ? /* @__PURE__ */ x.jsx(De, b({}, ke, {
            ref: ce
          })) : null]
        }))
      }))
    }))]
  });
});
process.env.NODE_ENV !== "production" && (Nn.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * If `true`, adds an arrow to the tooltip.
   * @default false
   */
  arrow: r.bool,
  /**
   * Tooltip reference element.
   */
  children: ao.isRequired,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `slots` prop.
   * It's recommended to use the `slots` prop instead.
   *
   * @default {}
   */
  components: r.shape({
    Arrow: r.elementType,
    Popper: r.elementType,
    Tooltip: r.elementType,
    Transition: r.elementType
  }),
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `slotProps` prop.
   * It's recommended to use the `slotProps` prop instead, as `componentsProps` will be deprecated in the future.
   *
   * @default {}
   */
  componentsProps: r.shape({
    arrow: r.object,
    popper: r.object,
    tooltip: r.object,
    transition: r.object
  }),
  /**
   * Set to `true` if the `title` acts as an accessible description.
   * By default the `title` acts as an accessible label for the child.
   * @default false
   */
  describeChild: r.bool,
  /**
   * Do not respond to focus-visible events.
   * @default false
   */
  disableFocusListener: r.bool,
  /**
   * Do not respond to hover events.
   * @default false
   */
  disableHoverListener: r.bool,
  /**
   * Makes a tooltip not interactive, i.e. it will close when the user
   * hovers over the tooltip before the `leaveDelay` is expired.
   * @default false
   */
  disableInteractive: r.bool,
  /**
   * Do not respond to long press touch events.
   * @default false
   */
  disableTouchListener: r.bool,
  /**
   * The number of milliseconds to wait before showing the tooltip.
   * This prop won't impact the enter touch delay (`enterTouchDelay`).
   * @default 100
   */
  enterDelay: r.number,
  /**
   * The number of milliseconds to wait before showing the tooltip when one was already recently opened.
   * @default 0
   */
  enterNextDelay: r.number,
  /**
   * The number of milliseconds a user must touch the element before showing the tooltip.
   * @default 700
   */
  enterTouchDelay: r.number,
  /**
   * If `true`, the tooltip follow the cursor over the wrapped element.
   * @default false
   */
  followCursor: r.bool,
  /**
   * This prop is used to help implement the accessibility logic.
   * If you don't provide this prop. It falls back to a randomly generated id.
   */
  id: r.string,
  /**
   * The number of milliseconds to wait before hiding the tooltip.
   * This prop won't impact the leave touch delay (`leaveTouchDelay`).
   * @default 0
   */
  leaveDelay: r.number,
  /**
   * The number of milliseconds after the user stops touching an element before hiding the tooltip.
   * @default 1500
   */
  leaveTouchDelay: r.number,
  /**
   * Callback fired when the component requests to be closed.
   *
   * @param {React.SyntheticEvent} event The event source of the callback.
   */
  onClose: r.func,
  /**
   * Callback fired when the component requests to be open.
   *
   * @param {React.SyntheticEvent} event The event source of the callback.
   */
  onOpen: r.func,
  /**
   * If `true`, the component is shown.
   */
  open: r.bool,
  /**
   * Tooltip placement.
   * @default 'bottom'
   */
  placement: r.oneOf(["bottom-end", "bottom-start", "bottom", "left-end", "left-start", "left", "right-end", "right-start", "right", "top-end", "top-start", "top"]),
  /**
   * The component used for the popper.
   * @default Popper
   */
  PopperComponent: r.elementType,
  /**
   * Props applied to the [`Popper`](/material-ui/api/popper/) element.
   * @default {}
   */
  PopperProps: r.object,
  /**
   * The extra props for the slot components.
   * You can override the existing props or add new ones.
   *
   * This prop is an alias for the `componentsProps` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slotProps: r.shape({
    arrow: r.object,
    popper: r.object,
    tooltip: r.object,
    transition: r.object
  }),
  /**
   * The components used for each slot inside.
   *
   * This prop is an alias for the `components` prop, which will be deprecated in the future.
   *
   * @default {}
   */
  slots: r.shape({
    arrow: r.elementType,
    popper: r.elementType,
    tooltip: r.elementType,
    transition: r.elementType
  }),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Tooltip title. Zero-length titles string, undefined, null and false are never displayed.
   */
  title: r.node,
  /**
   * The component used for the transition.
   * [Follow this guide](/material-ui/transitions/#transitioncomponent-prop) to learn more about the requirements for this component.
   * @default Grow
   */
  TransitionComponent: r.elementType,
  /**
   * Props applied to the transition element.
   * By default, the element is based on this [`Transition`](https://reactcommunity.org/react-transition-group/transition/) component.
   */
  TransitionProps: r.object
});
const uc = /* @__PURE__ */ g.createContext();
process.env.NODE_ENV !== "production" && (uc.displayName = "TableContext");
function pO(e) {
  return Pe("MuiTable", e);
}
Ce("MuiTable", ["root", "stickyHeader"]);
const fO = ["className", "component", "padding", "size", "stickyHeader"], mO = (e) => {
  const {
    classes: t,
    stickyHeader: n
  } = e;
  return Se({
    root: ["root", n && "stickyHeader"]
  }, pO, t);
}, hO = Z("table", {
  name: "MuiTable",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.stickyHeader && t.stickyHeader];
  }
})(({
  theme: e,
  ownerState: t
}) => b({
  display: "table",
  width: "100%",
  borderCollapse: "collapse",
  borderSpacing: 0,
  "& caption": b({}, e.typography.body2, {
    padding: e.spacing(2),
    color: (e.vars || e).palette.text.secondary,
    textAlign: "left",
    captionSide: "bottom"
  })
}, t.stickyHeader && {
  borderCollapse: "separate"
})), wd = "table", Tm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTable"
  }), {
    className: a,
    component: s = wd,
    padding: i = "normal",
    size: l = "medium",
    stickyHeader: c = !1
  } = o, u = ie(o, fO), d = b({}, o, {
    component: s,
    padding: i,
    size: l,
    stickyHeader: c
  }), f = mO(d), p = g.useMemo(() => ({
    padding: i,
    size: l,
    stickyHeader: c
  }), [i, l, c]);
  return /* @__PURE__ */ x.jsx(uc.Provider, {
    value: p,
    children: /* @__PURE__ */ x.jsx(hO, b({
      as: s,
      role: s === wd ? null : "table",
      ref: n,
      className: pe(f.root, a),
      ownerState: d
    }, u))
  });
});
process.env.NODE_ENV !== "production" && (Tm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the table, normally `TableHead` and `TableBody`.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * Allows TableCells to inherit padding of the Table.
   * @default 'normal'
   */
  padding: r.oneOf(["checkbox", "none", "normal"]),
  /**
   * Allows TableCells to inherit size of the Table.
   * @default 'medium'
   */
  size: r.oneOfType([r.oneOf(["medium", "small"]), r.string]),
  /**
   * Set the header sticky.
   *
   * ⚠️ It doesn't work with IE11.
   * @default false
   */
  stickyHeader: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
const ga = /* @__PURE__ */ g.createContext();
process.env.NODE_ENV !== "production" && (ga.displayName = "Tablelvl2Context");
function bO(e) {
  return Pe("MuiTableBody", e);
}
Ce("MuiTableBody", ["root"]);
const gO = ["className", "component"], yO = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"]
  }, bO, t);
}, vO = Z("tbody", {
  name: "MuiTableBody",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "table-row-group"
}), xO = {
  variant: "body"
}, Ed = "tbody", wm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTableBody"
  }), {
    className: a,
    component: s = Ed
  } = o, i = ie(o, gO), l = b({}, o, {
    component: s
  }), c = yO(l);
  return /* @__PURE__ */ x.jsx(ga.Provider, {
    value: xO,
    children: /* @__PURE__ */ x.jsx(vO, b({
      className: pe(c.root, a),
      as: s,
      ref: n,
      role: s === Ed ? null : "rowgroup",
      ownerState: l
    }, i))
  });
});
process.env.NODE_ENV !== "production" && (wm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component, normally `TableRow`.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function TO(e) {
  return Pe("MuiTableCell", e);
}
const Zi = Ce("MuiTableCell", ["root", "head", "body", "footer", "sizeSmall", "sizeMedium", "paddingCheckbox", "paddingNone", "alignLeft", "alignCenter", "alignRight", "alignJustify", "stickyHeader"]), wO = ["align", "className", "component", "padding", "scope", "size", "sortDirection", "variant"], EO = (e) => {
  const {
    classes: t,
    variant: n,
    align: o,
    padding: a,
    size: s,
    stickyHeader: i
  } = e, l = {
    root: ["root", n, i && "stickyHeader", o !== "inherit" && `align${de(o)}`, a !== "normal" && `padding${de(a)}`, `size${de(s)}`]
  };
  return Se(l, TO, t);
}, CO = Z("td", {
  name: "MuiTableCell",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, t[n.variant], t[`size${de(n.size)}`], n.padding !== "normal" && t[`padding${de(n.padding)}`], n.align !== "inherit" && t[`align${de(n.align)}`], n.stickyHeader && t.stickyHeader];
  }
})(({
  theme: e,
  ownerState: t
}) => b({}, e.typography.body2, {
  display: "table-cell",
  verticalAlign: "inherit",
  // Workaround for a rendering bug with spanned columns in Chrome 62.0.
  // Removes the alpha (sets it to 1), and lightens or darkens the theme color.
  borderBottom: e.vars ? `1px solid ${e.vars.palette.TableCell.border}` : `1px solid
    ${e.palette.mode === "light" ? bf(qe(e.palette.divider, 1), 0.88) : hf(qe(e.palette.divider, 1), 0.68)}`,
  textAlign: "left",
  padding: 16
}, t.variant === "head" && {
  color: (e.vars || e).palette.text.primary,
  lineHeight: e.typography.pxToRem(24),
  fontWeight: e.typography.fontWeightMedium
}, t.variant === "body" && {
  color: (e.vars || e).palette.text.primary
}, t.variant === "footer" && {
  color: (e.vars || e).palette.text.secondary,
  lineHeight: e.typography.pxToRem(21),
  fontSize: e.typography.pxToRem(12)
}, t.size === "small" && {
  padding: "6px 16px",
  [`&.${Zi.paddingCheckbox}`]: {
    width: 24,
    // prevent the checkbox column from growing
    padding: "0 12px 0 16px",
    "& > *": {
      padding: 0
    }
  }
}, t.padding === "checkbox" && {
  width: 48,
  // prevent the checkbox column from growing
  padding: "0 0 0 4px"
}, t.padding === "none" && {
  padding: 0
}, t.align === "left" && {
  textAlign: "left"
}, t.align === "center" && {
  textAlign: "center"
}, t.align === "right" && {
  textAlign: "right",
  flexDirection: "row-reverse"
}, t.align === "justify" && {
  textAlign: "justify"
}, t.stickyHeader && {
  position: "sticky",
  top: 0,
  zIndex: 2,
  backgroundColor: (e.vars || e).palette.background.default
})), Em = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTableCell"
  }), {
    align: a = "inherit",
    className: s,
    component: i,
    padding: l,
    scope: c,
    size: u,
    sortDirection: d,
    variant: f
  } = o, p = ie(o, wO), m = g.useContext(uc), v = g.useContext(ga), h = v && v.variant === "head";
  let y;
  i ? y = i : y = h ? "th" : "td";
  let w = c;
  y === "td" ? w = void 0 : !w && h && (w = "col");
  const C = f || v && v.variant, E = b({}, o, {
    align: a,
    component: y,
    padding: l || (m && m.padding ? m.padding : "normal"),
    size: u || (m && m.size ? m.size : "medium"),
    sortDirection: d,
    stickyHeader: C === "head" && m && m.stickyHeader,
    variant: C
  }), O = EO(E);
  let T = null;
  return d && (T = d === "asc" ? "ascending" : "descending"), /* @__PURE__ */ x.jsx(CO, b({
    as: y,
    ref: n,
    className: pe(O.root, s),
    "aria-sort": T,
    scope: w,
    ownerState: E
  }, p));
});
process.env.NODE_ENV !== "production" && (Em.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Set the text-align on the table cell content.
   *
   * Monetary or generally number fields **should be right aligned** as that allows
   * you to add them up quickly in your head without having to worry about decimals.
   * @default 'inherit'
   */
  align: r.oneOf(["center", "inherit", "justify", "left", "right"]),
  /**
   * The content of the component.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * Sets the padding applied to the cell.
   * The prop defaults to the value (`'default'`) inherited from the parent Table component.
   */
  padding: r.oneOf(["checkbox", "none", "normal"]),
  /**
   * Set scope attribute.
   */
  scope: r.string,
  /**
   * Specify the size of the cell.
   * The prop defaults to the value (`'medium'`) inherited from the parent Table component.
   */
  size: r.oneOfType([r.oneOf(["medium", "small"]), r.string]),
  /**
   * Set aria-sort direction.
   */
  sortDirection: r.oneOf(["asc", "desc", !1]),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Specify the cell type.
   * The prop defaults to the value inherited from the parent TableHead, TableBody, or TableFooter components.
   */
  variant: r.oneOfType([r.oneOf(["body", "footer", "head"]), r.string])
});
function OO(e) {
  return Pe("MuiTableContainer", e);
}
Ce("MuiTableContainer", ["root"]);
const SO = ["className", "component"], PO = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"]
  }, OO, t);
}, RO = Z("div", {
  name: "MuiTableContainer",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  width: "100%",
  overflowX: "auto"
}), Cm = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTableContainer"
  }), {
    className: a,
    component: s = "div"
  } = o, i = ie(o, SO), l = b({}, o, {
    component: s
  }), c = PO(l);
  return /* @__PURE__ */ x.jsx(RO, b({
    ref: n,
    as: s,
    className: pe(c.root, a),
    ownerState: l
  }, i));
});
process.env.NODE_ENV !== "production" && (Cm.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component, normally `Table`.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function DO(e) {
  return Pe("MuiTableHead", e);
}
Ce("MuiTableHead", ["root"]);
const $O = ["className", "component"], kO = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"]
  }, DO, t);
}, _O = Z("thead", {
  name: "MuiTableHead",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "table-header-group"
}), MO = {
  variant: "head"
}, Cd = "thead", Om = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTableHead"
  }), {
    className: a,
    component: s = Cd
  } = o, i = ie(o, $O), l = b({}, o, {
    component: s
  }), c = kO(l);
  return /* @__PURE__ */ x.jsx(ga.Provider, {
    value: MO,
    children: /* @__PURE__ */ x.jsx(_O, b({
      as: s,
      className: pe(c.root, a),
      ref: n,
      role: s === Cd ? null : "rowgroup",
      ownerState: l
    }, i))
  });
});
process.env.NODE_ENV !== "production" && (Om.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * The content of the component, normally `TableRow`.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function IO(e) {
  return Pe("MuiTableRow", e);
}
const Od = Ce("MuiTableRow", ["root", "selected", "hover", "head", "footer"]), NO = ["className", "component", "hover", "selected"], jO = (e) => {
  const {
    classes: t,
    selected: n,
    hover: o,
    head: a,
    footer: s
  } = e;
  return Se({
    root: ["root", n && "selected", o && "hover", a && "head", s && "footer"]
  }, IO, t);
}, AO = Z("tr", {
  name: "MuiTableRow",
  slot: "Root",
  overridesResolver: (e, t) => {
    const {
      ownerState: n
    } = e;
    return [t.root, n.head && t.head, n.footer && t.footer];
  }
})(({
  theme: e
}) => ({
  color: "inherit",
  display: "table-row",
  verticalAlign: "middle",
  // We disable the focus ring for mouse, touch and keyboard users.
  outline: 0,
  [`&.${Od.hover}:hover`]: {
    backgroundColor: (e.vars || e).palette.action.hover
  },
  [`&.${Od.selected}`]: {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.primary.mainChannel} / ${e.vars.palette.action.selectedOpacity})` : qe(e.palette.primary.main, e.palette.action.selectedOpacity),
    "&:hover": {
      backgroundColor: e.vars ? `rgba(${e.vars.palette.primary.mainChannel} / calc(${e.vars.palette.action.selectedOpacity} + ${e.vars.palette.action.hoverOpacity}))` : qe(e.palette.primary.main, e.palette.action.selectedOpacity + e.palette.action.hoverOpacity)
    }
  }
})), Sd = "tr", dc = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTableRow"
  }), {
    className: a,
    component: s = Sd,
    hover: i = !1,
    selected: l = !1
  } = o, c = ie(o, NO), u = g.useContext(ga), d = b({}, o, {
    component: s,
    hover: i,
    selected: l,
    head: u && u.variant === "head",
    footer: u && u.variant === "footer"
  }), f = jO(d);
  return /* @__PURE__ */ x.jsx(AO, b({
    as: s,
    ref: n,
    className: pe(f.root, a),
    role: s === Sd ? null : "row",
    ownerState: d
  }, c));
});
process.env.NODE_ENV !== "production" && (dc.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Should be valid `<tr>` children such as `TableCell`.
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: r.elementType,
  /**
   * If `true`, the table row will shade on hover.
   * @default false
   */
  hover: r.bool,
  /**
   * If `true`, the table row will have the selected shading.
   * @default false
   */
  selected: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function FO(e) {
  return Pe("MuiTextField", e);
}
Ce("MuiTextField", ["root"]);
const VO = ["autoComplete", "autoFocus", "children", "className", "color", "defaultValue", "disabled", "error", "FormHelperTextProps", "fullWidth", "helperText", "id", "InputLabelProps", "inputProps", "InputProps", "inputRef", "label", "maxRows", "minRows", "multiline", "name", "onBlur", "onChange", "onFocus", "placeholder", "required", "rows", "select", "SelectProps", "type", "value", "variant"], LO = {
  standard: Us,
  filled: Ws,
  outlined: ba
}, BO = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"]
  }, FO, t);
}, zO = Z(im, {
  name: "MuiTextField",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({}), os = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiTextField"
  }), {
    autoComplete: a,
    autoFocus: s = !1,
    children: i,
    className: l,
    color: c = "primary",
    defaultValue: u,
    disabled: d = !1,
    error: f = !1,
    FormHelperTextProps: p,
    fullWidth: m = !1,
    helperText: v,
    id: h,
    InputLabelProps: y,
    inputProps: w,
    InputProps: C,
    inputRef: E,
    label: O,
    maxRows: T,
    minRows: P,
    multiline: S = !1,
    name: j,
    onBlur: $,
    onChange: V,
    onFocus: _,
    placeholder: L,
    required: M = !1,
    rows: R,
    select: D = !1,
    SelectProps: F,
    type: z,
    value: N,
    variant: q = "outlined"
  } = o, A = ie(o, VO), H = b({}, o, {
    autoFocus: s,
    color: c,
    disabled: d,
    error: f,
    fullWidth: m,
    multiline: S,
    required: M,
    select: D,
    variant: q
  }), te = BO(H);
  process.env.NODE_ENV !== "production" && D && !i && console.error("MUI: `children` must be passed when using the `TextField` component with `select`.");
  const re = {};
  q === "outlined" && (y && typeof y.shrink < "u" && (re.notched = y.shrink), re.label = O), D && ((!F || !F.native) && (re.id = void 0), re["aria-describedby"] = void 0);
  const B = Bn(h), G = v && B ? `${B}-helper-text` : void 0, ee = O && B ? `${B}-label` : void 0, W = LO[q], J = /* @__PURE__ */ x.jsx(W, b({
    "aria-describedby": G,
    autoComplete: a,
    autoFocus: s,
    defaultValue: u,
    fullWidth: m,
    multiline: S,
    name: j,
    rows: R,
    maxRows: T,
    minRows: P,
    type: z,
    value: N,
    id: B,
    inputRef: E,
    onBlur: $,
    onChange: V,
    onFocus: _,
    placeholder: L,
    inputProps: w
  }, re, C));
  return /* @__PURE__ */ x.jsxs(zO, b({
    className: pe(te.root, l),
    disabled: d,
    error: f,
    fullWidth: m,
    ref: n,
    required: M,
    color: c,
    variant: q,
    ownerState: H
  }, A, {
    children: [O != null && O !== "" && /* @__PURE__ */ x.jsx(cm, b({
      htmlFor: B,
      id: ee
    }, y, {
      children: O
    })), D ? /* @__PURE__ */ x.jsx(cc, b({
      "aria-describedby": G,
      id: B,
      labelId: ee,
      value: N,
      input: J
    }, F, {
      children: i
    })) : J, v && /* @__PURE__ */ x.jsx(rc, b({
      id: G
    }, p, {
      children: v
    }))]
  }));
});
process.env.NODE_ENV !== "production" && (os.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * This prop helps users to fill forms faster, especially on mobile devices.
   * The name can be confusing, as it's more like an autofill.
   * You can learn more about it [following the specification](https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofill).
   */
  autoComplete: r.string,
  /**
   * If `true`, the `input` element is focused during the first mount.
   * @default false
   */
  autoFocus: r.bool,
  /**
   * @ignore
   */
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * @ignore
   */
  className: r.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * @default 'primary'
   */
  color: r.oneOfType([r.oneOf(["primary", "secondary", "error", "info", "success", "warning"]), r.string]),
  /**
   * The default value. Use when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the component is disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, the label is displayed in an error state.
   * @default false
   */
  error: r.bool,
  /**
   * Props applied to the [`FormHelperText`](/material-ui/api/form-helper-text/) element.
   */
  FormHelperTextProps: r.object,
  /**
   * If `true`, the input will take up the full width of its container.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * The helper text content.
   */
  helperText: r.node,
  /**
   * The id of the `input` element.
   * Use this prop to make `label` and `helperText` accessible for screen readers.
   */
  id: r.string,
  /**
   * Props applied to the [`InputLabel`](/material-ui/api/input-label/) element.
   * Pointer events like `onClick` are enabled if and only if `shrink` is `true`.
   */
  InputLabelProps: r.object,
  /**
   * [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Attributes) applied to the `input` element.
   */
  inputProps: r.object,
  /**
   * Props applied to the Input element.
   * It will be a [`FilledInput`](/material-ui/api/filled-input/),
   * [`OutlinedInput`](/material-ui/api/outlined-input/) or [`Input`](/material-ui/api/input/)
   * component depending on the `variant` prop value.
   */
  InputProps: r.object,
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * The label content.
   */
  label: r.node,
  /**
   * If `dense` or `normal`, will adjust vertical spacing of this and contained components.
   * @default 'none'
   */
  margin: r.oneOf(["dense", "none", "normal"]),
  /**
   * Maximum number of rows to display when multiline option is set to true.
   */
  maxRows: r.oneOfType([r.number, r.string]),
  /**
   * Minimum number of rows to display when multiline option is set to true.
   */
  minRows: r.oneOfType([r.number, r.string]),
  /**
   * If `true`, a `textarea` element is rendered instead of an input.
   * @default false
   */
  multiline: r.bool,
  /**
   * Name attribute of the `input` element.
   */
  name: r.string,
  /**
   * @ignore
   */
  onBlur: r.func,
  /**
   * Callback fired when the value is changed.
   *
   * @param {object} event The event source of the callback.
   * You can pull out the new value by accessing `event.target.value` (string).
   */
  onChange: r.func,
  /**
   * @ignore
   */
  onFocus: r.func,
  /**
   * The short hint displayed in the `input` before the user enters a value.
   */
  placeholder: r.string,
  /**
   * If `true`, the label is displayed as required and the `input` element is required.
   * @default false
   */
  required: r.bool,
  /**
   * Number of rows to display when multiline option is set to true.
   */
  rows: r.oneOfType([r.number, r.string]),
  /**
   * Render a [`Select`](/material-ui/api/select/) element while passing the Input element to `Select` as `input` parameter.
   * If this option is set you must pass the options of the select as children.
   * @default false
   */
  select: r.bool,
  /**
   * Props applied to the [`Select`](/material-ui/api/select/) element.
   */
  SelectProps: r.object,
  /**
   * The size of the component.
   */
  size: r.oneOfType([r.oneOf(["medium", "small"]), r.string]),
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Type of the `input` element. It should be [a valid HTML5 input type](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Form_%3Cinput%3E_types).
   */
  type: r.string,
  /**
   * The value of the `input` element, required for a controlled component.
   */
  value: r.any,
  /**
   * The variant to use.
   * @default 'outlined'
   */
  variant: r.oneOf(["filled", "outlined", "standard"])
});
const WO = ({ customHeight: e, ...t }) => /* @__PURE__ */ x.jsx(
  tn,
  {
    minHeight: e || "100px",
    minWidth: "300px",
    alignItems: "center",
    display: "flex",
    justifyContent: "center",
    children: /* @__PURE__ */ x.jsx(sm, { color: "inherit", ...t })
  }
), as = Z(Ar)(({ theme: e }) => ({
  fontSize: e.spacing(2),
  padding: "6px 15px",
  color: e.palette.text.primary,
  backgroundColor: e.palette.success.main,
  textTransform: "none",
  "&:hover": {
    backgroundColor: e.palette.success.dark,
    color: e.palette.common.white
  }
})), rr = Z(Yo)(({ theme: e }) => ({
  color: e.palette.text.primary
})), Fr = Z(pr)(({ theme: e }) => ({
  borderRadius: e.spacing(1),
  "&:hover": {
    backgroundColor: e.palette.success.dark,
    "&> svg": {
      color: e.palette.common.white
    }
  }
})), UO = Z(pr)(({ theme: e }) => ({
  borderRadius: "5px",
  width: "50px",
  height: "50px",
  backgroundColor: e.palette.success.main,
  "&:hover": {
    backgroundColor: e.palette.success.dark,
    "&> svg": {
      color: e.palette.common.white
    }
  }
})), tt = Z(Em)(({ theme: e }) => ({
  border: `1px solid ${e.palette.primary.light}`,
  margin: "1px",
  padding: e.spacing(1),
  [`&.${Zi.head}`]: {
    backgroundColor: e.palette.success.main,
    color: e.palette.text.primary,
    fontWeight: 550,
    fontSize: e.typography.body1.fontSize,
    fontFamily: "Campton-SemiBold, Arial, sans-serif"
  },
  [`&.${Zi.body}`]: {
    fontSize: e.spacing(2),
    fontFamily: "Campton-Light, Arial, sans-serif"
  }
})), Sm = Z(dc)(({ theme: e }) => ({
  "&:nth-of-type(even)": {
    backgroundColor: e.palette.text.disabled
  }
})), Pd = Z(ba)(({ theme: e }) => ({
  height: "40px",
  "&: hover .MuiOutlinedInput-notchedOutline": {
    borderColor: e.palette.success.main
  },
  "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
    border: `1px solid ${e.palette.success.main}`
  }
})), Pm = Z(ec)(({ theme: e }) => ({
  "& .MuiDialogContent-root": {
    padding: "0 20px 20px 20px",
    border: "none",
    height: "50%",
    top: e.spacing(9)
  }
})), Ji = Z(Rt)(({ theme: e }) => ({
  fontSize: "22px",
  fontWeight: 500,
  color: e.palette.text.primary
})), Hr = (e, t) => {
  if (e.ok)
    return e;
  (e.status === 403 || e.status === 401) && t();
  let n = new Error(e.statusText);
  return Promise.reject(n);
}, vn = (e, t) => {
  console.log(e), t();
}, HO = {
  name: "",
  data: {
    questionnaire: {
      id: "questionnaire",
      type: "questionnaire",
      items: []
    }
  },
  metadata: {
    label: "",
    languages: [
      "fi",
      "en"
    ]
  }
}, Rm = {
  label: void 0,
  latestTagDate: void 0,
  lastSaved: void 0,
  latestTagName: void 0
}, qO = (e) => {
  let t = {
    Accept: "application/json",
    "Content-Type": "application/json; charset=utf-8"
  };
  return e.csrf && (t[e.csrf.key] = e.csrf.value), t;
}, mo = (e, t, n) => (t.headers || (t.headers = qO(n)), fetch(e, {
  credentials: "same-origin",
  ...t
})), YO = async (e) => {
  let t = `${e.dialobApiUrl}/api/forms`;
  return await mo(t, {
    method: "GET"
  }, e);
}, KO = async (e, t) => {
  let n = `${e.dialobApiUrl}/api/forms/${t}/tags`;
  return await mo(n, {
    method: "GET"
  }, e);
}, Dm = async (e, t) => {
  let n = `${t.dialobApiUrl}/api/forms/${e}`;
  return await mo(n, {
    method: "GET"
  }, t);
}, Qi = async (e, t) => {
  let n = `${t.dialobApiUrl}/api/forms`;
  return await mo(n, {
    method: "POST",
    body: JSON.stringify(e)
  }, t);
}, GO = async (e, t) => {
  let n = `${t.dialobApiUrl}/api/forms/${e.name}?force=true`;
  return await mo(n, {
    method: "PUT",
    body: JSON.stringify(e)
  }, t);
}, XO = async (e, t) => {
  let n = `${t.dialobApiUrl}/api/forms/${e}`;
  return await mo(n, {
    method: "DELETE"
  }, t);
};
var ss = function() {
  return ss = Object.assign || function(t) {
    for (var n, o = 1, a = arguments.length; o < a; o++) {
      n = arguments[o];
      for (var s in n) Object.prototype.hasOwnProperty.call(n, s) && (t[s] = n[s]);
    }
    return t;
  }, ss.apply(this, arguments);
};
function is(e, t) {
  var n = {};
  for (var o in e) Object.prototype.hasOwnProperty.call(e, o) && t.indexOf(o) < 0 && (n[o] = e[o]);
  if (e != null && typeof Object.getOwnPropertySymbols == "function")
    for (var a = 0, o = Object.getOwnPropertySymbols(e); a < o.length; a++)
      t.indexOf(o[a]) < 0 && Object.prototype.propertyIsEnumerable.call(e, o[a]) && (n[o[a]] = e[o[a]]);
  return n;
}
function ZO(e, t, n) {
  if (n === void 0 && (n = Error), !e)
    throw new n(t);
}
var JO = function(e) {
  process.env.NODE_ENV !== "production" && console.error(e);
}, QO = function(e) {
  process.env.NODE_ENV !== "production" && console.warn(e);
}, eS = {
  formats: {},
  messages: {},
  timeZone: void 0,
  defaultLocale: "en",
  defaultFormats: {},
  fallbackOnEmptyString: !0,
  onError: JO,
  onWarn: QO
};
function tS(e) {
  ZO(e, "[React Intl] Could not find required `intl` object. <IntlProvider> needs to exist in the component ancestry.");
}
ss(ss({}, eS), { textComponent: g.Fragment });
function Rd(e, t) {
  if (e === t)
    return !0;
  if (!e || !t)
    return !1;
  var n = Object.keys(e), o = Object.keys(t), a = n.length;
  if (o.length !== a)
    return !1;
  for (var s = 0; s < a; s++) {
    var i = n[s];
    if (e[i] !== t[i] || !Object.prototype.hasOwnProperty.call(t, i))
      return !1;
  }
  return !0;
}
var pc = g.createContext(null);
pc.Consumer;
pc.Provider;
var nS = pc;
function hr() {
  var e = g.useContext(nS);
  return tS(e), e;
}
var el;
(function(e) {
  e.formatDate = "FormattedDate", e.formatTime = "FormattedTime", e.formatNumber = "FormattedNumber", e.formatList = "FormattedList", e.formatDisplayName = "FormattedDisplayName";
})(el || (el = {}));
var tl;
(function(e) {
  e.formatDate = "FormattedDateParts", e.formatTime = "FormattedTimeParts", e.formatNumber = "FormattedNumberParts", e.formatList = "FormattedListParts";
})(tl || (tl = {}));
function $m(e) {
  var t = function(n) {
    var o = hr(), a = n.value, s = n.children, i = is(n, ["value", "children"]), l = typeof a == "string" ? new Date(a || 0) : a, c = e === "formatDate" ? o.formatDateToParts(l, i) : o.formatTimeToParts(l, i);
    return s(c);
  };
  return t.displayName = tl[e], t;
}
function ya(e) {
  var t = function(n) {
    var o = hr(), a = n.value, s = n.children, i = is(
      n,
      ["value", "children"]
    ), l = o[e](a, i);
    if (typeof s == "function")
      return s(l);
    var c = o.textComponent || g.Fragment;
    return g.createElement(c, null, l);
  };
  return t.displayName = el[e], t;
}
function rS(e, t) {
  var n = e.values, o = is(e, ["values"]), a = t.values, s = is(t, ["values"]);
  return Rd(a, n) && Rd(o, s);
}
function km(e) {
  var t = hr(), n = t.formatMessage, o = t.textComponent, a = o === void 0 ? g.Fragment : o, s = e.id, i = e.description, l = e.defaultMessage, c = e.values, u = e.children, d = e.tagName, f = d === void 0 ? a : d, p = e.ignoreTag, m = { id: s, description: i, defaultMessage: l }, v = n(m, c, {
    ignoreTag: p
  });
  return typeof u == "function" ? u(Array.isArray(v) ? v : [v]) : f ? g.createElement(f, null, g.Children.toArray(v)) : g.createElement(g.Fragment, null, v);
}
km.displayName = "FormattedMessage";
var bt = g.memo(km, rS);
bt.displayName = "MemoizedFormattedMessage";
ya("formatDate");
ya("formatTime");
ya("formatNumber");
ya("formatList");
ya("formatDisplayName");
$m("formatDate");
$m("formatTime");
var oS = function(t) {
  return aS(t) && !sS(t);
};
function aS(e) {
  return !!e && typeof e == "object";
}
function sS(e) {
  var t = Object.prototype.toString.call(e);
  return t === "[object RegExp]" || t === "[object Date]" || cS(e);
}
var iS = typeof Symbol == "function" && Symbol.for, lS = iS ? Symbol.for("react.element") : 60103;
function cS(e) {
  return e.$$typeof === lS;
}
function uS(e) {
  return Array.isArray(e) ? [] : {};
}
function ls(e, t) {
  return t.clone !== !1 && t.isMergeableObject(e) ? Qo(uS(e), e, t) : e;
}
function dS(e, t, n) {
  return e.concat(t).map(function(o) {
    return ls(o, n);
  });
}
function pS(e, t, n) {
  var o = {};
  return n.isMergeableObject(e) && Object.keys(e).forEach(function(a) {
    o[a] = ls(e[a], n);
  }), Object.keys(t).forEach(function(a) {
    !n.isMergeableObject(t[a]) || !e[a] ? o[a] = ls(t[a], n) : o[a] = Qo(e[a], t[a], n);
  }), o;
}
function Qo(e, t, n) {
  n = n || {}, n.arrayMerge = n.arrayMerge || dS, n.isMergeableObject = n.isMergeableObject || oS;
  var o = Array.isArray(t), a = Array.isArray(e), s = o === a;
  return s ? o ? n.arrayMerge(e, t, n) : pS(e, t, n) : ls(t, n);
}
Qo.all = function(t, n) {
  if (!Array.isArray(t))
    throw new Error("first argument should be an array");
  return t.reduce(function(o, a) {
    return Qo(o, a, n);
  }, {});
};
var nl = Qo, _m = typeof global == "object" && global && global.Object === Object && global, fS = typeof self == "object" && self && self.Object === Object && self, mn = _m || fS || Function("return this")(), Vn = mn.Symbol, Mm = Object.prototype, mS = Mm.hasOwnProperty, hS = Mm.toString, Po = Vn ? Vn.toStringTag : void 0;
function bS(e) {
  var t = mS.call(e, Po), n = e[Po];
  try {
    e[Po] = void 0;
    var o = !0;
  } catch {
  }
  var a = hS.call(e);
  return o && (t ? e[Po] = n : delete e[Po]), a;
}
var gS = Object.prototype, yS = gS.toString;
function vS(e) {
  return yS.call(e);
}
var xS = "[object Null]", TS = "[object Undefined]", Dd = Vn ? Vn.toStringTag : void 0;
function br(e) {
  return e == null ? e === void 0 ? TS : xS : Dd && Dd in Object(e) ? bS(e) : vS(e);
}
function Im(e, t) {
  return function(n) {
    return e(t(n));
  };
}
var fc = Im(Object.getPrototypeOf, Object);
function gr(e) {
  return e != null && typeof e == "object";
}
var wS = "[object Object]", ES = Function.prototype, CS = Object.prototype, Nm = ES.toString, OS = CS.hasOwnProperty, SS = Nm.call(Object);
function $d(e) {
  if (!gr(e) || br(e) != wS)
    return !1;
  var t = fc(e);
  if (t === null)
    return !0;
  var n = OS.call(t, "constructor") && t.constructor;
  return typeof n == "function" && n instanceof n && Nm.call(n) == SS;
}
function PS() {
  this.__data__ = [], this.size = 0;
}
function jm(e, t) {
  return e === t || e !== e && t !== t;
}
function Hs(e, t) {
  for (var n = e.length; n--; )
    if (jm(e[n][0], t))
      return n;
  return -1;
}
var RS = Array.prototype, DS = RS.splice;
function $S(e) {
  var t = this.__data__, n = Hs(t, e);
  if (n < 0)
    return !1;
  var o = t.length - 1;
  return n == o ? t.pop() : DS.call(t, n, 1), --this.size, !0;
}
function kS(e) {
  var t = this.__data__, n = Hs(t, e);
  return n < 0 ? void 0 : t[n][1];
}
function _S(e) {
  return Hs(this.__data__, e) > -1;
}
function MS(e, t) {
  var n = this.__data__, o = Hs(n, e);
  return o < 0 ? (++this.size, n.push([e, t])) : n[o][1] = t, this;
}
function Pn(e) {
  var t = -1, n = e == null ? 0 : e.length;
  for (this.clear(); ++t < n; ) {
    var o = e[t];
    this.set(o[0], o[1]);
  }
}
Pn.prototype.clear = PS;
Pn.prototype.delete = $S;
Pn.prototype.get = kS;
Pn.prototype.has = _S;
Pn.prototype.set = MS;
function IS() {
  this.__data__ = new Pn(), this.size = 0;
}
function NS(e) {
  var t = this.__data__, n = t.delete(e);
  return this.size = t.size, n;
}
function jS(e) {
  return this.__data__.get(e);
}
function AS(e) {
  return this.__data__.has(e);
}
function va(e) {
  var t = typeof e;
  return e != null && (t == "object" || t == "function");
}
var FS = "[object AsyncFunction]", VS = "[object Function]", LS = "[object GeneratorFunction]", BS = "[object Proxy]";
function Am(e) {
  if (!va(e))
    return !1;
  var t = br(e);
  return t == VS || t == LS || t == FS || t == BS;
}
var Ci = mn["__core-js_shared__"], kd = function() {
  var e = /[^.]+$/.exec(Ci && Ci.keys && Ci.keys.IE_PROTO || "");
  return e ? "Symbol(src)_1." + e : "";
}();
function zS(e) {
  return !!kd && kd in e;
}
var WS = Function.prototype, US = WS.toString;
function yr(e) {
  if (e != null) {
    try {
      return US.call(e);
    } catch {
    }
    try {
      return e + "";
    } catch {
    }
  }
  return "";
}
var HS = /[\\^$.*+?()[\]{}|]/g, qS = /^\[object .+?Constructor\]$/, YS = Function.prototype, KS = Object.prototype, GS = YS.toString, XS = KS.hasOwnProperty, ZS = RegExp(
  "^" + GS.call(XS).replace(HS, "\\$&").replace(/hasOwnProperty|(function).*?(?=\\\()| for .+?(?=\\\])/g, "$1.*?") + "$"
);
function JS(e) {
  if (!va(e) || zS(e))
    return !1;
  var t = Am(e) ? ZS : qS;
  return t.test(yr(e));
}
function QS(e, t) {
  return e == null ? void 0 : e[t];
}
function vr(e, t) {
  var n = QS(e, t);
  return JS(n) ? n : void 0;
}
var ea = vr(mn, "Map"), ta = vr(Object, "create");
function eP() {
  this.__data__ = ta ? ta(null) : {}, this.size = 0;
}
function tP(e) {
  var t = this.has(e) && delete this.__data__[e];
  return this.size -= t ? 1 : 0, t;
}
var nP = "__lodash_hash_undefined__", rP = Object.prototype, oP = rP.hasOwnProperty;
function aP(e) {
  var t = this.__data__;
  if (ta) {
    var n = t[e];
    return n === nP ? void 0 : n;
  }
  return oP.call(t, e) ? t[e] : void 0;
}
var sP = Object.prototype, iP = sP.hasOwnProperty;
function lP(e) {
  var t = this.__data__;
  return ta ? t[e] !== void 0 : iP.call(t, e);
}
var cP = "__lodash_hash_undefined__";
function uP(e, t) {
  var n = this.__data__;
  return this.size += this.has(e) ? 0 : 1, n[e] = ta && t === void 0 ? cP : t, this;
}
function ur(e) {
  var t = -1, n = e == null ? 0 : e.length;
  for (this.clear(); ++t < n; ) {
    var o = e[t];
    this.set(o[0], o[1]);
  }
}
ur.prototype.clear = eP;
ur.prototype.delete = tP;
ur.prototype.get = aP;
ur.prototype.has = lP;
ur.prototype.set = uP;
function dP() {
  this.size = 0, this.__data__ = {
    hash: new ur(),
    map: new (ea || Pn)(),
    string: new ur()
  };
}
function pP(e) {
  var t = typeof e;
  return t == "string" || t == "number" || t == "symbol" || t == "boolean" ? e !== "__proto__" : e === null;
}
function qs(e, t) {
  var n = e.__data__;
  return pP(t) ? n[typeof t == "string" ? "string" : "hash"] : n.map;
}
function fP(e) {
  var t = qs(this, e).delete(e);
  return this.size -= t ? 1 : 0, t;
}
function mP(e) {
  return qs(this, e).get(e);
}
function hP(e) {
  return qs(this, e).has(e);
}
function bP(e, t) {
  var n = qs(this, e), o = n.size;
  return n.set(e, t), this.size += n.size == o ? 0 : 1, this;
}
function Un(e) {
  var t = -1, n = e == null ? 0 : e.length;
  for (this.clear(); ++t < n; ) {
    var o = e[t];
    this.set(o[0], o[1]);
  }
}
Un.prototype.clear = dP;
Un.prototype.delete = fP;
Un.prototype.get = mP;
Un.prototype.has = hP;
Un.prototype.set = bP;
var gP = 200;
function yP(e, t) {
  var n = this.__data__;
  if (n instanceof Pn) {
    var o = n.__data__;
    if (!ea || o.length < gP - 1)
      return o.push([e, t]), this.size = ++n.size, this;
    n = this.__data__ = new Un(o);
  }
  return n.set(e, t), this.size = n.size, this;
}
function ho(e) {
  var t = this.__data__ = new Pn(e);
  this.size = t.size;
}
ho.prototype.clear = IS;
ho.prototype.delete = NS;
ho.prototype.get = jS;
ho.prototype.has = AS;
ho.prototype.set = yP;
function vP(e, t) {
  for (var n = -1, o = e == null ? 0 : e.length; ++n < o && t(e[n], n, e) !== !1; )
    ;
  return e;
}
var _d = function() {
  try {
    var e = vr(Object, "defineProperty");
    return e({}, "", {}), e;
  } catch {
  }
}();
function Fm(e, t, n) {
  t == "__proto__" && _d ? _d(e, t, {
    configurable: !0,
    enumerable: !0,
    value: n,
    writable: !0
  }) : e[t] = n;
}
var xP = Object.prototype, TP = xP.hasOwnProperty;
function Vm(e, t, n) {
  var o = e[t];
  (!(TP.call(e, t) && jm(o, n)) || n === void 0 && !(t in e)) && Fm(e, t, n);
}
function Ys(e, t, n, o) {
  var a = !n;
  n || (n = {});
  for (var s = -1, i = t.length; ++s < i; ) {
    var l = t[s], c = void 0;
    c === void 0 && (c = e[l]), a ? Fm(n, l, c) : Vm(n, l, c);
  }
  return n;
}
function wP(e, t) {
  for (var n = -1, o = Array(e); ++n < e; )
    o[n] = t(n);
  return o;
}
var EP = "[object Arguments]";
function Md(e) {
  return gr(e) && br(e) == EP;
}
var Lm = Object.prototype, CP = Lm.hasOwnProperty, OP = Lm.propertyIsEnumerable, SP = Md(/* @__PURE__ */ function() {
  return arguments;
}()) ? Md : function(e) {
  return gr(e) && CP.call(e, "callee") && !OP.call(e, "callee");
}, xa = Array.isArray;
function PP() {
  return !1;
}
var Bm = typeof exports == "object" && exports && !exports.nodeType && exports, Id = Bm && typeof module == "object" && module && !module.nodeType && module, RP = Id && Id.exports === Bm, Nd = RP ? mn.Buffer : void 0, DP = Nd ? Nd.isBuffer : void 0, zm = DP || PP, $P = 9007199254740991, kP = /^(?:0|[1-9]\d*)$/;
function _P(e, t) {
  var n = typeof e;
  return t = t ?? $P, !!t && (n == "number" || n != "symbol" && kP.test(e)) && e > -1 && e % 1 == 0 && e < t;
}
var MP = 9007199254740991;
function Wm(e) {
  return typeof e == "number" && e > -1 && e % 1 == 0 && e <= MP;
}
var IP = "[object Arguments]", NP = "[object Array]", jP = "[object Boolean]", AP = "[object Date]", FP = "[object Error]", VP = "[object Function]", LP = "[object Map]", BP = "[object Number]", zP = "[object Object]", WP = "[object RegExp]", UP = "[object Set]", HP = "[object String]", qP = "[object WeakMap]", YP = "[object ArrayBuffer]", KP = "[object DataView]", GP = "[object Float32Array]", XP = "[object Float64Array]", ZP = "[object Int8Array]", JP = "[object Int16Array]", QP = "[object Int32Array]", e1 = "[object Uint8Array]", t1 = "[object Uint8ClampedArray]", n1 = "[object Uint16Array]", r1 = "[object Uint32Array]", Je = {};
Je[GP] = Je[XP] = Je[ZP] = Je[JP] = Je[QP] = Je[e1] = Je[t1] = Je[n1] = Je[r1] = !0;
Je[IP] = Je[NP] = Je[YP] = Je[jP] = Je[KP] = Je[AP] = Je[FP] = Je[VP] = Je[LP] = Je[BP] = Je[zP] = Je[WP] = Je[UP] = Je[HP] = Je[qP] = !1;
function o1(e) {
  return gr(e) && Wm(e.length) && !!Je[br(e)];
}
function mc(e) {
  return function(t) {
    return e(t);
  };
}
var Um = typeof exports == "object" && exports && !exports.nodeType && exports, Vo = Um && typeof module == "object" && module && !module.nodeType && module, a1 = Vo && Vo.exports === Um, Oi = a1 && _m.process, oo = function() {
  try {
    var e = Vo && Vo.require && Vo.require("util").types;
    return e || Oi && Oi.binding && Oi.binding("util");
  } catch {
  }
}(), jd = oo && oo.isTypedArray, s1 = jd ? mc(jd) : o1, i1 = Object.prototype, l1 = i1.hasOwnProperty;
function Hm(e, t) {
  var n = xa(e), o = !n && SP(e), a = !n && !o && zm(e), s = !n && !o && !a && s1(e), i = n || o || a || s, l = i ? wP(e.length, String) : [], c = l.length;
  for (var u in e)
    (t || l1.call(e, u)) && !(i && // Safari 9 has enumerable `arguments.length` in strict mode.
    (u == "length" || // Node.js 0.10 has enumerable non-index properties on buffers.
    a && (u == "offset" || u == "parent") || // PhantomJS 2 has enumerable non-index properties on typed arrays.
    s && (u == "buffer" || u == "byteLength" || u == "byteOffset") || // Skip index properties.
    _P(u, c))) && l.push(u);
  return l;
}
var c1 = Object.prototype;
function hc(e) {
  var t = e && e.constructor, n = typeof t == "function" && t.prototype || c1;
  return e === n;
}
var u1 = Im(Object.keys, Object), d1 = Object.prototype, p1 = d1.hasOwnProperty;
function f1(e) {
  if (!hc(e))
    return u1(e);
  var t = [];
  for (var n in Object(e))
    p1.call(e, n) && n != "constructor" && t.push(n);
  return t;
}
function qm(e) {
  return e != null && Wm(e.length) && !Am(e);
}
function bc(e) {
  return qm(e) ? Hm(e) : f1(e);
}
function m1(e, t) {
  return e && Ys(t, bc(t), e);
}
function h1(e) {
  var t = [];
  if (e != null)
    for (var n in Object(e))
      t.push(n);
  return t;
}
var b1 = Object.prototype, g1 = b1.hasOwnProperty;
function y1(e) {
  if (!va(e))
    return h1(e);
  var t = hc(e), n = [];
  for (var o in e)
    o == "constructor" && (t || !g1.call(e, o)) || n.push(o);
  return n;
}
function gc(e) {
  return qm(e) ? Hm(e, !0) : y1(e);
}
function v1(e, t) {
  return e && Ys(t, gc(t), e);
}
var Ym = typeof exports == "object" && exports && !exports.nodeType && exports, Ad = Ym && typeof module == "object" && module && !module.nodeType && module, x1 = Ad && Ad.exports === Ym, Fd = x1 ? mn.Buffer : void 0, Vd = Fd ? Fd.allocUnsafe : void 0;
function T1(e, t) {
  if (t)
    return e.slice();
  var n = e.length, o = Vd ? Vd(n) : new e.constructor(n);
  return e.copy(o), o;
}
function Km(e, t) {
  var n = -1, o = e.length;
  for (t || (t = Array(o)); ++n < o; )
    t[n] = e[n];
  return t;
}
function w1(e, t) {
  for (var n = -1, o = e == null ? 0 : e.length, a = 0, s = []; ++n < o; ) {
    var i = e[n];
    t(i, n, e) && (s[a++] = i);
  }
  return s;
}
function Gm() {
  return [];
}
var E1 = Object.prototype, C1 = E1.propertyIsEnumerable, Ld = Object.getOwnPropertySymbols, yc = Ld ? function(e) {
  return e == null ? [] : (e = Object(e), w1(Ld(e), function(t) {
    return C1.call(e, t);
  }));
} : Gm;
function O1(e, t) {
  return Ys(e, yc(e), t);
}
function Xm(e, t) {
  for (var n = -1, o = t.length, a = e.length; ++n < o; )
    e[a + n] = t[n];
  return e;
}
var S1 = Object.getOwnPropertySymbols, Zm = S1 ? function(e) {
  for (var t = []; e; )
    Xm(t, yc(e)), e = fc(e);
  return t;
} : Gm;
function P1(e, t) {
  return Ys(e, Zm(e), t);
}
function Jm(e, t, n) {
  var o = t(e);
  return xa(e) ? o : Xm(o, n(e));
}
function R1(e) {
  return Jm(e, bc, yc);
}
function D1(e) {
  return Jm(e, gc, Zm);
}
var rl = vr(mn, "DataView"), ol = vr(mn, "Promise"), al = vr(mn, "Set"), sl = vr(mn, "WeakMap"), Bd = "[object Map]", $1 = "[object Object]", zd = "[object Promise]", Wd = "[object Set]", Ud = "[object WeakMap]", Hd = "[object DataView]", k1 = yr(rl), _1 = yr(ea), M1 = yr(ol), I1 = yr(al), N1 = yr(sl), yn = br;
(rl && yn(new rl(new ArrayBuffer(1))) != Hd || ea && yn(new ea()) != Bd || ol && yn(ol.resolve()) != zd || al && yn(new al()) != Wd || sl && yn(new sl()) != Ud) && (yn = function(e) {
  var t = br(e), n = t == $1 ? e.constructor : void 0, o = n ? yr(n) : "";
  if (o)
    switch (o) {
      case k1:
        return Hd;
      case _1:
        return Bd;
      case M1:
        return zd;
      case I1:
        return Wd;
      case N1:
        return Ud;
    }
  return t;
});
var j1 = Object.prototype, A1 = j1.hasOwnProperty;
function F1(e) {
  var t = e.length, n = new e.constructor(t);
  return t && typeof e[0] == "string" && A1.call(e, "index") && (n.index = e.index, n.input = e.input), n;
}
var qd = mn.Uint8Array;
function vc(e) {
  var t = new e.constructor(e.byteLength);
  return new qd(t).set(new qd(e)), t;
}
function V1(e, t) {
  var n = t ? vc(e.buffer) : e.buffer;
  return new e.constructor(n, e.byteOffset, e.byteLength);
}
var L1 = /\w*$/;
function B1(e) {
  var t = new e.constructor(e.source, L1.exec(e));
  return t.lastIndex = e.lastIndex, t;
}
var Yd = Vn ? Vn.prototype : void 0, Kd = Yd ? Yd.valueOf : void 0;
function z1(e) {
  return Kd ? Object(Kd.call(e)) : {};
}
function W1(e, t) {
  var n = t ? vc(e.buffer) : e.buffer;
  return new e.constructor(n, e.byteOffset, e.length);
}
var U1 = "[object Boolean]", H1 = "[object Date]", q1 = "[object Map]", Y1 = "[object Number]", K1 = "[object RegExp]", G1 = "[object Set]", X1 = "[object String]", Z1 = "[object Symbol]", J1 = "[object ArrayBuffer]", Q1 = "[object DataView]", eR = "[object Float32Array]", tR = "[object Float64Array]", nR = "[object Int8Array]", rR = "[object Int16Array]", oR = "[object Int32Array]", aR = "[object Uint8Array]", sR = "[object Uint8ClampedArray]", iR = "[object Uint16Array]", lR = "[object Uint32Array]";
function cR(e, t, n) {
  var o = e.constructor;
  switch (t) {
    case J1:
      return vc(e);
    case U1:
    case H1:
      return new o(+e);
    case Q1:
      return V1(e, n);
    case eR:
    case tR:
    case nR:
    case rR:
    case oR:
    case aR:
    case sR:
    case iR:
    case lR:
      return W1(e, n);
    case q1:
      return new o();
    case Y1:
    case X1:
      return new o(e);
    case K1:
      return B1(e);
    case G1:
      return new o();
    case Z1:
      return z1(e);
  }
}
var Gd = Object.create, uR = /* @__PURE__ */ function() {
  function e() {
  }
  return function(t) {
    if (!va(t))
      return {};
    if (Gd)
      return Gd(t);
    e.prototype = t;
    var n = new e();
    return e.prototype = void 0, n;
  };
}();
function dR(e) {
  return typeof e.constructor == "function" && !hc(e) ? uR(fc(e)) : {};
}
var pR = "[object Map]";
function fR(e) {
  return gr(e) && yn(e) == pR;
}
var Xd = oo && oo.isMap, mR = Xd ? mc(Xd) : fR, hR = "[object Set]";
function bR(e) {
  return gr(e) && yn(e) == hR;
}
var Zd = oo && oo.isSet, gR = Zd ? mc(Zd) : bR, yR = 1, vR = 2, xR = 4, Qm = "[object Arguments]", TR = "[object Array]", wR = "[object Boolean]", ER = "[object Date]", CR = "[object Error]", eh = "[object Function]", OR = "[object GeneratorFunction]", SR = "[object Map]", PR = "[object Number]", th = "[object Object]", RR = "[object RegExp]", DR = "[object Set]", $R = "[object String]", kR = "[object Symbol]", _R = "[object WeakMap]", MR = "[object ArrayBuffer]", IR = "[object DataView]", NR = "[object Float32Array]", jR = "[object Float64Array]", AR = "[object Int8Array]", FR = "[object Int16Array]", VR = "[object Int32Array]", LR = "[object Uint8Array]", BR = "[object Uint8ClampedArray]", zR = "[object Uint16Array]", WR = "[object Uint32Array]", Xe = {};
Xe[Qm] = Xe[TR] = Xe[MR] = Xe[IR] = Xe[wR] = Xe[ER] = Xe[NR] = Xe[jR] = Xe[AR] = Xe[FR] = Xe[VR] = Xe[SR] = Xe[PR] = Xe[th] = Xe[RR] = Xe[DR] = Xe[$R] = Xe[kR] = Xe[LR] = Xe[BR] = Xe[zR] = Xe[WR] = !0;
Xe[CR] = Xe[eh] = Xe[_R] = !1;
function Lo(e, t, n, o, a, s) {
  var i, l = t & yR, c = t & vR, u = t & xR;
  if (i !== void 0)
    return i;
  if (!va(e))
    return e;
  var d = xa(e);
  if (d) {
    if (i = F1(e), !l)
      return Km(e, i);
  } else {
    var f = yn(e), p = f == eh || f == OR;
    if (zm(e))
      return T1(e, l);
    if (f == th || f == Qm || p && !a) {
      if (i = c || p ? {} : dR(e), !l)
        return c ? P1(e, v1(i, e)) : O1(e, m1(i, e));
    } else {
      if (!Xe[f])
        return a ? e : {};
      i = cR(e, f, l);
    }
  }
  s || (s = new ho());
  var m = s.get(e);
  if (m)
    return m;
  s.set(e, i), gR(e) ? e.forEach(function(y) {
    i.add(Lo(y, t, n, y, e, s));
  }) : mR(e) && e.forEach(function(y, w) {
    i.set(w, Lo(y, t, n, w, e, s));
  });
  var v = u ? c ? D1 : R1 : c ? gc : bc, h = d ? void 0 : v(e);
  return vP(h || e, function(y, w) {
    h && (w = y, y = e[w]), Vm(i, w, Lo(y, t, n, w, e, s));
  }), i;
}
var UR = 1, HR = 4;
function ka(e) {
  return Lo(e, UR | HR);
}
var Jd = Array.isArray, Qd = Object.keys, qR = Object.prototype.hasOwnProperty, YR = typeof Element < "u";
function il(e, t) {
  if (e === t) return !0;
  if (e && t && typeof e == "object" && typeof t == "object") {
    var n = Jd(e), o = Jd(t), a, s, i;
    if (n && o) {
      if (s = e.length, s != t.length) return !1;
      for (a = s; a-- !== 0; )
        if (!il(e[a], t[a])) return !1;
      return !0;
    }
    if (n != o) return !1;
    var l = e instanceof Date, c = t instanceof Date;
    if (l != c) return !1;
    if (l && c) return e.getTime() == t.getTime();
    var u = e instanceof RegExp, d = t instanceof RegExp;
    if (u != d) return !1;
    if (u && d) return e.toString() == t.toString();
    var f = Qd(e);
    if (s = f.length, s !== Qd(t).length)
      return !1;
    for (a = s; a-- !== 0; )
      if (!qR.call(t, f[a])) return !1;
    if (YR && e instanceof Element && t instanceof Element)
      return e === t;
    for (a = s; a-- !== 0; )
      if (i = f[a], !(i === "_owner" && e.$$typeof) && !il(e[i], t[i]))
        return !1;
    return !0;
  }
  return e !== e && t !== t;
}
var KR = function(t, n) {
  try {
    return il(t, n);
  } catch (o) {
    if (o.message && o.message.match(/stack|recursion/i) || o.number === -2146828260)
      return console.warn("Warning: react-fast-compare does not handle circular references.", o.name, o.message), !1;
    throw o;
  }
};
const tr = /* @__PURE__ */ ms(KR);
var GR = process.env.NODE_ENV === "production";
function jn(e, t) {
  if (!GR) {
    var n = "Warning: " + t;
    typeof console < "u" && console.warn(n);
    try {
      throw Error(n);
    } catch {
    }
  }
}
var XR = 4;
function ep(e) {
  return Lo(e, XR);
}
function nh(e, t) {
  for (var n = -1, o = e == null ? 0 : e.length, a = Array(o); ++n < o; )
    a[n] = t(e[n], n, e);
  return a;
}
var ZR = "[object Symbol]";
function xc(e) {
  return typeof e == "symbol" || gr(e) && br(e) == ZR;
}
var JR = "Expected a function";
function Tc(e, t) {
  if (typeof e != "function" || t != null && typeof t != "function")
    throw new TypeError(JR);
  var n = function() {
    var o = arguments, a = t ? t.apply(this, o) : o[0], s = n.cache;
    if (s.has(a))
      return s.get(a);
    var i = e.apply(this, o);
    return n.cache = s.set(a, i) || s, i;
  };
  return n.cache = new (Tc.Cache || Un)(), n;
}
Tc.Cache = Un;
var QR = 500;
function eD(e) {
  var t = Tc(e, function(o) {
    return n.size === QR && n.clear(), o;
  }), n = t.cache;
  return t;
}
var tD = /[^.[\]]+|\[(?:(-?\d+(?:\.\d+)?)|(["'])((?:(?!\2)[^\\]|\\.)*?)\2)\]|(?=(?:\.|\[\])(?:\.|\[\]|$))/g, nD = /\\(\\)?/g, rD = eD(function(e) {
  var t = [];
  return e.charCodeAt(0) === 46 && t.push(""), e.replace(tD, function(n, o, a, s) {
    t.push(a ? s.replace(nD, "$1") : o || n);
  }), t;
}), oD = 1 / 0;
function aD(e) {
  if (typeof e == "string" || xc(e))
    return e;
  var t = e + "";
  return t == "0" && 1 / e == -oD ? "-0" : t;
}
var sD = 1 / 0, tp = Vn ? Vn.prototype : void 0, np = tp ? tp.toString : void 0;
function rh(e) {
  if (typeof e == "string")
    return e;
  if (xa(e))
    return nh(e, rh) + "";
  if (xc(e))
    return np ? np.call(e) : "";
  var t = e + "";
  return t == "0" && 1 / e == -sD ? "-0" : t;
}
function iD(e) {
  return e == null ? "" : rh(e);
}
function oh(e) {
  return xa(e) ? nh(e, aD) : xc(e) ? [e] : Km(rD(iD(e)));
}
function pt() {
  return pt = Object.assign || function(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = arguments[t];
      for (var o in n)
        Object.prototype.hasOwnProperty.call(n, o) && (e[o] = n[o]);
    }
    return e;
  }, pt.apply(this, arguments);
}
function ah(e, t) {
  if (e == null) return {};
  var n = {}, o = Object.keys(e), a, s;
  for (s = 0; s < o.length; s++)
    a = o[s], !(t.indexOf(a) >= 0) && (n[a] = e[a]);
  return n;
}
var Ks = /* @__PURE__ */ lb(void 0);
Ks.displayName = "FormikContext";
var lD = Ks.Provider;
Ks.Consumer;
function cD() {
  var e = Sp(Ks);
  return e || (process.env.NODE_ENV !== "production" ? jn(!1, "Formik context is undefined, please verify you are calling useFormikContext() as child of a <Formik> component.") : jn()), e;
}
var Vt = function(t) {
  return typeof t == "function";
}, Gs = function(t) {
  return t !== null && typeof t == "object";
}, uD = function(t) {
  return String(Math.floor(Number(t))) === t;
}, Si = function(t) {
  return Object.prototype.toString.call(t) === "[object String]";
}, dD = function(t) {
  return ml.count(t) === 0;
}, Pi = function(t) {
  return Gs(t) && Vt(t.then);
};
function pD(e) {
  if (e = e || (typeof document < "u" ? document : void 0), typeof e > "u")
    return null;
  try {
    return e.activeElement || e.body;
  } catch {
    return e.body;
  }
}
function Dt(e, t, n, o) {
  o === void 0 && (o = 0);
  for (var a = oh(t); e && o < a.length; )
    e = e[a[o++]];
  return o !== a.length && !e || e === void 0 ? n : e;
}
function sr(e, t, n) {
  for (var o = ep(e), a = o, s = 0, i = oh(t); s < i.length - 1; s++) {
    var l = i[s], c = Dt(e, i.slice(0, s + 1));
    if (c && (Gs(c) || Array.isArray(c)))
      a = a[l] = ep(c);
    else {
      var u = i[s + 1];
      a = a[l] = uD(u) && Number(u) >= 0 ? [] : {};
    }
  }
  return (s === 0 ? e : a)[i[s]] === n ? e : (n === void 0 ? delete a[i[s]] : a[i[s]] = n, s === 0 && n === void 0 && delete o[i[s]], o);
}
function sh(e, t, n, o) {
  n === void 0 && (n = /* @__PURE__ */ new WeakMap()), o === void 0 && (o = {});
  for (var a = 0, s = Object.keys(e); a < s.length; a++) {
    var i = s[a], l = e[i];
    Gs(l) ? n.get(l) || (n.set(l, !0), o[i] = Array.isArray(l) ? [] : {}, sh(l, t, n, o[i])) : o[i] = t;
  }
  return o;
}
function fD(e, t) {
  switch (t.type) {
    case "SET_VALUES":
      return pt({}, e, {
        values: t.payload
      });
    case "SET_TOUCHED":
      return pt({}, e, {
        touched: t.payload
      });
    case "SET_ERRORS":
      return tr(e.errors, t.payload) ? e : pt({}, e, {
        errors: t.payload
      });
    case "SET_STATUS":
      return pt({}, e, {
        status: t.payload
      });
    case "SET_ISSUBMITTING":
      return pt({}, e, {
        isSubmitting: t.payload
      });
    case "SET_ISVALIDATING":
      return pt({}, e, {
        isValidating: t.payload
      });
    case "SET_FIELD_VALUE":
      return pt({}, e, {
        values: sr(e.values, t.payload.field, t.payload.value)
      });
    case "SET_FIELD_TOUCHED":
      return pt({}, e, {
        touched: sr(e.touched, t.payload.field, t.payload.value)
      });
    case "SET_FIELD_ERROR":
      return pt({}, e, {
        errors: sr(e.errors, t.payload.field, t.payload.value)
      });
    case "RESET_FORM":
      return pt({}, e, t.payload);
    case "SET_FORMIK_STATE":
      return t.payload(e);
    case "SUBMIT_ATTEMPT":
      return pt({}, e, {
        touched: sh(e.values, !0),
        isSubmitting: !0,
        submitCount: e.submitCount + 1
      });
    case "SUBMIT_FAILURE":
      return pt({}, e, {
        isSubmitting: !1
      });
    case "SUBMIT_SUCCESS":
      return pt({}, e, {
        isSubmitting: !1
      });
    default:
      return e;
  }
}
var Zn = {}, _a = {};
function mD(e) {
  var t = e.validateOnChange, n = t === void 0 ? !0 : t, o = e.validateOnBlur, a = o === void 0 ? !0 : o, s = e.validateOnMount, i = s === void 0 ? !1 : s, l = e.isInitialValid, c = e.enableReinitialize, u = c === void 0 ? !1 : c, d = e.onSubmit, f = ah(e, ["validateOnChange", "validateOnBlur", "validateOnMount", "isInitialValid", "enableReinitialize", "onSubmit"]), p = pt({
    validateOnChange: n,
    validateOnBlur: a,
    validateOnMount: i,
    onSubmit: d
  }, f), m = bn(p.initialValues), v = bn(p.initialErrors || Zn), h = bn(p.initialTouched || _a), y = bn(p.initialStatus), w = bn(!1), C = bn({});
  process.env.NODE_ENV !== "production" && Bt(function() {
    typeof l > "u" || (process.env.NODE_ENV !== "production" ? jn(!1, "isInitialValid has been deprecated and will be removed in future versions of Formik. Please use initialErrors or validateOnMount instead.") : jn());
  }, []), Bt(function() {
    return w.current = !0, function() {
      w.current = !1;
    };
  }, []);
  var E = gn(0), O = E[1], T = bn({
    values: ka(p.initialValues),
    errors: ka(p.initialErrors) || Zn,
    touched: ka(p.initialTouched) || _a,
    status: ka(p.initialStatus),
    isSubmitting: !1,
    isValidating: !1,
    submitCount: 0
  }), P = T.current, S = nt(function(I) {
    var Q = T.current;
    T.current = fD(Q, I), Q !== T.current && O(function(ne) {
      return ne + 1;
    });
  }, []), j = nt(function(I, Q) {
    return new Promise(function(ne, ue) {
      var ge = p.validate(I, Q);
      ge == null ? ne(Zn) : Pi(ge) ? ge.then(function(ye) {
        ne(ye || Zn);
      }, function(ye) {
        process.env.NODE_ENV !== "production" && console.warn("Warning: An unhandled error was caught during validation in <Formik validate />", ye), ue(ye);
      }) : ne(ge);
    });
  }, [p.validate]), $ = nt(function(I, Q) {
    var ne = p.validationSchema, ue = Vt(ne) ? ne(Q) : ne, ge = Q && ue.validateAt ? ue.validateAt(Q, I) : gD(I, ue);
    return new Promise(function(ye, xe) {
      ge.then(function() {
        ye(Zn);
      }, function(be) {
        be.name === "ValidationError" ? ye(bD(be)) : (process.env.NODE_ENV !== "production" && console.warn("Warning: An unhandled error was caught during validation in <Formik validationSchema />", be), xe(be));
      });
    });
  }, [p.validationSchema]), V = nt(function(I, Q) {
    return new Promise(function(ne) {
      return ne(C.current[I].validate(Q));
    });
  }, []), _ = nt(function(I) {
    var Q = Object.keys(C.current).filter(function(ue) {
      return Vt(C.current[ue].validate);
    }), ne = Q.length > 0 ? Q.map(function(ue) {
      return V(ue, Dt(I, ue));
    }) : [Promise.resolve("DO_NOT_DELETE_YOU_WILL_BE_FIRED")];
    return Promise.all(ne).then(function(ue) {
      return ue.reduce(function(ge, ye, xe) {
        return ye === "DO_NOT_DELETE_YOU_WILL_BE_FIRED" || ye && (ge = sr(ge, Q[xe], ye)), ge;
      }, {});
    });
  }, [V]), L = nt(function(I) {
    return Promise.all([_(I), p.validationSchema ? $(I) : {}, p.validate ? j(I) : {}]).then(function(Q) {
      var ne = Q[0], ue = Q[1], ge = Q[2], ye = nl.all([ne, ue, ge], {
        arrayMerge: yD
      });
      return ye;
    });
  }, [p.validate, p.validationSchema, _, j, $]), M = At(function(I) {
    return I === void 0 && (I = P.values), S({
      type: "SET_ISVALIDATING",
      payload: !0
    }), L(I).then(function(Q) {
      return w.current && (S({
        type: "SET_ISVALIDATING",
        payload: !1
      }), S({
        type: "SET_ERRORS",
        payload: Q
      })), Q;
    });
  });
  Bt(function() {
    i && w.current === !0 && tr(m.current, p.initialValues) && M(m.current);
  }, [i, M]);
  var R = nt(function(I) {
    var Q = I && I.values ? I.values : m.current, ne = I && I.errors ? I.errors : v.current ? v.current : p.initialErrors || {}, ue = I && I.touched ? I.touched : h.current ? h.current : p.initialTouched || {}, ge = I && I.status ? I.status : y.current ? y.current : p.initialStatus;
    m.current = Q, v.current = ne, h.current = ue, y.current = ge;
    var ye = function() {
      S({
        type: "RESET_FORM",
        payload: {
          isSubmitting: !!I && !!I.isSubmitting,
          errors: ne,
          touched: ue,
          status: ge,
          values: Q,
          isValidating: !!I && !!I.isValidating,
          submitCount: I && I.submitCount && typeof I.submitCount == "number" ? I.submitCount : 0
        }
      });
    };
    if (p.onReset) {
      var xe = p.onReset(P.values, K);
      Pi(xe) ? xe.then(ye) : ye();
    } else
      ye();
  }, [p.initialErrors, p.initialStatus, p.initialTouched, p.onReset]);
  Bt(function() {
    w.current === !0 && !tr(m.current, p.initialValues) && u && (m.current = p.initialValues, R(), i && M(m.current));
  }, [u, p.initialValues, R, i, M]), Bt(function() {
    u && w.current === !0 && !tr(v.current, p.initialErrors) && (v.current = p.initialErrors || Zn, S({
      type: "SET_ERRORS",
      payload: p.initialErrors || Zn
    }));
  }, [u, p.initialErrors]), Bt(function() {
    u && w.current === !0 && !tr(h.current, p.initialTouched) && (h.current = p.initialTouched || _a, S({
      type: "SET_TOUCHED",
      payload: p.initialTouched || _a
    }));
  }, [u, p.initialTouched]), Bt(function() {
    u && w.current === !0 && !tr(y.current, p.initialStatus) && (y.current = p.initialStatus, S({
      type: "SET_STATUS",
      payload: p.initialStatus
    }));
  }, [u, p.initialStatus, p.initialTouched]);
  var D = At(function(I) {
    if (C.current[I] && Vt(C.current[I].validate)) {
      var Q = Dt(P.values, I), ne = C.current[I].validate(Q);
      return Pi(ne) ? (S({
        type: "SET_ISVALIDATING",
        payload: !0
      }), ne.then(function(ue) {
        return ue;
      }).then(function(ue) {
        S({
          type: "SET_FIELD_ERROR",
          payload: {
            field: I,
            value: ue
          }
        }), S({
          type: "SET_ISVALIDATING",
          payload: !1
        });
      })) : (S({
        type: "SET_FIELD_ERROR",
        payload: {
          field: I,
          value: ne
        }
      }), Promise.resolve(ne));
    } else if (p.validationSchema)
      return S({
        type: "SET_ISVALIDATING",
        payload: !0
      }), $(P.values, I).then(function(ue) {
        return ue;
      }).then(function(ue) {
        S({
          type: "SET_FIELD_ERROR",
          payload: {
            field: I,
            value: Dt(ue, I)
          }
        }), S({
          type: "SET_ISVALIDATING",
          payload: !1
        });
      });
    return Promise.resolve();
  }), F = nt(function(I, Q) {
    var ne = Q.validate;
    C.current[I] = {
      validate: ne
    };
  }, []), z = nt(function(I) {
    delete C.current[I];
  }, []), N = At(function(I, Q) {
    S({
      type: "SET_TOUCHED",
      payload: I
    });
    var ne = Q === void 0 ? a : Q;
    return ne ? M(P.values) : Promise.resolve();
  }), q = nt(function(I) {
    S({
      type: "SET_ERRORS",
      payload: I
    });
  }, []), A = At(function(I, Q) {
    var ne = Vt(I) ? I(P.values) : I;
    S({
      type: "SET_VALUES",
      payload: ne
    });
    var ue = Q === void 0 ? n : Q;
    return ue ? M(ne) : Promise.resolve();
  }), H = nt(function(I, Q) {
    S({
      type: "SET_FIELD_ERROR",
      payload: {
        field: I,
        value: Q
      }
    });
  }, []), te = At(function(I, Q, ne) {
    S({
      type: "SET_FIELD_VALUE",
      payload: {
        field: I,
        value: Q
      }
    });
    var ue = ne === void 0 ? n : ne;
    return ue ? M(sr(P.values, I, Q)) : Promise.resolve();
  }), re = nt(function(I, Q) {
    var ne = Q, ue = I, ge;
    if (!Si(I)) {
      I.persist && I.persist();
      var ye = I.target ? I.target : I.currentTarget, xe = ye.type, be = ye.name, _e = ye.id, st = ye.value, rt = ye.checked, Qe = ye.outerHTML, Te = ye.options, $e = ye.multiple;
      ne = Q || be || _e, !ne && process.env.NODE_ENV !== "production" && rp({
        htmlContent: Qe,
        documentationAnchorLink: "handlechange-e-reactchangeeventany--void",
        handlerName: "handleChange"
      }), ue = /number|range/.test(xe) ? (ge = parseFloat(st), isNaN(ge) ? "" : ge) : /checkbox/.test(xe) ? xD(Dt(P.values, ne), rt, st) : Te && $e ? vD(Te) : st;
    }
    ne && te(ne, ue);
  }, [te, P.values]), B = At(function(I) {
    if (Si(I))
      return function(Q) {
        return re(Q, I);
      };
    re(I);
  }), G = At(function(I, Q, ne) {
    Q === void 0 && (Q = !0), S({
      type: "SET_FIELD_TOUCHED",
      payload: {
        field: I,
        value: Q
      }
    });
    var ue = ne === void 0 ? a : ne;
    return ue ? M(P.values) : Promise.resolve();
  }), ee = nt(function(I, Q) {
    I.persist && I.persist();
    var ne = I.target, ue = ne.name, ge = ne.id, ye = ne.outerHTML, xe = Q || ue || ge;
    !xe && process.env.NODE_ENV !== "production" && rp({
      htmlContent: ye,
      documentationAnchorLink: "handleblur-e-any--void",
      handlerName: "handleBlur"
    }), G(xe, !0);
  }, [G]), W = At(function(I) {
    if (Si(I))
      return function(Q) {
        return ee(Q, I);
      };
    ee(I);
  }), J = nt(function(I) {
    Vt(I) ? S({
      type: "SET_FORMIK_STATE",
      payload: I
    }) : S({
      type: "SET_FORMIK_STATE",
      payload: function() {
        return I;
      }
    });
  }, []), se = nt(function(I) {
    S({
      type: "SET_STATUS",
      payload: I
    });
  }, []), le = nt(function(I) {
    S({
      type: "SET_ISSUBMITTING",
      payload: I
    });
  }, []), X = At(function() {
    return S({
      type: "SUBMIT_ATTEMPT"
    }), M().then(function(I) {
      var Q = I instanceof Error, ne = !Q && Object.keys(I).length === 0;
      if (ne) {
        var ue;
        try {
          if (ue = Y(), ue === void 0)
            return;
        } catch (ge) {
          throw ge;
        }
        return Promise.resolve(ue).then(function(ge) {
          return w.current && S({
            type: "SUBMIT_SUCCESS"
          }), ge;
        }).catch(function(ge) {
          if (w.current)
            throw S({
              type: "SUBMIT_FAILURE"
            }), ge;
        });
      } else if (w.current && (S({
        type: "SUBMIT_FAILURE"
      }), Q))
        throw I;
    });
  }), U = At(function(I) {
    if (I && I.preventDefault && Vt(I.preventDefault) && I.preventDefault(), I && I.stopPropagation && Vt(I.stopPropagation) && I.stopPropagation(), process.env.NODE_ENV !== "production" && typeof document < "u") {
      var Q = pD();
      Q !== null && Q instanceof HTMLButtonElement && (Q.attributes && Q.attributes.getNamedItem("type") || (process.env.NODE_ENV !== "production" ? jn(!1, 'You submitted a Formik form using a button with an unspecified `type` attribute.  Most browsers default button elements to `type="submit"`. If this is not a submit button, please add `type="button"`.') : jn()));
    }
    X().catch(function(ne) {
      console.warn("Warning: An unhandled error was caught from submitForm()", ne);
    });
  }), K = {
    resetForm: R,
    validateForm: M,
    validateField: D,
    setErrors: q,
    setFieldError: H,
    setFieldTouched: G,
    setFieldValue: te,
    setStatus: se,
    setSubmitting: le,
    setTouched: N,
    setValues: A,
    setFormikState: J,
    submitForm: X
  }, Y = At(function() {
    return d(P.values, K);
  }), he = At(function(I) {
    I && I.preventDefault && Vt(I.preventDefault) && I.preventDefault(), I && I.stopPropagation && Vt(I.stopPropagation) && I.stopPropagation(), R();
  }), Oe = nt(function(I) {
    return {
      value: Dt(P.values, I),
      error: Dt(P.errors, I),
      touched: !!Dt(P.touched, I),
      initialValue: Dt(m.current, I),
      initialTouched: !!Dt(h.current, I),
      initialError: Dt(v.current, I)
    };
  }, [P.errors, P.touched, P.values]), Ne = nt(function(I) {
    return {
      setValue: function(ne, ue) {
        return te(I, ne, ue);
      },
      setTouched: function(ne, ue) {
        return G(I, ne, ue);
      },
      setError: function(ne) {
        return H(I, ne);
      }
    };
  }, [te, G, H]), fe = nt(function(I) {
    var Q = Gs(I), ne = Q ? I.name : I, ue = Dt(P.values, ne), ge = {
      name: ne,
      value: ue,
      onChange: B,
      onBlur: W
    };
    if (Q) {
      var ye = I.type, xe = I.value, be = I.as, _e = I.multiple;
      ye === "checkbox" ? xe === void 0 ? ge.checked = !!ue : (ge.checked = !!(Array.isArray(ue) && ~ue.indexOf(xe)), ge.value = xe) : ye === "radio" ? (ge.checked = ue === xe, ge.value = xe) : be === "select" && _e && (ge.value = ge.value || [], ge.multiple = !0);
    }
    return ge;
  }, [W, B, P.values]), ve = Ya(function() {
    return !tr(m.current, P.values);
  }, [m.current, P.values]), oe = Ya(function() {
    return typeof l < "u" ? ve ? P.errors && Object.keys(P.errors).length === 0 : l !== !1 && Vt(l) ? l(p) : l : P.errors && Object.keys(P.errors).length === 0;
  }, [l, ve, P.errors, p]), ce = pt({}, P, {
    initialValues: m.current,
    initialErrors: v.current,
    initialTouched: h.current,
    initialStatus: y.current,
    handleBlur: W,
    handleChange: B,
    handleReset: he,
    handleSubmit: U,
    resetForm: R,
    setErrors: q,
    setFormikState: J,
    setFieldTouched: G,
    setFieldValue: te,
    setFieldError: H,
    setStatus: se,
    setSubmitting: le,
    setTouched: N,
    setValues: A,
    submitForm: X,
    validateForm: M,
    validateField: D,
    isValid: oe,
    dirty: ve,
    unregisterField: z,
    registerField: F,
    getFieldProps: fe,
    getFieldMeta: Oe,
    getFieldHelpers: Ne,
    validateOnBlur: a,
    validateOnChange: n,
    validateOnMount: i
  });
  return ce;
}
function hD(e) {
  var t = mD(e), n = e.component, o = e.children, a = e.render, s = e.innerRef;
  return ib(s, function() {
    return t;
  }), process.env.NODE_ENV !== "production" && Bt(function() {
    e.render && (process.env.NODE_ENV !== "production" ? jn(!1, "<Formik render> has been deprecated and will be removed in future versions of Formik. Please use a child callback function instead. To get rid of this warning, replace <Formik render={(props) => ...} /> with <Formik>{(props) => ...}</Formik>") : jn());
  }, []), _i(lD, {
    value: t
  }, n ? _i(n, t) : a ? a(t) : o ? Vt(o) ? o(t) : dD(o) ? null : ml.only(o) : null);
}
function rp(e) {
  var t = e.htmlContent, n = e.documentationAnchorLink, o = e.handlerName;
  console.warn("Warning: Formik called `" + o + "`, but you forgot to pass an `id` or `name` attribute to your input:\n    " + t + `
    Formik cannot determine which value to update. For more info see https://formik.org/docs/api/formik#` + n + `
  `);
}
function bD(e) {
  var t = {};
  if (e.inner) {
    if (e.inner.length === 0)
      return sr(t, e.path, e.message);
    for (var a = e.inner, n = Array.isArray(a), o = 0, a = n ? a : a[Symbol.iterator](); ; ) {
      var s;
      if (n) {
        if (o >= a.length) break;
        s = a[o++];
      } else {
        if (o = a.next(), o.done) break;
        s = o.value;
      }
      var i = s;
      Dt(t, i.path) || (t = sr(t, i.path, i.message));
    }
  }
  return t;
}
function gD(e, t, n, o) {
  n === void 0 && (n = !1);
  var a = ll(e);
  return t[n ? "validateSync" : "validate"](a, {
    abortEarly: !1,
    context: a
  });
}
function ll(e) {
  var t = Array.isArray(e) ? [] : {};
  for (var n in e)
    if (Object.prototype.hasOwnProperty.call(e, n)) {
      var o = String(n);
      Array.isArray(e[o]) === !0 ? t[o] = e[o].map(function(a) {
        return Array.isArray(a) === !0 || $d(a) ? ll(a) : a !== "" ? a : void 0;
      }) : $d(e[o]) ? t[o] = ll(e[o]) : t[o] = e[o] !== "" ? e[o] : void 0;
    }
  return t;
}
function yD(e, t, n) {
  var o = e.slice();
  return t.forEach(function(s, i) {
    if (typeof o[i] > "u") {
      var l = n.clone !== !1, c = l && n.isMergeableObject(s);
      o[i] = c ? nl(Array.isArray(s) ? [] : {}, s, n) : s;
    } else n.isMergeableObject(s) ? o[i] = nl(e[i], s, n) : e.indexOf(s) === -1 && o.push(s);
  }), o;
}
function vD(e) {
  return Array.from(e).filter(function(t) {
    return t.selected;
  }).map(function(t) {
    return t.value;
  });
}
function xD(e, t, n) {
  if (typeof e == "boolean")
    return !!t;
  var o = [], a = !1, s = -1;
  if (Array.isArray(e))
    o = e, s = e.indexOf(n), a = s >= 0;
  else if (!n || n == "true" || n == "false")
    return !!t;
  return t && n && !a ? o.concat(n) : a ? o.slice(0, s).concat(o.slice(s + 1)) : o;
}
var TD = typeof window < "u" && typeof window.document < "u" && typeof window.document.createElement < "u" ? cb : Bt;
function At(e) {
  var t = bn(e);
  return TD(function() {
    t.current = e;
  }), nt(function() {
    for (var n = arguments.length, o = new Array(n), a = 0; a < n; a++)
      o[a] = arguments[a];
    return t.current.apply(void 0, o);
  }, []);
}
var ih = /* @__PURE__ */ Op(function(e, t) {
  var n = e.action, o = ah(e, ["action"]), a = n ?? "#", s = cD(), i = s.handleReset, l = s.handleSubmit;
  return _i("form", pt({
    onSubmit: l,
    ref: t,
    onReset: i,
    action: a
  }, o));
});
ih.displayName = "Form";
function xr(e) {
  this._maxSize = e, this.clear();
}
xr.prototype.clear = function() {
  this._size = 0, this._values = /* @__PURE__ */ Object.create(null);
};
xr.prototype.get = function(e) {
  return this._values[e];
};
xr.prototype.set = function(e, t) {
  return this._size >= this._maxSize && this.clear(), e in this._values || this._size++, this._values[e] = t;
};
var wD = /[^.^\]^[]+|(?=\[\]|\.\.)/g, lh = /^\d+$/, ED = /^\d/, CD = /[~`!#$%\^&*+=\-\[\]\\';,/{}|\\":<>\?]/g, OD = /^\s*(['"]?)(.*?)(\1)\s*$/, wc = 512, op = new xr(wc), ap = new xr(wc), sp = new xr(wc), ir = {
  Cache: xr,
  split: cl,
  normalizePath: Ri,
  setter: function(e) {
    var t = Ri(e);
    return ap.get(e) || ap.set(e, function(o, a) {
      for (var s = 0, i = t.length, l = o; s < i - 1; ) {
        var c = t[s];
        if (c === "__proto__" || c === "constructor" || c === "prototype")
          return o;
        l = l[t[s++]];
      }
      l[t[s]] = a;
    });
  },
  getter: function(e, t) {
    var n = Ri(e);
    return sp.get(e) || sp.set(e, function(a) {
      for (var s = 0, i = n.length; s < i; )
        if (a != null || !t) a = a[n[s++]];
        else return;
      return a;
    });
  },
  join: function(e) {
    return e.reduce(function(t, n) {
      return t + (Ec(n) || lh.test(n) ? "[" + n + "]" : (t ? "." : "") + n);
    }, "");
  },
  forEach: function(e, t, n) {
    SD(Array.isArray(e) ? e : cl(e), t, n);
  }
};
function Ri(e) {
  return op.get(e) || op.set(
    e,
    cl(e).map(function(t) {
      return t.replace(OD, "$2");
    })
  );
}
function cl(e) {
  return e.match(wD) || [""];
}
function SD(e, t, n) {
  var o = e.length, a, s, i, l;
  for (s = 0; s < o; s++)
    a = e[s], a && (DD(a) && (a = '"' + a + '"'), l = Ec(a), i = !l && /^\d+$/.test(a), t.call(n, a, l, i, s, e));
}
function Ec(e) {
  return typeof e == "string" && e && ["'", '"'].indexOf(e.charAt(0)) !== -1;
}
function PD(e) {
  return e.match(ED) && !e.match(lh);
}
function RD(e) {
  return CD.test(e);
}
function DD(e) {
  return !Ec(e) && (PD(e) || RD(e));
}
const $D = /[A-Z\xc0-\xd6\xd8-\xde]?[a-z\xdf-\xf6\xf8-\xff]+(?:['’](?:d|ll|m|re|s|t|ve))?(?=[\xac\xb1\xd7\xf7\x00-\x2f\x3a-\x40\x5b-\x60\x7b-\xbf\u2000-\u206f \t\x0b\f\xa0\ufeff\n\r\u2028\u2029\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000]|[A-Z\xc0-\xd6\xd8-\xde]|$)|(?:[A-Z\xc0-\xd6\xd8-\xde]|[^\ud800-\udfff\xac\xb1\xd7\xf7\x00-\x2f\x3a-\x40\x5b-\x60\x7b-\xbf\u2000-\u206f \t\x0b\f\xa0\ufeff\n\r\u2028\u2029\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\d+\u2700-\u27bfa-z\xdf-\xf6\xf8-\xffA-Z\xc0-\xd6\xd8-\xde])+(?:['’](?:D|LL|M|RE|S|T|VE))?(?=[\xac\xb1\xd7\xf7\x00-\x2f\x3a-\x40\x5b-\x60\x7b-\xbf\u2000-\u206f \t\x0b\f\xa0\ufeff\n\r\u2028\u2029\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000]|[A-Z\xc0-\xd6\xd8-\xde](?:[a-z\xdf-\xf6\xf8-\xff]|[^\ud800-\udfff\xac\xb1\xd7\xf7\x00-\x2f\x3a-\x40\x5b-\x60\x7b-\xbf\u2000-\u206f \t\x0b\f\xa0\ufeff\n\r\u2028\u2029\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\d+\u2700-\u27bfa-z\xdf-\xf6\xf8-\xffA-Z\xc0-\xd6\xd8-\xde])|$)|[A-Z\xc0-\xd6\xd8-\xde]?(?:[a-z\xdf-\xf6\xf8-\xff]|[^\ud800-\udfff\xac\xb1\xd7\xf7\x00-\x2f\x3a-\x40\x5b-\x60\x7b-\xbf\u2000-\u206f \t\x0b\f\xa0\ufeff\n\r\u2028\u2029\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\d+\u2700-\u27bfa-z\xdf-\xf6\xf8-\xffA-Z\xc0-\xd6\xd8-\xde])+(?:['’](?:d|ll|m|re|s|t|ve))?|[A-Z\xc0-\xd6\xd8-\xde]+(?:['’](?:D|LL|M|RE|S|T|VE))?|\d*(?:1ST|2ND|3RD|(?![123])\dTH)(?=\b|[a-z_])|\d*(?:1st|2nd|3rd|(?![123])\dth)(?=\b|[A-Z_])|\d+|(?:[\u2700-\u27bf]|(?:\ud83c[\udde6-\uddff]){2}|[\ud800-\udbff][\udc00-\udfff])[\ufe0e\ufe0f]?(?:[\u0300-\u036f\ufe20-\ufe2f\u20d0-\u20ff]|\ud83c[\udffb-\udfff])?(?:\u200d(?:[^\ud800-\udfff]|(?:\ud83c[\udde6-\uddff]){2}|[\ud800-\udbff][\udc00-\udfff])[\ufe0e\ufe0f]?(?:[\u0300-\u036f\ufe20-\ufe2f\u20d0-\u20ff]|\ud83c[\udffb-\udfff])?)*/g, Xs = (e) => e.match($D) || [], Zs = (e) => e[0].toUpperCase() + e.slice(1), Cc = (e, t) => Xs(e).join(t).toLowerCase(), ch = (e) => Xs(e).reduce(
  (t, n) => `${t}${t ? n[0].toUpperCase() + n.slice(1).toLowerCase() : n.toLowerCase()}`,
  ""
), kD = (e) => Zs(ch(e)), _D = (e) => Cc(e, "_"), MD = (e) => Cc(e, "-"), ID = (e) => Zs(Cc(e, " ")), ND = (e) => Xs(e).map(Zs).join(" ");
var Di = {
  words: Xs,
  upperFirst: Zs,
  camelCase: ch,
  pascalCase: kD,
  snakeCase: _D,
  kebabCase: MD,
  sentenceCase: ID,
  titleCase: ND
}, Oc = { exports: {} };
Oc.exports = function(e) {
  return uh(jD(e), e);
};
Oc.exports.array = uh;
function uh(e, t) {
  var n = e.length, o = new Array(n), a = {}, s = n, i = AD(t), l = FD(e);
  for (t.forEach(function(u) {
    if (!l.has(u[0]) || !l.has(u[1]))
      throw new Error("Unknown node. There is an unknown node in the supplied edges.");
  }); s--; )
    a[s] || c(e[s], s, /* @__PURE__ */ new Set());
  return o;
  function c(u, d, f) {
    if (f.has(u)) {
      var p;
      try {
        p = ", node was:" + JSON.stringify(u);
      } catch {
        p = "";
      }
      throw new Error("Cyclic dependency" + p);
    }
    if (!l.has(u))
      throw new Error("Found unknown node. Make sure to provided all involved nodes. Unknown node: " + JSON.stringify(u));
    if (!a[d]) {
      a[d] = !0;
      var m = i.get(u) || /* @__PURE__ */ new Set();
      if (m = Array.from(m), d = m.length) {
        f.add(u);
        do {
          var v = m[--d];
          c(v, l.get(v), f);
        } while (d);
        f.delete(u);
      }
      o[--n] = u;
    }
  }
}
function jD(e) {
  for (var t = /* @__PURE__ */ new Set(), n = 0, o = e.length; n < o; n++) {
    var a = e[n];
    t.add(a[0]), t.add(a[1]);
  }
  return Array.from(t);
}
function AD(e) {
  for (var t = /* @__PURE__ */ new Map(), n = 0, o = e.length; n < o; n++) {
    var a = e[n];
    t.has(a[0]) || t.set(a[0], /* @__PURE__ */ new Set()), t.has(a[1]) || t.set(a[1], /* @__PURE__ */ new Set()), t.get(a[0]).add(a[1]);
  }
  return t;
}
function FD(e) {
  for (var t = /* @__PURE__ */ new Map(), n = 0, o = e.length; n < o; n++)
    t.set(e[n], n);
  return t;
}
var VD = Oc.exports;
const LD = /* @__PURE__ */ ms(VD), BD = Object.prototype.toString, zD = Error.prototype.toString, WD = RegExp.prototype.toString, UD = typeof Symbol < "u" ? Symbol.prototype.toString : () => "", HD = /^Symbol\((.*)\)(.*)$/;
function qD(e) {
  return e != +e ? "NaN" : e === 0 && 1 / e < 0 ? "-0" : "" + e;
}
function ip(e, t = !1) {
  if (e == null || e === !0 || e === !1) return "" + e;
  const n = typeof e;
  if (n === "number") return qD(e);
  if (n === "string") return t ? `"${e}"` : e;
  if (n === "function") return "[Function " + (e.name || "anonymous") + "]";
  if (n === "symbol") return UD.call(e).replace(HD, "Symbol($1)");
  const o = BD.call(e).slice(8, -1);
  return o === "Date" ? isNaN(e.getTime()) ? "" + e : e.toISOString(e) : o === "Error" || e instanceof Error ? "[" + zD.call(e) + "]" : o === "RegExp" ? WD.call(e) : null;
}
function An(e, t) {
  let n = ip(e, t);
  return n !== null ? n : JSON.stringify(e, function(o, a) {
    let s = ip(this[o], t);
    return s !== null ? s : a;
  }, 2);
}
function dh(e) {
  return e == null ? [] : [].concat(e);
}
let ph, fh, mh, YD = /\$\{\s*(\w+)\s*\}/g;
ph = Symbol.toStringTag;
class lp {
  constructor(t, n, o, a) {
    this.name = void 0, this.message = void 0, this.value = void 0, this.path = void 0, this.type = void 0, this.params = void 0, this.errors = void 0, this.inner = void 0, this[ph] = "Error", this.name = "ValidationError", this.value = n, this.path = o, this.type = a, this.errors = [], this.inner = [], dh(t).forEach((s) => {
      if (Et.isError(s)) {
        this.errors.push(...s.errors);
        const i = s.inner.length ? s.inner : [s];
        this.inner.push(...i);
      } else
        this.errors.push(s);
    }), this.message = this.errors.length > 1 ? `${this.errors.length} errors occurred` : this.errors[0];
  }
}
fh = Symbol.hasInstance;
mh = Symbol.toStringTag;
class Et extends Error {
  static formatError(t, n) {
    const o = n.label || n.path || "this";
    return o !== n.path && (n = Object.assign({}, n, {
      path: o
    })), typeof t == "string" ? t.replace(YD, (a, s) => An(n[s])) : typeof t == "function" ? t(n) : t;
  }
  static isError(t) {
    return t && t.name === "ValidationError";
  }
  constructor(t, n, o, a, s) {
    const i = new lp(t, n, o, a);
    if (s)
      return i;
    super(), this.value = void 0, this.path = void 0, this.type = void 0, this.params = void 0, this.errors = [], this.inner = [], this[mh] = "Error", this.name = i.name, this.message = i.message, this.type = i.type, this.value = i.value, this.path = i.path, this.errors = i.errors, this.inner = i.inner, Error.captureStackTrace && Error.captureStackTrace(this, Et);
  }
  static [fh](t) {
    return lp[Symbol.hasInstance](t) || super[Symbol.hasInstance](t);
  }
}
let sn = {
  default: "${path} is invalid",
  required: "${path} is a required field",
  defined: "${path} must be defined",
  notNull: "${path} cannot be null",
  oneOf: "${path} must be one of the following values: ${values}",
  notOneOf: "${path} must not be one of the following values: ${values}",
  notType: ({
    path: e,
    type: t,
    value: n,
    originalValue: o
  }) => {
    const a = o != null && o !== n ? ` (cast from the value \`${An(o, !0)}\`).` : ".";
    return t !== "mixed" ? `${e} must be a \`${t}\` type, but the final value was: \`${An(n, !0)}\`` + a : `${e} must match the configured type. The validated value was: \`${An(n, !0)}\`` + a;
  }
}, wt = {
  length: "${path} must be exactly ${length} characters",
  min: "${path} must be at least ${min} characters",
  max: "${path} must be at most ${max} characters",
  matches: '${path} must match the following: "${regex}"',
  email: "${path} must be a valid email",
  url: "${path} must be a valid URL",
  uuid: "${path} must be a valid UUID",
  datetime: "${path} must be a valid ISO date-time",
  datetime_precision: "${path} must be a valid ISO date-time with a sub-second precision of exactly ${precision} digits",
  datetime_offset: '${path} must be a valid ISO date-time with UTC "Z" timezone',
  trim: "${path} must be a trimmed string",
  lowercase: "${path} must be a lowercase string",
  uppercase: "${path} must be a upper case string"
}, KD = {
  min: "${path} must be greater than or equal to ${min}",
  max: "${path} must be less than or equal to ${max}",
  lessThan: "${path} must be less than ${less}",
  moreThan: "${path} must be greater than ${more}",
  positive: "${path} must be a positive number",
  negative: "${path} must be a negative number",
  integer: "${path} must be an integer"
}, ul = {
  min: "${path} field must be later than ${min}",
  max: "${path} field must be at earlier than ${max}"
}, GD = {
  isValue: "${path} field must be ${value}"
}, dl = {
  noUnknown: "${path} field has unspecified keys: ${unknown}"
}, XD = {
  min: "${path} field must have at least ${min} items",
  max: "${path} field must have less than or equal to ${max} items",
  length: "${path} must have ${length} items"
}, ZD = {
  notType: (e) => {
    const {
      path: t,
      value: n,
      spec: o
    } = e, a = o.types.length;
    if (Array.isArray(n)) {
      if (n.length < a) return `${t} tuple value has too few items, expected a length of ${a} but got ${n.length} for value: \`${An(n, !0)}\``;
      if (n.length > a) return `${t} tuple value has too many items, expected a length of ${a} but got ${n.length} for value: \`${An(n, !0)}\``;
    }
    return Et.formatError(sn.notType, e);
  }
};
Object.assign(/* @__PURE__ */ Object.create(null), {
  mixed: sn,
  string: wt,
  number: KD,
  date: ul,
  object: dl,
  array: XD,
  boolean: GD,
  tuple: ZD
});
const Sc = (e) => e && e.__isYupSchema__;
class cs {
  static fromOptions(t, n) {
    if (!n.then && !n.otherwise) throw new TypeError("either `then:` or `otherwise:` is required for `when()` conditions");
    let {
      is: o,
      then: a,
      otherwise: s
    } = n, i = typeof o == "function" ? o : (...l) => l.every((c) => c === o);
    return new cs(t, (l, c) => {
      var u;
      let d = i(...l) ? a : s;
      return (u = d == null ? void 0 : d(c)) != null ? u : c;
    });
  }
  constructor(t, n) {
    this.fn = void 0, this.refs = t, this.refs = t, this.fn = n;
  }
  resolve(t, n) {
    let o = this.refs.map((s) => (
      // TODO: ? operator here?
      s.getValue(n == null ? void 0 : n.value, n == null ? void 0 : n.parent, n == null ? void 0 : n.context)
    )), a = this.fn(o, t, n);
    if (a === void 0 || // @ts-ignore this can be base
    a === t)
      return t;
    if (!Sc(a)) throw new TypeError("conditions must return a schema object");
    return a.resolve(n);
  }
}
const Ma = {
  context: "$",
  value: "."
};
class Tr {
  constructor(t, n = {}) {
    if (this.key = void 0, this.isContext = void 0, this.isValue = void 0, this.isSibling = void 0, this.path = void 0, this.getter = void 0, this.map = void 0, typeof t != "string") throw new TypeError("ref must be a string, got: " + t);
    if (this.key = t.trim(), t === "") throw new TypeError("ref must be a non-empty string");
    this.isContext = this.key[0] === Ma.context, this.isValue = this.key[0] === Ma.value, this.isSibling = !this.isContext && !this.isValue;
    let o = this.isContext ? Ma.context : this.isValue ? Ma.value : "";
    this.path = this.key.slice(o.length), this.getter = this.path && ir.getter(this.path, !0), this.map = n.map;
  }
  getValue(t, n, o) {
    let a = this.isContext ? o : this.isValue ? t : n;
    return this.getter && (a = this.getter(a || {})), this.map && (a = this.map(a)), a;
  }
  /**
   *
   * @param {*} value
   * @param {Object} options
   * @param {Object=} options.context
   * @param {Object=} options.parent
   */
  cast(t, n) {
    return this.getValue(t, n == null ? void 0 : n.parent, n == null ? void 0 : n.context);
  }
  resolve() {
    return this;
  }
  describe() {
    return {
      type: "ref",
      key: this.key
    };
  }
  toString() {
    return `Ref(${this.key})`;
  }
  static isRef(t) {
    return t && t.__isYupRef;
  }
}
Tr.prototype.__isYupRef = !0;
const or = (e) => e == null;
function _r(e) {
  function t({
    value: n,
    path: o = "",
    options: a,
    originalValue: s,
    schema: i
  }, l, c) {
    const {
      name: u,
      test: d,
      params: f,
      message: p,
      skipAbsent: m
    } = e;
    let {
      parent: v,
      context: h,
      abortEarly: y = i.spec.abortEarly,
      disableStackTrace: w = i.spec.disableStackTrace
    } = a;
    function C(_) {
      return Tr.isRef(_) ? _.getValue(n, v, h) : _;
    }
    function E(_ = {}) {
      const L = Object.assign({
        value: n,
        originalValue: s,
        label: i.spec.label,
        path: _.path || o,
        spec: i.spec,
        disableStackTrace: _.disableStackTrace || w
      }, f, _.params);
      for (const R of Object.keys(L)) L[R] = C(L[R]);
      const M = new Et(Et.formatError(_.message || p, L), n, L.path, _.type || u, L.disableStackTrace);
      return M.params = L, M;
    }
    const O = y ? l : c;
    let T = {
      path: o,
      parent: v,
      type: u,
      from: a.from,
      createError: E,
      resolve: C,
      options: a,
      originalValue: s,
      schema: i
    };
    const P = (_) => {
      Et.isError(_) ? O(_) : _ ? c(null) : O(E());
    }, S = (_) => {
      Et.isError(_) ? O(_) : l(_);
    };
    if (m && or(n))
      return P(!0);
    let $;
    try {
      var V;
      if ($ = d.call(T, n, T), typeof ((V = $) == null ? void 0 : V.then) == "function") {
        if (a.sync)
          throw new Error(`Validation test of type: "${T.type}" returned a Promise during a synchronous validate. This test will finish after the validate call has returned`);
        return Promise.resolve($).then(P, S);
      }
    } catch (_) {
      S(_);
      return;
    }
    P($);
  }
  return t.OPTIONS = e, t;
}
function JD(e, t, n, o = n) {
  let a, s, i;
  return t ? (ir.forEach(t, (l, c, u) => {
    let d = c ? l.slice(1, l.length - 1) : l;
    e = e.resolve({
      context: o,
      parent: a,
      value: n
    });
    let f = e.type === "tuple", p = u ? parseInt(d, 10) : 0;
    if (e.innerType || f) {
      if (f && !u) throw new Error(`Yup.reach cannot implicitly index into a tuple type. the path part "${i}" must contain an index to the tuple element, e.g. "${i}[0]"`);
      if (n && p >= n.length)
        throw new Error(`Yup.reach cannot resolve an array item at index: ${l}, in the path: ${t}. because there is no value at that index. `);
      a = n, n = n && n[p], e = f ? e.spec.types[p] : e.innerType;
    }
    if (!u) {
      if (!e.fields || !e.fields[d]) throw new Error(`The schema does not contain the path: ${t}. (failed at: ${i} which is a type: "${e.type}")`);
      a = n, n = n && n[d], e = e.fields[d];
    }
    s = d, i = c ? "[" + l + "]" : "." + l;
  }), {
    schema: e,
    parent: a,
    parentPath: s
  }) : {
    parent: a,
    parentPath: t,
    schema: e
  };
}
class us extends Set {
  describe() {
    const t = [];
    for (const n of this.values())
      t.push(Tr.isRef(n) ? n.describe() : n);
    return t;
  }
  resolveAll(t) {
    let n = [];
    for (const o of this.values())
      n.push(t(o));
    return n;
  }
  clone() {
    return new us(this.values());
  }
  merge(t, n) {
    const o = this.clone();
    return t.forEach((a) => o.add(a)), n.forEach((a) => o.delete(a)), o;
  }
}
function Vr(e, t = /* @__PURE__ */ new Map()) {
  if (Sc(e) || !e || typeof e != "object") return e;
  if (t.has(e)) return t.get(e);
  let n;
  if (e instanceof Date)
    n = new Date(e.getTime()), t.set(e, n);
  else if (e instanceof RegExp)
    n = new RegExp(e), t.set(e, n);
  else if (Array.isArray(e)) {
    n = new Array(e.length), t.set(e, n);
    for (let o = 0; o < e.length; o++) n[o] = Vr(e[o], t);
  } else if (e instanceof Map) {
    n = /* @__PURE__ */ new Map(), t.set(e, n);
    for (const [o, a] of e.entries()) n.set(o, Vr(a, t));
  } else if (e instanceof Set) {
    n = /* @__PURE__ */ new Set(), t.set(e, n);
    for (const o of e) n.add(Vr(o, t));
  } else if (e instanceof Object) {
    n = {}, t.set(e, n);
    for (const [o, a] of Object.entries(e)) n[o] = Vr(a, t);
  } else
    throw Error(`Unable to clone ${e}`);
  return n;
}
class fn {
  constructor(t) {
    this.type = void 0, this.deps = [], this.tests = void 0, this.transforms = void 0, this.conditions = [], this._mutate = void 0, this.internalTests = {}, this._whitelist = new us(), this._blacklist = new us(), this.exclusiveTests = /* @__PURE__ */ Object.create(null), this._typeCheck = void 0, this.spec = void 0, this.tests = [], this.transforms = [], this.withMutation(() => {
      this.typeError(sn.notType);
    }), this.type = t.type, this._typeCheck = t.check, this.spec = Object.assign({
      strip: !1,
      strict: !1,
      abortEarly: !0,
      recursive: !0,
      disableStackTrace: !1,
      nullable: !1,
      optional: !0,
      coerce: !0
    }, t == null ? void 0 : t.spec), this.withMutation((n) => {
      n.nonNullable();
    });
  }
  // TODO: remove
  get _type() {
    return this.type;
  }
  clone(t) {
    if (this._mutate)
      return t && Object.assign(this.spec, t), this;
    const n = Object.create(Object.getPrototypeOf(this));
    return n.type = this.type, n._typeCheck = this._typeCheck, n._whitelist = this._whitelist.clone(), n._blacklist = this._blacklist.clone(), n.internalTests = Object.assign({}, this.internalTests), n.exclusiveTests = Object.assign({}, this.exclusiveTests), n.deps = [...this.deps], n.conditions = [...this.conditions], n.tests = [...this.tests], n.transforms = [...this.transforms], n.spec = Vr(Object.assign({}, this.spec, t)), n;
  }
  label(t) {
    let n = this.clone();
    return n.spec.label = t, n;
  }
  meta(...t) {
    if (t.length === 0) return this.spec.meta;
    let n = this.clone();
    return n.spec.meta = Object.assign(n.spec.meta || {}, t[0]), n;
  }
  withMutation(t) {
    let n = this._mutate;
    this._mutate = !0;
    let o = t(this);
    return this._mutate = n, o;
  }
  concat(t) {
    if (!t || t === this) return this;
    if (t.type !== this.type && this.type !== "mixed") throw new TypeError(`You cannot \`concat()\` schema's of different types: ${this.type} and ${t.type}`);
    let n = this, o = t.clone();
    const a = Object.assign({}, n.spec, o.spec);
    return o.spec = a, o.internalTests = Object.assign({}, n.internalTests, o.internalTests), o._whitelist = n._whitelist.merge(t._whitelist, t._blacklist), o._blacklist = n._blacklist.merge(t._blacklist, t._whitelist), o.tests = n.tests, o.exclusiveTests = n.exclusiveTests, o.withMutation((s) => {
      t.tests.forEach((i) => {
        s.test(i.OPTIONS);
      });
    }), o.transforms = [...n.transforms, ...o.transforms], o;
  }
  isType(t) {
    return t == null ? !!(this.spec.nullable && t === null || this.spec.optional && t === void 0) : this._typeCheck(t);
  }
  resolve(t) {
    let n = this;
    if (n.conditions.length) {
      let o = n.conditions;
      n = n.clone(), n.conditions = [], n = o.reduce((a, s) => s.resolve(a, t), n), n = n.resolve(t);
    }
    return n;
  }
  resolveOptions(t) {
    var n, o, a, s;
    return Object.assign({}, t, {
      from: t.from || [],
      strict: (n = t.strict) != null ? n : this.spec.strict,
      abortEarly: (o = t.abortEarly) != null ? o : this.spec.abortEarly,
      recursive: (a = t.recursive) != null ? a : this.spec.recursive,
      disableStackTrace: (s = t.disableStackTrace) != null ? s : this.spec.disableStackTrace
    });
  }
  /**
   * Run the configured transform pipeline over an input value.
   */
  cast(t, n = {}) {
    let o = this.resolve(Object.assign({
      value: t
    }, n)), a = n.assert === "ignore-optionality", s = o._cast(t, n);
    if (n.assert !== !1 && !o.isType(s)) {
      if (a && or(s))
        return s;
      let i = An(t), l = An(s);
      throw new TypeError(`The value of ${n.path || "field"} could not be cast to a value that satisfies the schema type: "${o.type}". 

attempted value: ${i} 
` + (l !== i ? `result of cast: ${l}` : ""));
    }
    return s;
  }
  _cast(t, n) {
    let o = t === void 0 ? t : this.transforms.reduce((a, s) => s.call(this, a, t, this), t);
    return o === void 0 && (o = this.getDefault(n)), o;
  }
  _validate(t, n = {}, o, a) {
    let {
      path: s,
      originalValue: i = t,
      strict: l = this.spec.strict
    } = n, c = t;
    l || (c = this._cast(c, Object.assign({
      assert: !1
    }, n)));
    let u = [];
    for (let d of Object.values(this.internalTests))
      d && u.push(d);
    this.runTests({
      path: s,
      value: c,
      originalValue: i,
      options: n,
      tests: u
    }, o, (d) => {
      if (d.length)
        return a(d, c);
      this.runTests({
        path: s,
        value: c,
        originalValue: i,
        options: n,
        tests: this.tests
      }, o, a);
    });
  }
  /**
   * Executes a set of validations, either schema, produced Tests or a nested
   * schema validate result.
   */
  runTests(t, n, o) {
    let a = !1, {
      tests: s,
      value: i,
      originalValue: l,
      path: c,
      options: u
    } = t, d = (h) => {
      a || (a = !0, n(h, i));
    }, f = (h) => {
      a || (a = !0, o(h, i));
    }, p = s.length, m = [];
    if (!p) return f([]);
    let v = {
      value: i,
      originalValue: l,
      path: c,
      options: u,
      schema: this
    };
    for (let h = 0; h < s.length; h++) {
      const y = s[h];
      y(v, d, function(C) {
        C && (Array.isArray(C) ? m.push(...C) : m.push(C)), --p <= 0 && f(m);
      });
    }
  }
  asNestedTest({
    key: t,
    index: n,
    parent: o,
    parentPath: a,
    originalParent: s,
    options: i
  }) {
    const l = t ?? n;
    if (l == null)
      throw TypeError("Must include `key` or `index` for nested validations");
    const c = typeof l == "number";
    let u = o[l];
    const d = Object.assign({}, i, {
      // Nested validations fields are always strict:
      //    1. parent isn't strict so the casting will also have cast inner values
      //    2. parent is strict in which case the nested values weren't cast either
      strict: !0,
      parent: o,
      value: u,
      originalValue: s[l],
      // FIXME: tests depend on `index` being passed around deeply,
      //   we should not let the options.key/index bleed through
      key: void 0,
      // index: undefined,
      [c ? "index" : "key"]: l,
      path: c || l.includes(".") ? `${a || ""}[${c ? l : `"${l}"`}]` : (a ? `${a}.` : "") + t
    });
    return (f, p, m) => this.resolve(d)._validate(u, d, p, m);
  }
  validate(t, n) {
    var o;
    let a = this.resolve(Object.assign({}, n, {
      value: t
    })), s = (o = n == null ? void 0 : n.disableStackTrace) != null ? o : a.spec.disableStackTrace;
    return new Promise((i, l) => a._validate(t, n, (c, u) => {
      Et.isError(c) && (c.value = u), l(c);
    }, (c, u) => {
      c.length ? l(new Et(c, u, void 0, void 0, s)) : i(u);
    }));
  }
  validateSync(t, n) {
    var o;
    let a = this.resolve(Object.assign({}, n, {
      value: t
    })), s, i = (o = n == null ? void 0 : n.disableStackTrace) != null ? o : a.spec.disableStackTrace;
    return a._validate(t, Object.assign({}, n, {
      sync: !0
    }), (l, c) => {
      throw Et.isError(l) && (l.value = c), l;
    }, (l, c) => {
      if (l.length) throw new Et(l, t, void 0, void 0, i);
      s = c;
    }), s;
  }
  isValid(t, n) {
    return this.validate(t, n).then(() => !0, (o) => {
      if (Et.isError(o)) return !1;
      throw o;
    });
  }
  isValidSync(t, n) {
    try {
      return this.validateSync(t, n), !0;
    } catch (o) {
      if (Et.isError(o)) return !1;
      throw o;
    }
  }
  _getDefault(t) {
    let n = this.spec.default;
    return n == null ? n : typeof n == "function" ? n.call(this, t) : Vr(n);
  }
  getDefault(t) {
    return this.resolve(t || {})._getDefault(t);
  }
  default(t) {
    return arguments.length === 0 ? this._getDefault() : this.clone({
      default: t
    });
  }
  strict(t = !0) {
    return this.clone({
      strict: t
    });
  }
  nullability(t, n) {
    const o = this.clone({
      nullable: t
    });
    return o.internalTests.nullable = _r({
      message: n,
      name: "nullable",
      test(a) {
        return a === null ? this.schema.spec.nullable : !0;
      }
    }), o;
  }
  optionality(t, n) {
    const o = this.clone({
      optional: t
    });
    return o.internalTests.optionality = _r({
      message: n,
      name: "optionality",
      test(a) {
        return a === void 0 ? this.schema.spec.optional : !0;
      }
    }), o;
  }
  optional() {
    return this.optionality(!0);
  }
  defined(t = sn.defined) {
    return this.optionality(!1, t);
  }
  nullable() {
    return this.nullability(!0);
  }
  nonNullable(t = sn.notNull) {
    return this.nullability(!1, t);
  }
  required(t = sn.required) {
    return this.clone().withMutation((n) => n.nonNullable(t).defined(t));
  }
  notRequired() {
    return this.clone().withMutation((t) => t.nullable().optional());
  }
  transform(t) {
    let n = this.clone();
    return n.transforms.push(t), n;
  }
  /**
   * Adds a test function to the schema's queue of tests.
   * tests can be exclusive or non-exclusive.
   *
   * - exclusive tests, will replace any existing tests of the same name.
   * - non-exclusive: can be stacked
   *
   * If a non-exclusive test is added to a schema with an exclusive test of the same name
   * the exclusive test is removed and further tests of the same name will be stacked.
   *
   * If an exclusive test is added to a schema with non-exclusive tests of the same name
   * the previous tests are removed and further tests of the same name will replace each other.
   */
  test(...t) {
    let n;
    if (t.length === 1 ? typeof t[0] == "function" ? n = {
      test: t[0]
    } : n = t[0] : t.length === 2 ? n = {
      name: t[0],
      test: t[1]
    } : n = {
      name: t[0],
      message: t[1],
      test: t[2]
    }, n.message === void 0 && (n.message = sn.default), typeof n.test != "function") throw new TypeError("`test` is a required parameters");
    let o = this.clone(), a = _r(n), s = n.exclusive || n.name && o.exclusiveTests[n.name] === !0;
    if (n.exclusive && !n.name)
      throw new TypeError("Exclusive tests must provide a unique `name` identifying the test");
    return n.name && (o.exclusiveTests[n.name] = !!n.exclusive), o.tests = o.tests.filter((i) => !(i.OPTIONS.name === n.name && (s || i.OPTIONS.test === a.OPTIONS.test))), o.tests.push(a), o;
  }
  when(t, n) {
    !Array.isArray(t) && typeof t != "string" && (n = t, t = ".");
    let o = this.clone(), a = dh(t).map((s) => new Tr(s));
    return a.forEach((s) => {
      s.isSibling && o.deps.push(s.key);
    }), o.conditions.push(typeof n == "function" ? new cs(a, n) : cs.fromOptions(a, n)), o;
  }
  typeError(t) {
    let n = this.clone();
    return n.internalTests.typeError = _r({
      message: t,
      name: "typeError",
      skipAbsent: !0,
      test(o) {
        return this.schema._typeCheck(o) ? !0 : this.createError({
          params: {
            type: this.schema.type
          }
        });
      }
    }), n;
  }
  oneOf(t, n = sn.oneOf) {
    let o = this.clone();
    return t.forEach((a) => {
      o._whitelist.add(a), o._blacklist.delete(a);
    }), o.internalTests.whiteList = _r({
      message: n,
      name: "oneOf",
      skipAbsent: !0,
      test(a) {
        let s = this.schema._whitelist, i = s.resolveAll(this.resolve);
        return i.includes(a) ? !0 : this.createError({
          params: {
            values: Array.from(s).join(", "),
            resolved: i
          }
        });
      }
    }), o;
  }
  notOneOf(t, n = sn.notOneOf) {
    let o = this.clone();
    return t.forEach((a) => {
      o._blacklist.add(a), o._whitelist.delete(a);
    }), o.internalTests.blacklist = _r({
      message: n,
      name: "notOneOf",
      test(a) {
        let s = this.schema._blacklist, i = s.resolveAll(this.resolve);
        return i.includes(a) ? this.createError({
          params: {
            values: Array.from(s).join(", "),
            resolved: i
          }
        }) : !0;
      }
    }), o;
  }
  strip(t = !0) {
    let n = this.clone();
    return n.spec.strip = t, n;
  }
  /**
   * Return a serialized description of the schema including validations, flags, types etc.
   *
   * @param options Provide any needed context for resolving runtime schema alterations (lazy, when conditions, etc).
   */
  describe(t) {
    const n = (t ? this.resolve(t) : this).clone(), {
      label: o,
      meta: a,
      optional: s,
      nullable: i
    } = n.spec;
    return {
      meta: a,
      label: o,
      optional: s,
      nullable: i,
      default: n.getDefault(t),
      type: n.type,
      oneOf: n._whitelist.describe(),
      notOneOf: n._blacklist.describe(),
      tests: n.tests.map((c) => ({
        name: c.OPTIONS.name,
        params: c.OPTIONS.params
      })).filter((c, u, d) => d.findIndex((f) => f.name === c.name) === u)
    };
  }
}
fn.prototype.__isYupSchema__ = !0;
for (const e of ["validate", "validateSync"]) fn.prototype[`${e}At`] = function(t, n, o = {}) {
  const {
    parent: a,
    parentPath: s,
    schema: i
  } = JD(this, t, n, o.context);
  return i[e](a && a[s], Object.assign({}, o, {
    parent: a,
    path: t
  }));
};
for (const e of ["equals", "is"]) fn.prototype[e] = fn.prototype.oneOf;
for (const e of ["not", "nope"]) fn.prototype[e] = fn.prototype.notOneOf;
const QD = /^(\d{4}|[+-]\d{6})(?:-?(\d{2})(?:-?(\d{2}))?)?(?:[ T]?(\d{2}):?(\d{2})(?::?(\d{2})(?:[,.](\d{1,}))?)?(?:(Z)|([+-])(\d{2})(?::?(\d{2}))?)?)?$/;
function e$(e) {
  const t = pl(e);
  if (!t) return Date.parse ? Date.parse(e) : Number.NaN;
  if (t.z === void 0 && t.plusMinus === void 0)
    return new Date(t.year, t.month, t.day, t.hour, t.minute, t.second, t.millisecond).valueOf();
  let n = 0;
  return t.z !== "Z" && t.plusMinus !== void 0 && (n = t.hourOffset * 60 + t.minuteOffset, t.plusMinus === "+" && (n = 0 - n)), Date.UTC(t.year, t.month, t.day, t.hour, t.minute + n, t.second, t.millisecond);
}
function pl(e) {
  var t, n;
  const o = QD.exec(e);
  return o ? {
    year: hn(o[1]),
    month: hn(o[2], 1) - 1,
    day: hn(o[3], 1),
    hour: hn(o[4]),
    minute: hn(o[5]),
    second: hn(o[6]),
    millisecond: o[7] ? (
      // allow arbitrary sub-second precision beyond milliseconds
      hn(o[7].substring(0, 3))
    ) : 0,
    precision: (t = (n = o[7]) == null ? void 0 : n.length) != null ? t : void 0,
    z: o[8] || void 0,
    plusMinus: o[9] || void 0,
    hourOffset: hn(o[10]),
    minuteOffset: hn(o[11])
  } : null;
}
function hn(e, t = 0) {
  return Number(e) || t;
}
let t$ = (
  // eslint-disable-next-line
  /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/
), n$ = (
  // eslint-disable-next-line
  /^((https?|ftp):)?\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i
), r$ = /^(?:[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|00000000-0000-0000-0000-000000000000)$/i, o$ = "^\\d{4}-\\d{2}-\\d{2}", a$ = "\\d{2}:\\d{2}:\\d{2}", s$ = "(([+-]\\d{2}(:?\\d{2})?)|Z)", i$ = new RegExp(`${o$}T${a$}(\\.\\d+)?${s$}$`), l$ = (e) => or(e) || e === e.trim(), c$ = {}.toString();
function hh() {
  return new bh();
}
class bh extends fn {
  constructor() {
    super({
      type: "string",
      check(t) {
        return t instanceof String && (t = t.valueOf()), typeof t == "string";
      }
    }), this.withMutation(() => {
      this.transform((t, n, o) => {
        if (!o.spec.coerce || o.isType(t) || Array.isArray(t)) return t;
        const a = t != null && t.toString ? t.toString() : t;
        return a === c$ ? t : a;
      });
    });
  }
  required(t) {
    return super.required(t).withMutation((n) => n.test({
      message: t || sn.required,
      name: "required",
      skipAbsent: !0,
      test: (o) => !!o.length
    }));
  }
  notRequired() {
    return super.notRequired().withMutation((t) => (t.tests = t.tests.filter((n) => n.OPTIONS.name !== "required"), t));
  }
  length(t, n = wt.length) {
    return this.test({
      message: n,
      name: "length",
      exclusive: !0,
      params: {
        length: t
      },
      skipAbsent: !0,
      test(o) {
        return o.length === this.resolve(t);
      }
    });
  }
  min(t, n = wt.min) {
    return this.test({
      message: n,
      name: "min",
      exclusive: !0,
      params: {
        min: t
      },
      skipAbsent: !0,
      test(o) {
        return o.length >= this.resolve(t);
      }
    });
  }
  max(t, n = wt.max) {
    return this.test({
      name: "max",
      exclusive: !0,
      message: n,
      params: {
        max: t
      },
      skipAbsent: !0,
      test(o) {
        return o.length <= this.resolve(t);
      }
    });
  }
  matches(t, n) {
    let o = !1, a, s;
    return n && (typeof n == "object" ? {
      excludeEmptyString: o = !1,
      message: a,
      name: s
    } = n : a = n), this.test({
      name: s || "matches",
      message: a || wt.matches,
      params: {
        regex: t
      },
      skipAbsent: !0,
      test: (i) => i === "" && o || i.search(t) !== -1
    });
  }
  email(t = wt.email) {
    return this.matches(t$, {
      name: "email",
      message: t,
      excludeEmptyString: !0
    });
  }
  url(t = wt.url) {
    return this.matches(n$, {
      name: "url",
      message: t,
      excludeEmptyString: !0
    });
  }
  uuid(t = wt.uuid) {
    return this.matches(r$, {
      name: "uuid",
      message: t,
      excludeEmptyString: !1
    });
  }
  datetime(t) {
    let n = "", o, a;
    return t && (typeof t == "object" ? {
      message: n = "",
      allowOffset: o = !1,
      precision: a = void 0
    } = t : n = t), this.matches(i$, {
      name: "datetime",
      message: n || wt.datetime,
      excludeEmptyString: !0
    }).test({
      name: "datetime_offset",
      message: n || wt.datetime_offset,
      params: {
        allowOffset: o
      },
      skipAbsent: !0,
      test: (s) => {
        if (!s || o) return !0;
        const i = pl(s);
        return i ? !!i.z : !1;
      }
    }).test({
      name: "datetime_precision",
      message: n || wt.datetime_precision,
      params: {
        precision: a
      },
      skipAbsent: !0,
      test: (s) => {
        if (!s || a == null) return !0;
        const i = pl(s);
        return i ? i.precision === a : !1;
      }
    });
  }
  //-- transforms --
  ensure() {
    return this.default("").transform((t) => t === null ? "" : t);
  }
  trim(t = wt.trim) {
    return this.transform((n) => n != null ? n.trim() : n).test({
      message: t,
      name: "trim",
      test: l$
    });
  }
  lowercase(t = wt.lowercase) {
    return this.transform((n) => or(n) ? n : n.toLowerCase()).test({
      message: t,
      name: "string_case",
      exclusive: !0,
      skipAbsent: !0,
      test: (n) => or(n) || n === n.toLowerCase()
    });
  }
  uppercase(t = wt.uppercase) {
    return this.transform((n) => or(n) ? n : n.toUpperCase()).test({
      message: t,
      name: "string_case",
      exclusive: !0,
      skipAbsent: !0,
      test: (n) => or(n) || n === n.toUpperCase()
    });
  }
}
hh.prototype = bh.prototype;
let u$ = /* @__PURE__ */ new Date(""), d$ = (e) => Object.prototype.toString.call(e) === "[object Date]";
class Js extends fn {
  constructor() {
    super({
      type: "date",
      check(t) {
        return d$(t) && !isNaN(t.getTime());
      }
    }), this.withMutation(() => {
      this.transform((t, n, o) => !o.spec.coerce || o.isType(t) || t === null ? t : (t = e$(t), isNaN(t) ? Js.INVALID_DATE : new Date(t)));
    });
  }
  prepareParam(t, n) {
    let o;
    if (Tr.isRef(t))
      o = t;
    else {
      let a = this.cast(t);
      if (!this._typeCheck(a)) throw new TypeError(`\`${n}\` must be a Date or a value that can be \`cast()\` to a Date`);
      o = a;
    }
    return o;
  }
  min(t, n = ul.min) {
    let o = this.prepareParam(t, "min");
    return this.test({
      message: n,
      name: "min",
      exclusive: !0,
      params: {
        min: t
      },
      skipAbsent: !0,
      test(a) {
        return a >= this.resolve(o);
      }
    });
  }
  max(t, n = ul.max) {
    let o = this.prepareParam(t, "max");
    return this.test({
      message: n,
      name: "max",
      exclusive: !0,
      params: {
        max: t
      },
      skipAbsent: !0,
      test(a) {
        return a <= this.resolve(o);
      }
    });
  }
}
Js.INVALID_DATE = u$;
Js.prototype;
function p$(e, t = []) {
  let n = [], o = /* @__PURE__ */ new Set(), a = new Set(t.map(([i, l]) => `${i}-${l}`));
  function s(i, l) {
    let c = ir.split(i)[0];
    o.add(c), a.has(`${l}-${c}`) || n.push([l, c]);
  }
  for (const i of Object.keys(e)) {
    let l = e[i];
    o.add(i), Tr.isRef(l) && l.isSibling ? s(l.path, i) : Sc(l) && "deps" in l && l.deps.forEach((c) => s(c, i));
  }
  return LD.array(Array.from(o), n).reverse();
}
function cp(e, t) {
  let n = 1 / 0;
  return e.some((o, a) => {
    var s;
    if ((s = t.path) != null && s.includes(o))
      return n = a, !0;
  }), n;
}
function gh(e) {
  return (t, n) => cp(e, t) - cp(e, n);
}
const f$ = (e, t, n) => {
  if (typeof e != "string")
    return e;
  let o = e;
  try {
    o = JSON.parse(e);
  } catch {
  }
  return n.isType(o) ? o : e;
};
function qa(e) {
  if ("fields" in e) {
    const t = {};
    for (const [n, o] of Object.entries(e.fields))
      t[n] = qa(o);
    return e.setFields(t);
  }
  if (e.type === "array") {
    const t = e.optional();
    return t.innerType && (t.innerType = qa(t.innerType)), t;
  }
  return e.type === "tuple" ? e.optional().clone({
    types: e.spec.types.map(qa)
  }) : "optional" in e ? e.optional() : e;
}
const m$ = (e, t) => {
  const n = [...ir.normalizePath(t)];
  if (n.length === 1) return n[0] in e;
  let o = n.pop(), a = ir.getter(ir.join(n), !0)(e);
  return !!(a && o in a);
};
let up = (e) => Object.prototype.toString.call(e) === "[object Object]";
function h$(e, t) {
  let n = Object.keys(e.fields);
  return Object.keys(t).filter((o) => n.indexOf(o) === -1);
}
const b$ = gh([]);
function yh(e) {
  return new vh(e);
}
class vh extends fn {
  constructor(t) {
    super({
      type: "object",
      check(n) {
        return up(n) || typeof n == "function";
      }
    }), this.fields = /* @__PURE__ */ Object.create(null), this._sortErrors = b$, this._nodes = [], this._excludedEdges = [], this.withMutation(() => {
      t && this.shape(t);
    });
  }
  _cast(t, n = {}) {
    var o;
    let a = super._cast(t, n);
    if (a === void 0) return this.getDefault(n);
    if (!this._typeCheck(a)) return a;
    let s = this.fields, i = (o = n.stripUnknown) != null ? o : this.spec.noUnknown, l = [].concat(this._nodes, Object.keys(a).filter((f) => !this._nodes.includes(f))), c = {}, u = Object.assign({}, n, {
      parent: c,
      __validating: n.__validating || !1
    }), d = !1;
    for (const f of l) {
      let p = s[f], m = f in a;
      if (p) {
        let v, h = a[f];
        u.path = (n.path ? `${n.path}.` : "") + f, p = p.resolve({
          value: h,
          context: n.context,
          parent: c
        });
        let y = p instanceof fn ? p.spec : void 0, w = y == null ? void 0 : y.strict;
        if (y != null && y.strip) {
          d = d || f in a;
          continue;
        }
        v = !n.__validating || !w ? (
          // TODO: use _cast, this is double resolving
          p.cast(a[f], u)
        ) : a[f], v !== void 0 && (c[f] = v);
      } else m && !i && (c[f] = a[f]);
      (m !== f in c || c[f] !== a[f]) && (d = !0);
    }
    return d ? c : a;
  }
  _validate(t, n = {}, o, a) {
    let {
      from: s = [],
      originalValue: i = t,
      recursive: l = this.spec.recursive
    } = n;
    n.from = [{
      schema: this,
      value: i
    }, ...s], n.__validating = !0, n.originalValue = i, super._validate(t, n, o, (c, u) => {
      if (!l || !up(u)) {
        a(c, u);
        return;
      }
      i = i || u;
      let d = [];
      for (let f of this._nodes) {
        let p = this.fields[f];
        !p || Tr.isRef(p) || d.push(p.asNestedTest({
          options: n,
          key: f,
          parent: u,
          parentPath: n.path,
          originalParent: i
        }));
      }
      this.runTests({
        tests: d,
        value: u,
        originalValue: i,
        options: n
      }, o, (f) => {
        a(f.sort(this._sortErrors).concat(c), u);
      });
    });
  }
  clone(t) {
    const n = super.clone(t);
    return n.fields = Object.assign({}, this.fields), n._nodes = this._nodes, n._excludedEdges = this._excludedEdges, n._sortErrors = this._sortErrors, n;
  }
  concat(t) {
    let n = super.concat(t), o = n.fields;
    for (let [a, s] of Object.entries(this.fields)) {
      const i = o[a];
      o[a] = i === void 0 ? s : i;
    }
    return n.withMutation((a) => (
      // XXX: excludes here is wrong
      a.setFields(o, [...this._excludedEdges, ...t._excludedEdges])
    ));
  }
  _getDefault(t) {
    if ("default" in this.spec)
      return super._getDefault(t);
    if (!this._nodes.length)
      return;
    let n = {};
    return this._nodes.forEach((o) => {
      var a;
      const s = this.fields[o];
      let i = t;
      (a = i) != null && a.value && (i = Object.assign({}, i, {
        parent: i.value,
        value: i.value[o]
      })), n[o] = s && "getDefault" in s ? s.getDefault(i) : void 0;
    }), n;
  }
  setFields(t, n) {
    let o = this.clone();
    return o.fields = t, o._nodes = p$(t, n), o._sortErrors = gh(Object.keys(t)), n && (o._excludedEdges = n), o;
  }
  shape(t, n = []) {
    return this.clone().withMutation((o) => {
      let a = o._excludedEdges;
      return n.length && (Array.isArray(n[0]) || (n = [n]), a = [...o._excludedEdges, ...n]), o.setFields(Object.assign(o.fields, t), a);
    });
  }
  partial() {
    const t = {};
    for (const [n, o] of Object.entries(this.fields))
      t[n] = "optional" in o && o.optional instanceof Function ? o.optional() : o;
    return this.setFields(t);
  }
  deepPartial() {
    return qa(this);
  }
  pick(t) {
    const n = {};
    for (const o of t)
      this.fields[o] && (n[o] = this.fields[o]);
    return this.setFields(n, this._excludedEdges.filter(([o, a]) => t.includes(o) && t.includes(a)));
  }
  omit(t) {
    const n = [];
    for (const o of Object.keys(this.fields))
      t.includes(o) || n.push(o);
    return this.pick(n);
  }
  from(t, n, o) {
    let a = ir.getter(t, !0);
    return this.transform((s) => {
      if (!s) return s;
      let i = s;
      return m$(s, t) && (i = Object.assign({}, s), o || delete i[t], i[n] = a(s)), i;
    });
  }
  /** Parse an input JSON string to an object */
  json() {
    return this.transform(f$);
  }
  noUnknown(t = !0, n = dl.noUnknown) {
    typeof t != "boolean" && (n = t, t = !0);
    let o = this.test({
      name: "noUnknown",
      exclusive: !0,
      message: n,
      test(a) {
        if (a == null) return !0;
        const s = h$(this.schema, a);
        return !t || s.length === 0 || this.createError({
          params: {
            unknown: s.join(", ")
          }
        });
      }
    });
    return o.spec.noUnknown = t, o;
  }
  unknown(t = !0, n = dl.noUnknown) {
    return this.noUnknown(!t, n);
  }
  transformKeys(t) {
    return this.transform((n) => {
      if (!n) return n;
      const o = {};
      for (const a of Object.keys(n)) o[t(a)] = n[a];
      return o;
    });
  }
  camelCase() {
    return this.transformKeys(Di.camelCase);
  }
  snakeCase() {
    return this.transformKeys(Di.snakeCase);
  }
  constantCase() {
    return this.transformKeys((t) => Di.snakeCase(t).toUpperCase());
  }
  describe(t) {
    const n = (t ? this.resolve(t) : this).clone(), o = super.describe(t);
    o.fields = {};
    for (const [s, i] of Object.entries(n.fields)) {
      var a;
      let l = t;
      (a = l) != null && a.value && (l = Object.assign({}, l, {
        parent: l.value,
        value: l.value[s]
      })), o.fields[s] = i.describe(l);
    }
    return o;
  }
}
yh.prototype = vh.prototype;
const g$ = ({
  setFetchAgain: e,
  createModalOpen: t,
  handleCreateModalClose: n,
  formConfiguration: o,
  config: a
}) => {
  const s = hr(), i = () => yh().shape({
    name: hh().required(s.formatMessage({ id: "error.valueRequired" })).matches(/^[_\-a-zA-Z\d]*$/g, s.formatMessage({ id: "error.invalidFormName" }))
  }), l = async (c) => {
    const u = async (f) => {
      try {
        const p = await f.json();
        return e((m) => !m), n(), p;
      } catch (p) {
        throw vn(p, a.setTechnicalError), p;
      }
    }, d = async (f, p) => {
      try {
        return await Hr(f, p);
      } catch (m) {
        throw vn(m, a.setTechnicalError), m;
      }
    };
    if (o)
      try {
        const f = await Dm(
          o.id,
          a
        ), p = await d(f, a.setLoginRequired), m = await u(p);
        delete m._id, delete m._rev, m.name = c.name, m.metadata.label = c.label || "";
        const v = await Qi(m, a);
        await d(v, a.setLoginRequired), await u({ json: () => m });
      } catch (f) {
        vn(f, a.setTechnicalError);
      }
    else {
      const f = HO;
      f.name = c.name, f.metadata.label = c.label || "";
      try {
        const p = await Qi(f, a);
        await d(p, a.setLoginRequired), await u(p);
      } catch (p) {
        vn(p, a.setTechnicalError);
      }
    }
  };
  return /* @__PURE__ */ x.jsx(tn, { children: /* @__PURE__ */ x.jsxs(
    Pm,
    {
      open: t,
      onClose: n,
      maxWidth: "lg",
      children: [
        /* @__PURE__ */ x.jsx(nc, { sx: { m: 0, py: 2, px: 4 }, children: o ? /* @__PURE__ */ x.jsx(Ji, { children: /* @__PURE__ */ x.jsx(bt, { id: "heading.copyDialog" }) }) : /* @__PURE__ */ x.jsx(Ji, { children: /* @__PURE__ */ x.jsx(bt, { id: "heading.addDialog" }) }) }),
        /* @__PURE__ */ x.jsx(Jo, {}),
        /* @__PURE__ */ x.jsx(zs, { children: /* @__PURE__ */ x.jsx(
          hD,
          {
            initialValues: {
              name: void 0,
              label: o ? "Copy of " + o.metadata.label : "New form"
            },
            onSubmit: (c) => {
              l(c);
            },
            validationSchema: i,
            children: ({ isSubmitting: c, dirty: u, isValid: d, touched: f, errors: p, submitForm: m, values: v, setFieldValue: h }) => /* @__PURE__ */ x.jsx(ih, { children: /* @__PURE__ */ x.jsxs(tn, { sx: { px: 3, pt: 1, pb: 2, display: "flex", flexDirection: "column" }, children: [
              /* @__PURE__ */ x.jsxs(tn, { sx: { display: "flex", flexDirection: "column" }, children: [
                /* @__PURE__ */ x.jsx(Rt, { sx: { my: 1, mx: 0 }, children: /* @__PURE__ */ x.jsx(bt, { id: "adminUI.dialog.formName" }) }),
                /* @__PURE__ */ x.jsx(
                  os,
                  {
                    name: "name",
                    error: !!p.name,
                    required: !0,
                    onChange: (y) => h("name", y.target.value),
                    value: v.name,
                    sx: { minWidth: "500px" }
                  }
                ),
                p.name && /* @__PURE__ */ x.jsx(rc, { error: !!p.name, children: p.name }),
                /* @__PURE__ */ x.jsx(Rt, { sx: { my: 1, mx: 0 }, children: /* @__PURE__ */ x.jsx(bt, { id: "adminUI.dialog.formLabel" }) }),
                /* @__PURE__ */ x.jsx(
                  os,
                  {
                    name: "label",
                    onChange: (y) => h("label", y.target.value),
                    value: v.label,
                    sx: { minWidth: "500px" }
                  }
                )
              ] }),
              /* @__PURE__ */ x.jsxs(tn, { sx: { display: "flex", mt: 2, justifyContent: "space-between" }, children: [
                /* @__PURE__ */ x.jsx(as, { onClick: n, children: /* @__PURE__ */ x.jsx(bt, { id: "button.cancel" }) }),
                /* @__PURE__ */ x.jsx(as, { onClick: m, disabled: !u || c || !d, children: /* @__PURE__ */ x.jsx(bt, { id: "button.accept" }) })
              ] })
            ] }) })
          }
        ) })
      ]
    }
  ) });
}, y$ = ({
  setFetchAgain: e,
  deleteModalOpen: t,
  handleDeleteModalClose: n,
  formConfiguration: o,
  config: a
}) => {
  const s = hr(), i = async () => {
    XO(o == null ? void 0 : o.id, a).then((l) => Hr(l, a.setLoginRequired)).then((l) => l.json()).then((l) => {
      n(), e((c) => !c);
    }).catch((l) => {
      vn(l, a.setTechnicalError);
    });
  };
  return /* @__PURE__ */ x.jsx(tn, { children: /* @__PURE__ */ x.jsxs(
    Pm,
    {
      open: t,
      onClose: n,
      maxWidth: "lg",
      children: [
        /* @__PURE__ */ x.jsx(nc, { sx: { m: 0, p: 2 }, children: /* @__PURE__ */ x.jsx(Ji, { children: /* @__PURE__ */ x.jsx(bt, { id: "heading.deleteDialog" }) }) }),
        /* @__PURE__ */ x.jsx(Jo, {}),
        /* @__PURE__ */ x.jsx(zs, { children: /* @__PURE__ */ x.jsxs(Rt, { sx: { padding: "20px 4px 4px 2px" }, children: [
          /* @__PURE__ */ x.jsx(bt, { id: "adminUI.dialog.deleteQuestion" }),
          " ",
          `"${(o == null ? void 0 : o.metadata.label) || s.formatMessage({ id: "adminUI.dialog.emptyTitle" })}"?`
        ] }) }),
        /* @__PURE__ */ x.jsx(Jo, {}),
        /* @__PURE__ */ x.jsxs(tc, { sx: { display: "flex", justifyContent: "space-between", padding: "12px" }, children: [
          /* @__PURE__ */ x.jsx(as, { onClick: n, children: /* @__PURE__ */ x.jsx(bt, { id: "button.cancel" }) }),
          /* @__PURE__ */ x.jsx(
            as,
            {
              sx: { backgroundColor: (l) => l.palette.error.main, color: (l) => l.palette.common.white },
              onClick: () => i(),
              children: /* @__PURE__ */ x.jsx(bt, { id: "button.accept" })
            }
          )
        ] })
      ]
    }
  ) });
};
var Pc = {}, v$ = Ln;
Object.defineProperty(Pc, "__esModule", {
  value: !0
});
var xh = Pc.default = void 0, x$ = v$(po()), T$ = x;
xh = Pc.default = (0, x$.default)(/* @__PURE__ */ (0, T$.jsx)("path", {
  d: "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34a.9959.9959 0 0 0-1.41 0l-1.83 1.83 3.75 3.75z"
}), "Edit");
var Rc = {}, w$ = Ln;
Object.defineProperty(Rc, "__esModule", {
  value: !0
});
var Th = Rc.default = void 0, E$ = w$(po()), C$ = x;
Th = Rc.default = (0, E$.default)(/* @__PURE__ */ (0, C$.jsx)("path", {
  d: "M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"
}), "Close");
var Dc = {}, O$ = Ln;
Object.defineProperty(Dc, "__esModule", {
  value: !0
});
var wh = Dc.default = void 0, S$ = O$(po()), P$ = x;
wh = Dc.default = (0, S$.default)(/* @__PURE__ */ (0, P$.jsx)("path", {
  d: "M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2m0 16H8V7h11z"
}), "ContentCopy");
const dp = {
  year: "numeric",
  month: "2-digit",
  day: "2-digit",
  hour: "numeric",
  minute: "numeric",
  hour12: !1
};
var Eh = { exports: {} };
(function(e, t) {
  (function(n, o) {
    o();
  })(go, function() {
    function n(u, d) {
      return typeof d > "u" ? d = { autoBom: !1 } : typeof d != "object" && (console.warn("Deprecated: Expected third argument to be a object"), d = { autoBom: !d }), d.autoBom && /^\s*(?:text\/\S*|application\/xml|\S*\/\S*\+xml)\s*;.*charset\s*=\s*utf-8/i.test(u.type) ? new Blob(["\uFEFF", u], { type: u.type }) : u;
    }
    function o(u, d, f) {
      var p = new XMLHttpRequest();
      p.open("GET", u), p.responseType = "blob", p.onload = function() {
        c(p.response, d, f);
      }, p.onerror = function() {
        console.error("could not download file");
      }, p.send();
    }
    function a(u) {
      var d = new XMLHttpRequest();
      d.open("HEAD", u, !1);
      try {
        d.send();
      } catch {
      }
      return 200 <= d.status && 299 >= d.status;
    }
    function s(u) {
      try {
        u.dispatchEvent(new MouseEvent("click"));
      } catch {
        var d = document.createEvent("MouseEvents");
        d.initMouseEvent("click", !0, !0, window, 0, 0, 0, 80, 20, !1, !1, !1, !1, 0, null), u.dispatchEvent(d);
      }
    }
    var i = typeof window == "object" && window.window === window ? window : typeof self == "object" && self.self === self ? self : typeof go == "object" && go.global === go ? go : void 0, l = i.navigator && /Macintosh/.test(navigator.userAgent) && /AppleWebKit/.test(navigator.userAgent) && !/Safari/.test(navigator.userAgent), c = i.saveAs || (typeof window != "object" || window !== i ? function() {
    } : "download" in HTMLAnchorElement.prototype && !l ? function(u, d, f) {
      var p = i.URL || i.webkitURL, m = document.createElement("a");
      d = d || u.name || "download", m.download = d, m.rel = "noopener", typeof u == "string" ? (m.href = u, m.origin === location.origin ? s(m) : a(m.href) ? o(u, d, f) : s(m, m.target = "_blank")) : (m.href = p.createObjectURL(u), setTimeout(function() {
        p.revokeObjectURL(m.href);
      }, 4e4), setTimeout(function() {
        s(m);
      }, 0));
    } : "msSaveOrOpenBlob" in navigator ? function(u, d, f) {
      if (d = d || u.name || "download", typeof u != "string") navigator.msSaveOrOpenBlob(n(u, f), d);
      else if (a(u)) o(u, d, f);
      else {
        var p = document.createElement("a");
        p.href = u, p.target = "_blank", setTimeout(function() {
          s(p);
        });
      }
    } : function(u, d, f, p) {
      if (p = p || open("", "_blank"), p && (p.document.title = p.document.body.innerText = "downloading..."), typeof u == "string") return o(u, d, f);
      var m = u.type === "application/octet-stream", v = /constructor/i.test(i.HTMLElement) || i.safari, h = /CriOS\/[\d]+/.test(navigator.userAgent);
      if ((h || m && v || l) && typeof FileReader < "u") {
        var y = new FileReader();
        y.onloadend = function() {
          var E = y.result;
          E = h ? E : E.replace(/^data:[^;]*;/, "data:attachment/file;"), p ? p.location.href = E : location = E, p = null;
        }, y.readAsDataURL(u);
      } else {
        var w = i.URL || i.webkitURL, C = w.createObjectURL(u);
        p ? p.location = C : location.href = C, p = null, setTimeout(function() {
          w.revokeObjectURL(C);
        }, 4e4);
      }
    });
    i.saveAs = c.saveAs = c, e.exports = c;
  });
})(Eh);
var R$ = Eh.exports;
const D$ = /* @__PURE__ */ ms(R$), Ia = (e) => {
  if (!e) return null;
  const t = new Date(e);
  return t.setHours(0, 0, 0, 0), t;
}, Ch = (e) => {
  var o;
  let t;
  Array.isArray(e) ? t = "dialobForms.json" : t = (o = e == null ? void 0 : e.metadata) != null && o.label ? `${e.metadata.label}.json` : "data.json";
  const n = new Blob([JSON.stringify(e)], { type: "json" });
  D$.saveAs(n, t);
};
var $c = {}, $$ = Ln;
Object.defineProperty($c, "__esModule", {
  value: !0
});
var kc = $c.default = void 0, k$ = $$(po()), _$ = x;
kc = $c.default = (0, k$.default)(/* @__PURE__ */ (0, _$.jsx)("path", {
  d: "M5 20h14v-2H5zM19 9h-4V3H9v6H5l7 7z"
}), "Download");
const M$ = ({
  filters: e,
  formConfiguration: t,
  copyFormConfiguration: n,
  deleteFormConfiguration: o,
  dialobForm: a,
  config: s
}) => {
  const [i, l] = gn([]), c = hr();
  Bt(
    () => {
      KO(s, t.id).then((p) => Hr(p, s.setLoginRequired)).then((p) => p.json()).then((p) => {
        l(p == null ? void 0 : p.map((m) => ({
          latestTagDate: m.created,
          latestTagName: m.name
        })));
      }).catch((p) => {
        vn(p, s.setTechnicalError);
      });
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    []
  );
  const u = Ya(() => {
    if (i.length !== 0)
      return i.sort((p, m) => new Date(m.latestTagDate).getTime() - new Date(p.latestTagDate).getTime()), i[0];
  }, [i]), d = Ya(() => {
    var m, v;
    const p = {
      ...Rm,
      lastSaved: t.metadata.lastSaved,
      label: t.metadata.label || "",
      latestTagName: (u == null ? void 0 : u.latestTagName) || "",
      latestTagDate: u == null ? void 0 : u.latestTagDate
    };
    if (!(e.label && !((m = p.label) != null && m.toLowerCase().includes(e.label.toLowerCase()))) && !(e.latestTagName && !((v = p.latestTagName) != null && v.toLowerCase().includes(e.latestTagName.toLowerCase())))) {
      if (e.latestTagDate)
        if (u) {
          const h = Ia(e.latestTagDate), y = Ia(u == null ? void 0 : u.latestTagDate);
          if (h && y && h.getTime() !== y.getTime())
            return;
        } else
          return;
      if (e.lastSaved) {
        const h = Ia(e.lastSaved), y = Ia(t.metadata.lastSaved);
        if (h && y && h.getTime() !== y.getTime())
          return;
      }
      return p;
    }
  }, [e, t.metadata.label, t.metadata.lastSaved, u]), f = () => {
    Ch(a);
  };
  return /* @__PURE__ */ x.jsx(x.Fragment, { children: d && /* @__PURE__ */ x.jsxs(Sm, { children: [
    /* @__PURE__ */ x.jsx(tt, { sx: { textAlign: "center" }, children: /* @__PURE__ */ x.jsx(Nn, { title: c.formatMessage({ id: "adminUI.table.tooltip.edit" }), placement: "top-end", arrow: !0, children: /* @__PURE__ */ x.jsx(
      Fr,
      {
        onClick: function(p) {
          p.preventDefault(), window.location.replace(`${s.dialobApiUrl}/composer/${t.id}`);
        },
        children: /* @__PURE__ */ x.jsx(rr, { fontSize: "small", children: /* @__PURE__ */ x.jsx(xh, {}) })
      }
    ) }) }),
    /* @__PURE__ */ x.jsx(tt, { sx: { textAlign: "center" }, children: /* @__PURE__ */ x.jsx(Nn, { title: c.formatMessage({ id: "adminUI.table.tooltip.copy" }), placement: "top-end", arrow: !0, children: /* @__PURE__ */ x.jsx(
      Fr,
      {
        onClick: function(p) {
          p.preventDefault(), n(t);
        },
        children: /* @__PURE__ */ x.jsx(rr, { fontSize: "small", children: /* @__PURE__ */ x.jsx(wh, {}) })
      }
    ) }) }),
    /* @__PURE__ */ x.jsx(tt, { children: t.metadata.label || c.formatMessage({ id: "adminUI.dialog.emptyTitle" }) }),
    /* @__PURE__ */ x.jsx(tt, { children: u == null ? void 0 : u.latestTagName }),
    /* @__PURE__ */ x.jsx(tt, { children: u && new Intl.DateTimeFormat(s.language, dp).format(new Date(u.latestTagDate)) }),
    /* @__PURE__ */ x.jsx(tt, { children: new Intl.DateTimeFormat(s.language, dp).format(new Date(t.metadata.lastSaved)) }),
    /* @__PURE__ */ x.jsx(tt, { sx: { textAlign: "center" }, children: /* @__PURE__ */ x.jsx(Nn, { title: c.formatMessage({ id: "adminUI.table.tooltip.delete" }), placement: "top-end", arrow: !0, children: /* @__PURE__ */ x.jsx(
      Fr,
      {
        onClick: function(p) {
          p.preventDefault(), o(t);
        },
        sx: { "&:hover": { backgroundColor: (p) => p.palette.error.main } },
        children: /* @__PURE__ */ x.jsx(rr, { fontSize: "small", children: /* @__PURE__ */ x.jsx(Th, {}) })
      }
    ) }) }),
    /* @__PURE__ */ x.jsx(tt, { sx: { textAlign: "center" }, children: /* @__PURE__ */ x.jsx(Nn, { title: c.formatMessage({ id: "download" }), placement: "top-end", arrow: !0, children: /* @__PURE__ */ x.jsx(
      Fr,
      {
        onClick: function(p) {
          p.preventDefault(), f();
        },
        children: /* @__PURE__ */ x.jsx(rr, { fontSize: "small", children: /* @__PURE__ */ x.jsx(kc, {}) })
      }
    ) }) })
  ] }, t.id) });
}, I$ = ["localeText"], ds = /* @__PURE__ */ g.createContext(null);
process.env.NODE_ENV !== "production" && (ds.displayName = "MuiPickersAdapterContext");
const _c = function(t) {
  var n;
  const {
    localeText: o
  } = t, a = ie(t, I$), {
    utils: s,
    localeText: i
  } = (n = g.useContext(ds)) != null ? n : {
    utils: void 0,
    localeText: void 0
  }, l = Ee({
    // We don't want to pass the `localeText` prop to the theme, that way it will always return the theme value,
    // We will then merge this theme value with our value manually
    props: a,
    name: "MuiLocalizationProvider"
  }), {
    children: c,
    dateAdapter: u,
    dateFormats: d,
    dateLibInstance: f,
    adapterLocale: p,
    localeText: m
  } = l, v = g.useMemo(() => b({}, m, i, o), [m, i, o]), h = g.useMemo(() => {
    if (!u)
      return s || null;
    const C = new u({
      locale: p,
      formats: d,
      instance: f
    });
    if (!C.isMUIAdapter)
      throw new Error(["MUI: The date adapter should be imported from `@mui/x-date-pickers` or `@mui/x-date-pickers-pro`, not from `@date-io`", "For example, `import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'` instead of `import AdapterDayjs from '@date-io/dayjs'`", "More information on the installation documentation: https://mui.com/x/react-date-pickers/getting-started/#installation"].join(`
`));
    return C;
  }, [u, p, d, f, s]), y = g.useMemo(() => h ? {
    minDate: h.date("1900-01-01T00:00:00.000"),
    maxDate: h.date("2099-12-31T00:00:00.000")
  } : null, [h]), w = g.useMemo(() => ({
    utils: h,
    defaultDates: y,
    localeText: v
  }), [y, h, v]);
  return /* @__PURE__ */ x.jsx(ds.Provider, {
    value: w,
    children: c
  });
};
process.env.NODE_ENV !== "production" && (_c.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * Locale for the date library you are using
   */
  adapterLocale: r.any,
  children: r.node,
  /**
   * Date library adapter class function.
   * @see See the localization provider {@link https://mui.com/x/react-date-pickers/getting-started/#setup-your-date-library-adapter date adapter setup section} for more details.
   */
  dateAdapter: r.func,
  /**
   * Formats that are used for any child pickers
   */
  dateFormats: r.shape({
    dayOfMonth: r.string,
    fullDate: r.string,
    fullDateTime: r.string,
    fullDateTime12h: r.string,
    fullDateTime24h: r.string,
    fullDateWithWeekday: r.string,
    fullTime: r.string,
    fullTime12h: r.string,
    fullTime24h: r.string,
    hours12h: r.string,
    hours24h: r.string,
    keyboardDate: r.string,
    keyboardDateTime: r.string,
    keyboardDateTime12h: r.string,
    keyboardDateTime24h: r.string,
    meridiem: r.string,
    minutes: r.string,
    month: r.string,
    monthAndDate: r.string,
    monthAndYear: r.string,
    monthShort: r.string,
    normalDate: r.string,
    normalDateWithWeekday: r.string,
    seconds: r.string,
    shortDate: r.string,
    weekday: r.string,
    weekdayShort: r.string,
    year: r.string
  }),
  /**
   * Date library instance you are using, if it has some global overrides
   * ```jsx
   * dateLibInstance={momentTimeZone}
   * ```
   */
  dateLibInstance: r.any,
  /**
   * Locale for components texts
   */
  localeText: r.object
});
const N$ = (e) => ({
  components: {
    MuiLocalizationProvider: {
      defaultProps: {
        localeText: b({}, e)
      }
    }
  }
}), Oh = {
  // Calendar navigation
  previousMonth: "Previous month",
  nextMonth: "Next month",
  // View navigation
  openPreviousView: "open previous view",
  openNextView: "open next view",
  calendarViewSwitchingButtonAriaLabel: (e) => e === "year" ? "year view is open, switch to calendar view" : "calendar view is open, switch to year view",
  // DateRange placeholders
  start: "Start",
  end: "End",
  // Action bar
  cancelButtonLabel: "Cancel",
  clearButtonLabel: "Clear",
  okButtonLabel: "OK",
  todayButtonLabel: "Today",
  // Toolbar titles
  datePickerToolbarTitle: "Select date",
  dateTimePickerToolbarTitle: "Select date & time",
  timePickerToolbarTitle: "Select time",
  dateRangePickerToolbarTitle: "Select date range",
  // Clock labels
  clockLabelText: (e, t, n) => `Select ${e}. ${t === null ? "No time selected" : `Selected time is ${n.format(t, "fullTime")}`}`,
  hoursClockNumberText: (e) => `${e} hours`,
  minutesClockNumberText: (e) => `${e} minutes`,
  secondsClockNumberText: (e) => `${e} seconds`,
  // Digital clock labels
  selectViewText: (e) => `Select ${e}`,
  // Calendar labels
  calendarWeekNumberHeaderLabel: "Week number",
  calendarWeekNumberHeaderText: "#",
  calendarWeekNumberAriaLabelText: (e) => `Week ${e}`,
  calendarWeekNumberText: (e) => `${e}`,
  // Open picker labels
  openDatePickerDialogue: (e, t) => e !== null && t.isValid(e) ? `Choose date, selected date is ${t.format(e, "fullDate")}` : "Choose date",
  openTimePickerDialogue: (e, t) => e !== null && t.isValid(e) ? `Choose time, selected time is ${t.format(e, "fullTime")}` : "Choose time",
  fieldClearLabel: "Clear value",
  // Table labels
  timeTableLabel: "pick time",
  dateTableLabel: "pick date",
  // Field section placeholders
  fieldYearPlaceholder: (e) => "Y".repeat(e.digitAmount),
  fieldMonthPlaceholder: (e) => e.contentType === "letter" ? "MMMM" : "MM",
  fieldDayPlaceholder: () => "DD",
  fieldWeekDayPlaceholder: (e) => e.contentType === "letter" ? "EEEE" : "EE",
  fieldHoursPlaceholder: () => "hh",
  fieldMinutesPlaceholder: () => "mm",
  fieldSecondsPlaceholder: () => "ss",
  fieldMeridiemPlaceholder: () => "aa"
}, j$ = Oh;
N$(Oh);
const wr = () => {
  const e = g.useContext(ds);
  if (e === null)
    throw new Error(["MUI: Can not find the date and time pickers localization context.", "It looks like you forgot to wrap your component in LocalizationProvider.", "This can also happen if you are bundling multiple versions of the `@mui/x-date-pickers` package"].join(`
`));
  if (e.utils === null)
    throw new Error(["MUI: Can not find the date and time pickers adapter from its localization context.", "It looks like you forgot to pass a `dateAdapter` to your LocalizationProvider."].join(`
`));
  const t = g.useMemo(() => b({}, j$, e.localeText), [e.localeText]);
  return g.useMemo(() => b({}, e, {
    localeText: t
  }), [e, t]);
}, Ze = () => wr().utils, Ta = () => wr().defaultDates, Hn = () => wr().localeText, Qs = (e) => {
  const t = Ze(), n = g.useRef();
  return n.current === void 0 && (n.current = t.dateWithTimezone(void 0, e)), n.current;
}, A$ = rn(/* @__PURE__ */ x.jsx("path", {
  d: "M7 10l5 5 5-5z"
}), "ArrowDropDown"), F$ = rn(/* @__PURE__ */ x.jsx("path", {
  d: "M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6 1.41-1.41z"
}), "ArrowLeft"), V$ = rn(/* @__PURE__ */ x.jsx("path", {
  d: "M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"
}), "ArrowRight"), L$ = rn(/* @__PURE__ */ x.jsx("path", {
  d: "M17 12h-5v5h5v-5zM16 1v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2h-1V1h-2zm3 18H5V8h14v11z"
}), "Calendar");
rn(/* @__PURE__ */ x.jsxs(g.Fragment, {
  children: [/* @__PURE__ */ x.jsx("path", {
    d: "M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"
  }), /* @__PURE__ */ x.jsx("path", {
    d: "M12.5 7H11v6l5.25 3.15.75-1.23-4.5-2.67z"
  })]
}), "Clock");
rn(/* @__PURE__ */ x.jsx("path", {
  d: "M9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"
}), "DateRange");
rn(/* @__PURE__ */ x.jsxs(g.Fragment, {
  children: [/* @__PURE__ */ x.jsx("path", {
    d: "M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"
  }), /* @__PURE__ */ x.jsx("path", {
    d: "M12.5 7H11v6l5.25 3.15.75-1.23-4.5-2.67z"
  })]
}), "Time");
const B$ = rn(/* @__PURE__ */ x.jsx("path", {
  d: "M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"
}), "Clear");
function z$(e) {
  return Pe("MuiPickersArrowSwitcher", e);
}
Ce("MuiPickersArrowSwitcher", ["root", "spacer", "button"]);
const W$ = ["children", "className", "slots", "slotProps", "isNextDisabled", "isNextHidden", "onGoToNext", "nextLabel", "isPreviousDisabled", "isPreviousHidden", "onGoToPrevious", "previousLabel"], U$ = ["ownerState"], H$ = ["ownerState"], q$ = Z("div", {
  name: "MuiPickersArrowSwitcher",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "flex"
}), Y$ = Z("div", {
  name: "MuiPickersArrowSwitcher",
  slot: "Spacer",
  overridesResolver: (e, t) => t.spacer
})(({
  theme: e
}) => ({
  width: e.spacing(3)
})), pp = Z(pr, {
  name: "MuiPickersArrowSwitcher",
  slot: "Button",
  overridesResolver: (e, t) => t.button
})(({
  ownerState: e
}) => b({}, e.hidden && {
  visibility: "hidden"
})), K$ = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"],
    spacer: ["spacer"],
    button: ["button"]
  }, z$, t);
}, G$ = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s, i;
  const c = Zt().direction === "rtl", u = Ee({
    props: t,
    name: "MuiPickersArrowSwitcher"
  }), {
    children: d,
    className: f,
    slots: p,
    slotProps: m,
    isNextDisabled: v,
    isNextHidden: h,
    onGoToNext: y,
    nextLabel: w,
    isPreviousDisabled: C,
    isPreviousHidden: E,
    onGoToPrevious: O,
    previousLabel: T
  } = u, P = ie(u, W$), S = u, j = K$(S), $ = {
    isDisabled: v,
    isHidden: h,
    goTo: y,
    label: w
  }, V = {
    isDisabled: C,
    isHidden: E,
    goTo: O,
    label: T
  }, _ = (o = p == null ? void 0 : p.previousIconButton) != null ? o : pp, L = Ye({
    elementType: _,
    externalSlotProps: m == null ? void 0 : m.previousIconButton,
    additionalProps: {
      size: "medium",
      title: V.label,
      "aria-label": V.label,
      disabled: V.isDisabled,
      edge: "end",
      onClick: V.goTo
    },
    ownerState: b({}, S, {
      hidden: V.isHidden
    }),
    className: j.button
  }), M = (a = p == null ? void 0 : p.nextIconButton) != null ? a : pp, R = Ye({
    elementType: M,
    externalSlotProps: m == null ? void 0 : m.nextIconButton,
    additionalProps: {
      size: "medium",
      title: $.label,
      "aria-label": $.label,
      disabled: $.isDisabled,
      edge: "start",
      onClick: $.goTo
    },
    ownerState: b({}, S, {
      hidden: $.isHidden
    }),
    className: j.button
  }), D = (s = p == null ? void 0 : p.leftArrowIcon) != null ? s : F$, F = Ye({
    elementType: D,
    externalSlotProps: m == null ? void 0 : m.leftArrowIcon,
    additionalProps: {
      fontSize: "inherit"
    },
    ownerState: void 0
  }), z = ie(F, U$), N = (i = p == null ? void 0 : p.rightArrowIcon) != null ? i : V$, q = Ye({
    elementType: N,
    externalSlotProps: m == null ? void 0 : m.rightArrowIcon,
    additionalProps: {
      fontSize: "inherit"
    },
    ownerState: void 0
  }), A = ie(q, H$);
  return /* @__PURE__ */ x.jsxs(q$, b({
    ref: n,
    className: pe(j.root, f),
    ownerState: S
  }, P, {
    children: [/* @__PURE__ */ x.jsx(_, b({}, L, {
      children: c ? /* @__PURE__ */ x.jsx(N, b({}, A)) : /* @__PURE__ */ x.jsx(D, b({}, z))
    })), d ? /* @__PURE__ */ x.jsx(Rt, {
      variant: "subtitle1",
      component: "span",
      children: d
    }) : /* @__PURE__ */ x.jsx(Y$, {
      className: j.spacer,
      ownerState: S
    }), /* @__PURE__ */ x.jsx(M, b({}, R, {
      children: c ? /* @__PURE__ */ x.jsx(D, b({}, z)) : /* @__PURE__ */ x.jsx(N, b({}, A))
    }))]
  }));
}), Ro = (e, t) => e.length !== t.length ? !1 : t.every((n) => e.includes(n)), X$ = ({
  openTo: e,
  defaultOpenTo: t,
  views: n,
  defaultViews: o
}) => {
  const a = n ?? o;
  let s;
  if (e != null)
    s = e;
  else if (a.includes(t))
    s = t;
  else if (a.length > 0)
    s = a[0];
  else
    throw new Error("MUI: The `views` prop must contain at least one view");
  return {
    views: a,
    openTo: s
  };
}, Z$ = ["hours", "minutes", "seconds"], J$ = (e) => Z$.includes(e), fp = (e, t) => t.getHours(e) * 3600 + t.getMinutes(e) * 60 + t.getSeconds(e), Q$ = (e, t) => (n, o) => e ? t.isAfter(n, o) : fp(n, t) > fp(o, t);
let $i = !1;
function Sh({
  onChange: e,
  onViewChange: t,
  openTo: n,
  view: o,
  views: a,
  autoFocus: s,
  focusedView: i,
  onFocusedViewChange: l
}) {
  var c, u;
  process.env.NODE_ENV !== "production" && ($i || (o != null && !a.includes(o) && (console.warn(`MUI: \`view="${o}"\` is not a valid prop.`, `It must be an element of \`views=["${a.join('", "')}"]\`.`), $i = !0), o == null && n != null && !a.includes(n) && (console.warn(`MUI: \`openTo="${n}"\` is not a valid prop.`, `It must be an element of \`views=["${a.join('", "')}"]\`.`), $i = !0)));
  const d = g.useRef(n), f = g.useRef(a), p = g.useRef(a.includes(n) ? n : a[0]), [m, v] = Ht({
    name: "useViews",
    state: "view",
    controlled: o,
    default: p.current
  }), h = g.useRef(s ? m : null), [y, w] = Ht({
    name: "useViews",
    state: "focusedView",
    controlled: i,
    default: h.current
  });
  g.useEffect(() => {
    (d.current && d.current !== n || f.current && f.current.some(($) => !a.includes($))) && (v(a.includes(n) ? n : a[0]), f.current = a, d.current = n);
  }, [n, v, m, a]);
  const C = a.indexOf(m), E = (c = a[C - 1]) != null ? c : null, O = (u = a[C + 1]) != null ? u : null, T = we(($, V) => {
    w(V ? $ : (_) => $ === _ ? null : _), l == null || l($, V);
  }), P = we(($) => {
    T($, !0), $ !== m && (v($), t && t($));
  }), S = we(() => {
    O && P(O);
  }), j = we(($, V, _) => {
    const L = V === "finish", M = _ ? (
      // handles case like `DateTimePicker`, where a view might return a `finish` selection state
      // but we it's not the final view given all `views` -> overall selection state should be `partial`.
      a.indexOf(_) < a.length - 1
    ) : !!O;
    if (e($, L && M ? "partial" : V, _), _ && _ !== m) {
      const D = a[a.indexOf(_) + 1];
      D && P(D);
    } else L && S();
  });
  return {
    view: m,
    setView: P,
    focusedView: y,
    setFocusedView: T,
    nextView: O,
    previousView: E,
    // Always return up to date default view instead of the initial one (i.e. defaultView.current)
    defaultView: a.includes(n) ? n : a[0],
    goToNextView: S,
    setValueAndGoToNextView: j
  };
}
function ek(e, {
  disableFuture: t,
  maxDate: n,
  timezone: o
}) {
  const a = Ze();
  return g.useMemo(() => {
    const s = a.dateWithTimezone(void 0, o), i = a.startOfMonth(t && a.isBefore(s, n) ? s : n);
    return !a.isAfter(i, e);
  }, [t, n, e, a, o]);
}
function tk(e, {
  disablePast: t,
  minDate: n,
  timezone: o
}) {
  const a = Ze();
  return g.useMemo(() => {
    const s = a.dateWithTimezone(void 0, o), i = a.startOfMonth(t && a.isAfter(s, n) ? s : n);
    return !a.isBefore(i, e);
  }, [t, n, e, a, o]);
}
const na = 36, ei = 2, ti = 320, nk = 280, Mc = 334, rk = Z("div")({
  overflow: "hidden",
  width: ti,
  maxHeight: Mc,
  display: "flex",
  flexDirection: "column",
  margin: "0 auto"
}), ps = (e, t, n) => {
  let o = t;
  return o = e.setHours(o, e.getHours(n)), o = e.setMinutes(o, e.getMinutes(n)), o = e.setSeconds(o, e.getSeconds(n)), o;
}, Bo = ({
  date: e,
  disableFuture: t,
  disablePast: n,
  maxDate: o,
  minDate: a,
  isDateDisabled: s,
  utils: i,
  timezone: l
}) => {
  const c = ps(i, i.dateWithTimezone(void 0, l), e);
  n && i.isBefore(a, c) && (a = c), t && i.isAfter(o, c) && (o = c);
  let u = e, d = e;
  for (i.isBefore(e, a) && (u = a, d = null), i.isAfter(e, o) && (d && (d = o), u = null); u || d; ) {
    if (u && i.isAfter(u, o) && (u = null), d && i.isBefore(d, a) && (d = null), u) {
      if (!s(u))
        return u;
      u = i.addDays(u, 1);
    }
    if (d) {
      if (!s(d))
        return d;
      d = i.addDays(d, -1);
    }
  }
  return null;
}, ok = (e, t) => t == null || !e.isValid(t) ? null : t, Gt = (e, t, n) => t == null || !e.isValid(t) ? n : t, ak = (e, t, n) => !e.isValid(t) && t != null && !e.isValid(n) && n != null ? !0 : e.isEqual(t, n), Ic = (e, t) => {
  const o = [e.startOfYear(t)];
  for (; o.length < 12; ) {
    const a = o[o.length - 1];
    o.push(e.addMonths(a, 1));
  }
  return o;
}, Ph = (e, t, n) => n === "date" ? e.startOfDay(e.dateWithTimezone(void 0, t)) : e.dateWithTimezone(void 0, t), sk = ["year", "month", "day"], mp = (e) => sk.includes(e), Nc = (e, {
  format: t,
  views: n
}, o) => {
  if (t != null)
    return t;
  const a = e.formats;
  return Ro(n, ["year"]) ? a.year : Ro(n, ["month"]) ? a.month : Ro(n, ["day"]) ? a.dayOfMonth : Ro(n, ["month", "year"]) ? `${a.month} ${a.year}` : Ro(n, ["day", "month"]) ? `${a.month} ${a.dayOfMonth}` : o ? /en/.test(e.getCurrentLocaleCode()) ? a.normalDateWithWeekday : a.normalDate : a.keyboardDate;
}, ik = (e, t) => {
  const n = e.startOfWeek(t);
  return [0, 1, 2, 3, 4, 5, 6].map((o) => e.addDays(n, o));
}, jc = ({
  timezone: e,
  value: t,
  defaultValue: n,
  onChange: o,
  valueManager: a
}) => {
  var s, i;
  const l = Ze(), c = g.useRef(n), u = (s = t ?? c.current) != null ? s : a.emptyValue, d = g.useMemo(() => a.getTimezone(l, u), [l, a, u]), f = we((h) => d == null ? h : a.setTimezone(l, d, h)), p = (i = e ?? d) != null ? i : "default", m = g.useMemo(() => a.setTimezone(l, p, u), [a, l, p, u]), v = we((h, ...y) => {
    const w = f(h);
    o == null || o(w, ...y);
  });
  return {
    value: m,
    handleValueChange: v,
    timezone: p
  };
}, Ac = ({
  name: e,
  timezone: t,
  value: n,
  defaultValue: o,
  onChange: a,
  valueManager: s
}) => {
  const [i, l] = Ht({
    name: e,
    state: "value",
    controlled: n,
    default: o ?? s.emptyValue
  }), c = we((u, ...d) => {
    l(u), a == null || a(u, ...d);
  });
  return jc({
    timezone: t,
    value: i,
    defaultValue: void 0,
    onChange: c,
    valueManager: s
  });
}, cn = {
  year: 1,
  month: 2,
  day: 3,
  hours: 4,
  minutes: 5,
  seconds: 6,
  milliseconds: 7
}, lk = (e) => Math.max(...e.map((t) => {
  var n;
  return (n = cn[t.type]) != null ? n : 1;
})), Do = (e, t, n) => {
  if (t === cn.year)
    return e.startOfYear(n);
  if (t === cn.month)
    return e.startOfMonth(n);
  if (t === cn.day)
    return e.startOfDay(n);
  let o = n;
  return t < cn.minutes && (o = e.setMinutes(o, 0)), t < cn.seconds && (o = e.setSeconds(o, 0)), t < cn.milliseconds && (o = e.setMilliseconds(o, 0)), o;
}, ck = ({
  props: e,
  utils: t,
  granularity: n,
  timezone: o,
  getTodayDate: a
}) => {
  var s;
  let i = a ? a() : Do(t, n, Ph(t, o));
  e.minDate != null && t.isAfterDay(e.minDate, i) && (i = Do(t, n, e.minDate)), e.maxDate != null && t.isBeforeDay(e.maxDate, i) && (i = Do(t, n, e.maxDate));
  const l = Q$((s = e.disableIgnoringDatePartForTimeValidation) != null ? s : !1, t);
  return e.minTime != null && l(e.minTime, i) && (i = Do(t, n, e.disableIgnoringDatePartForTimeValidation ? e.minTime : ps(t, i, e.minTime))), e.maxTime != null && l(i, e.maxTime) && (i = Do(t, n, e.disableIgnoringDatePartForTimeValidation ? e.maxTime : ps(t, i, e.maxTime))), i;
}, Fc = (e, t) => {
  const n = e.formatTokenMap[t];
  if (n == null)
    throw new Error([`MUI: The token "${t}" is not supported by the Date and Time Pickers.`, "Please try using another token or open an issue on https://github.com/mui/mui-x/issues/new/choose if you think it should be supported."].join(`
`));
  return typeof n == "string" ? {
    type: n,
    contentType: n === "meridiem" ? "letter" : "digit",
    maxLength: void 0
  } : {
    type: n.sectionType,
    contentType: n.contentType,
    maxLength: n.maxLength
  };
}, uk = (e) => {
  switch (e) {
    case "ArrowUp":
      return 1;
    case "ArrowDown":
      return -1;
    case "PageUp":
      return 5;
    case "PageDown":
      return -5;
    default:
      return 0;
  }
}, ni = (e, t, n) => {
  const o = [], a = e.dateWithTimezone(void 0, t), s = e.startOfWeek(a), i = e.endOfWeek(a);
  let l = s;
  for (; e.isBefore(l, i); )
    o.push(l), l = e.addDays(l, 1);
  return o.map((c) => e.formatByString(c, n));
}, Rh = (e, t, n, o) => {
  switch (n) {
    case "month":
      return Ic(e, e.dateWithTimezone(void 0, t)).map((a) => e.formatByString(a, o));
    case "weekDay":
      return ni(e, t, o);
    case "meridiem": {
      const a = e.dateWithTimezone(void 0, t);
      return [e.startOfDay(a), e.endOfDay(a)].map((s) => e.formatByString(s, o));
    }
    default:
      return [];
  }
}, Dh = (e, t, n) => {
  let o = t;
  for (o = Number(o).toString(); o.length < n; )
    o = `0${o}`;
  return o;
}, $h = (e, t, n, o, a) => {
  if (process.env.NODE_ENV !== "production" && a.type !== "day" && a.contentType === "digit-with-letter")
    throw new Error([`MUI: The token "${a.format}" is a digit format with letter in it.'
             This type of format is only supported for 'day' sections`].join(`
`));
  if (a.type === "day" && a.contentType === "digit-with-letter") {
    const i = e.setDate(o.longestMonth, n);
    return e.formatByString(i, a.format);
  }
  const s = n.toString();
  return a.hasLeadingZerosInInput ? Dh(e, s, a.maxLength) : s;
}, dk = (e, t, n, o, a, s, i) => {
  const l = uk(o), c = o === "Home", u = o === "End", d = n.value === "" || c || u, f = () => {
    const m = a[n.type]({
      currentDate: s,
      format: n.format,
      contentType: n.contentType
    }), v = (C) => $h(e, t, C, m, n), h = n.type === "minutes" && i != null && i.minutesStep ? i.minutesStep : 1;
    let w = parseInt(n.value, 10) + l * h;
    if (d) {
      if (n.type === "year" && !u && !c)
        return e.formatByString(e.dateWithTimezone(void 0, t), n.format);
      l > 0 || c ? w = m.minimum : w = m.maximum;
    }
    return w % h !== 0 && ((l < 0 || c) && (w += h - (h + w) % h), (l > 0 || u) && (w -= w % h)), w > m.maximum ? v(m.minimum + (w - m.maximum - 1) % (m.maximum - m.minimum + 1)) : w < m.minimum ? v(m.maximum - (m.minimum - w - 1) % (m.maximum - m.minimum + 1)) : v(w);
  }, p = () => {
    const m = Rh(e, t, n.type, n.format);
    if (m.length === 0)
      return n.value;
    if (d)
      return l > 0 || c ? m[0] : m[m.length - 1];
    const h = (m.indexOf(n.value) + m.length + l) % m.length;
    return m[h];
  };
  return n.contentType === "digit" || n.contentType === "digit-with-letter" ? f() : p();
}, Vc = (e, t) => {
  let n = e.value || e.placeholder;
  const o = t === "non-input" ? e.hasLeadingZerosInFormat : e.hasLeadingZerosInInput;
  return t === "non-input" && e.hasLeadingZerosInInput && !e.hasLeadingZerosInFormat && (n = Number(n).toString()), ["input-rtl", "input-ltr"].includes(t) && e.contentType === "digit" && !o && n.length === 1 && (n = `${n}‎`), t === "input-rtl" && (n = `⁨${n}⁩`), n;
}, Lr = (e) => e.replace(/[\u2066\u2067\u2068\u2069]/g, ""), kh = (e, t) => {
  let n = 0, o = t ? 1 : 0;
  const a = [];
  for (let s = 0; s < e.length; s += 1) {
    const i = e[s], l = Vc(i, t ? "input-rtl" : "input-ltr"), c = `${i.startSeparator}${l}${i.endSeparator}`, u = Lr(c).length, d = c.length, f = Lr(l), p = o + l.indexOf(f[0]) + i.startSeparator.length, m = p + f.length;
    a.push(b({}, i, {
      start: n,
      end: n + u,
      startInInput: p,
      endInInput: m
    })), n += u, o += d;
  }
  return a;
}, pk = (e, t, n, o, a) => {
  switch (o.type) {
    case "year":
      return n.fieldYearPlaceholder({
        digitAmount: e.formatByString(e.dateWithTimezone(void 0, t), a).length,
        format: a
      });
    case "month":
      return n.fieldMonthPlaceholder({
        contentType: o.contentType,
        format: a
      });
    case "day":
      return n.fieldDayPlaceholder({
        format: a
      });
    case "weekDay":
      return n.fieldWeekDayPlaceholder({
        contentType: o.contentType,
        format: a
      });
    case "hours":
      return n.fieldHoursPlaceholder({
        format: a
      });
    case "minutes":
      return n.fieldMinutesPlaceholder({
        format: a
      });
    case "seconds":
      return n.fieldSecondsPlaceholder({
        format: a
      });
    case "meridiem":
      return n.fieldMeridiemPlaceholder({
        format: a
      });
    default:
      return a;
  }
}, hp = (e, t, n, o) => {
  if (process.env.NODE_ENV !== "production" && Fc(e, n).type === "weekDay")
    throw new Error("changeSectionValueFormat doesn't support week day formats");
  return e.formatByString(e.parse(t, n), o);
}, _h = (e, t, n) => e.formatByString(e.dateWithTimezone(void 0, t), n).length === 4, Mh = (e, t, n, o, a) => {
  if (n !== "digit")
    return !1;
  const s = e.dateWithTimezone(void 0, t);
  switch (o) {
    case "year":
      return _h(e, t, a) ? e.formatByString(e.setYear(s, 1), a) === "0001" : e.formatByString(e.setYear(s, 2001), a) === "01";
    case "month":
      return e.formatByString(e.startOfYear(s), a).length > 1;
    case "day":
      return e.formatByString(e.startOfMonth(s), a).length > 1;
    case "weekDay":
      return e.formatByString(e.startOfWeek(s), a).length > 1;
    case "hours":
      return e.formatByString(e.setHours(s, 1), a).length > 1;
    case "minutes":
      return e.formatByString(e.setMinutes(s, 1), a).length > 1;
    case "seconds":
      return e.formatByString(e.setSeconds(s, 1), a).length > 1;
    default:
      throw new Error("Invalid section type");
  }
}, fk = (e, t) => {
  const n = [], {
    start: o,
    end: a
  } = e.escapedCharacters, s = new RegExp(`(\\${o}[^\\${a}]*\\${a})+`, "g");
  let i = null;
  for (; i = s.exec(t); )
    n.push({
      start: i.index,
      end: s.lastIndex - 1
    });
  return n;
}, bp = (e, t, n, o, a, s, i, l) => {
  let c = "";
  const u = [], d = e.date(), f = (E) => {
    if (E === "")
      return null;
    const O = Fc(e, E), T = Mh(e, t, O.contentType, O.type, E), P = i ? T : O.contentType === "digit", S = a != null && e.isValid(a);
    let j = S ? e.formatByString(a, E) : "", $ = null;
    if (P)
      if (T)
        $ = j === "" ? e.formatByString(d, E).length : j.length;
      else {
        if (O.maxLength == null)
          throw new Error(`MUI: The token ${E} should have a 'maxDigitNumber' property on it's adapter`);
        $ = O.maxLength, S && (j = Dh(e, j, $));
      }
    return u.push(b({}, O, {
      format: E,
      maxLength: $,
      value: j,
      placeholder: pk(e, t, n, O, E),
      hasLeadingZeros: T,
      hasLeadingZerosInFormat: T,
      hasLeadingZerosInInput: P,
      startSeparator: u.length === 0 ? c : "",
      endSeparator: "",
      modified: !1
    })), null;
  };
  let p = 10, m = o, v = e.expandFormat(o);
  for (; v !== m; )
    if (m = v, v = e.expandFormat(m), p -= 1, p < 0)
      throw new Error("MUI: The format expansion seems to be  enter in an infinite loop. Please open an issue with the format passed to the picker component");
  const h = v, y = fk(e, h), w = new RegExp(`^(${Object.keys(e.formatTokenMap).sort((E, O) => O.length - E.length).join("|")})`, "g");
  let C = "";
  for (let E = 0; E < h.length; E += 1) {
    const O = y.find(($) => $.start <= E && $.end >= E), T = h[E], P = O != null, S = `${C}${h.slice(E)}`, j = w.test(S);
    !P && T.match(/([A-Za-z]+)/) && j ? (C = S.slice(0, w.lastIndex), E += w.lastIndex - 1) : P && (O == null ? void 0 : O.start) === E || (O == null ? void 0 : O.end) === E || (f(C), C = "", u.length === 0 ? c += T : u[u.length - 1].endSeparator += T);
  }
  return f(C), u.map((E) => {
    const O = (T) => {
      let P = T;
      return l && P !== null && P.includes(" ") && (P = `⁩${P}⁦`), s === "spacious" && ["/", ".", "-"].includes(P) && (P = ` ${P} `), P;
    };
    return E.startSeparator = O(E.startSeparator), E.endSeparator = O(E.endSeparator), E;
  });
}, mk = (e, t) => {
  const n = t.some((l) => l.type === "day"), o = [], a = [];
  for (let l = 0; l < t.length; l += 1) {
    const c = t[l];
    n && c.type === "weekDay" || (o.push(c.format), a.push(Vc(c, "non-input")));
  }
  const s = o.join(" "), i = a.join(" ");
  return e.parse(i, s);
}, hk = (e, t) => {
  const o = e.map((a) => {
    const s = Vc(a, t ? "input-rtl" : "input-ltr");
    return `${a.startSeparator}${s}${a.endSeparator}`;
  }).join("");
  return t ? `⁦${o}⁩` : o;
}, bk = (e, t) => {
  const n = e.dateWithTimezone(void 0, t), o = e.endOfYear(n), a = e.endOfDay(n), {
    maxDaysInMonth: s,
    longestMonth: i
  } = Ic(e, n).reduce((l, c) => {
    const u = e.getDaysInMonth(c);
    return u > l.maxDaysInMonth ? {
      maxDaysInMonth: u,
      longestMonth: c
    } : l;
  }, {
    maxDaysInMonth: 0,
    longestMonth: null
  });
  return {
    year: ({
      format: l
    }) => ({
      minimum: 0,
      maximum: _h(e, t, l) ? 9999 : 99
    }),
    month: () => ({
      minimum: 1,
      // Assumption: All years have the same amount of months
      maximum: e.getMonth(o) + 1
    }),
    day: ({
      currentDate: l
    }) => ({
      minimum: 1,
      maximum: l != null && e.isValid(l) ? e.getDaysInMonth(l) : s,
      longestMonth: i
    }),
    weekDay: ({
      format: l,
      contentType: c
    }) => {
      if (c === "digit") {
        const u = ni(e, t, l).map(Number);
        return {
          minimum: Math.min(...u),
          maximum: Math.max(...u)
        };
      }
      return {
        minimum: 1,
        maximum: 7
      };
    },
    hours: ({
      format: l
    }) => {
      const c = e.getHours(a);
      return e.formatByString(e.endOfDay(n), l) !== c.toString() ? {
        minimum: 1,
        maximum: Number(e.formatByString(e.startOfDay(n), l))
      } : {
        minimum: 0,
        maximum: c
      };
    },
    minutes: () => ({
      minimum: 0,
      // Assumption: All years have the same amount of minutes
      maximum: e.getMinutes(a)
    }),
    seconds: () => ({
      minimum: 0,
      // Assumption: All years have the same amount of seconds
      maximum: e.getSeconds(a)
    }),
    meridiem: () => ({
      minimum: 0,
      maximum: 0
    })
  };
};
let gp = !1;
const yp = (e, t) => {
  if (process.env.NODE_ENV !== "production" && !gp) {
    const n = [];
    ["date", "date-time"].includes(t) && n.push("weekDay", "day", "month", "year"), ["time", "date-time"].includes(t) && n.push("hours", "minutes", "seconds", "meridiem");
    const o = e.find((a) => !n.includes(a.type));
    o && (console.warn(`MUI: The field component you are using is not compatible with the "${o.type} date section.`, `The supported date sections are ["${n.join('", "')}"]\`.`), gp = !0);
  }
}, gk = (e, t, n, o, a) => {
  switch (n.type) {
    case "year":
      return e.setYear(a, e.getYear(o));
    case "month":
      return e.setMonth(a, e.getMonth(o));
    case "weekDay": {
      const s = ni(e, t, n.format), i = e.formatByString(o, n.format), l = s.indexOf(i), u = s.indexOf(n.value) - l;
      return e.addDays(o, u);
    }
    case "day":
      return e.setDate(a, e.getDate(o));
    case "meridiem": {
      const s = e.getHours(o) < 12, i = e.getHours(a);
      return s && i >= 12 ? e.addHours(a, -12) : !s && i < 12 ? e.addHours(a, 12) : a;
    }
    case "hours":
      return e.setHours(a, e.getHours(o));
    case "minutes":
      return e.setMinutes(a, e.getMinutes(o));
    case "seconds":
      return e.setSeconds(a, e.getSeconds(o));
    default:
      return a;
  }
}, vp = {
  year: 1,
  month: 2,
  day: 3,
  weekDay: 4,
  hours: 5,
  minutes: 6,
  seconds: 7,
  meridiem: 8
}, xp = (e, t, n, o, a, s) => (
  // cloning sections before sort to avoid mutating it
  [...o].sort((i, l) => vp[i.type] - vp[l.type]).reduce((i, l) => !s || l.modified ? gk(e, t, l, n, i) : i, a)
), yk = () => navigator.userAgent.toLowerCase().indexOf("android") > -1, vk = (e, t) => {
  const n = {};
  if (!t)
    return e.forEach((c, u) => {
      const d = u === 0 ? null : u - 1, f = u === e.length - 1 ? null : u + 1;
      n[u] = {
        leftIndex: d,
        rightIndex: f
      };
    }), {
      neighbors: n,
      startIndex: 0,
      endIndex: e.length - 1
    };
  const o = {}, a = {};
  let s = 0, i = 0, l = e.length - 1;
  for (; l >= 0; ) {
    i = e.findIndex(
      // eslint-disable-next-line @typescript-eslint/no-loop-func
      (c, u) => {
        var d;
        return u >= s && ((d = c.endSeparator) == null ? void 0 : d.includes(" ")) && // Special case where the spaces were not there in the initial input
        c.endSeparator !== " / ";
      }
    ), i === -1 && (i = e.length - 1);
    for (let c = i; c >= s; c -= 1)
      a[c] = l, o[l] = c, l -= 1;
    s = i + 1;
  }
  return e.forEach((c, u) => {
    const d = a[u], f = d === 0 ? null : o[d - 1], p = d === e.length - 1 ? null : o[d + 1];
    n[u] = {
      leftIndex: f,
      rightIndex: p
    };
  }), {
    neighbors: n,
    startIndex: o[0],
    endIndex: o[e.length - 1]
  };
}, xk = ["value", "referenceDate"], Cn = {
  emptyValue: null,
  getTodayValue: Ph,
  getInitialReferenceValue: (e) => {
    let {
      value: t,
      referenceDate: n
    } = e, o = ie(e, xk);
    return t != null && o.utils.isValid(t) ? t : n ?? ck(o);
  },
  cleanValue: ok,
  areValuesEqual: ak,
  isSameError: (e, t) => e === t,
  hasError: (e) => e != null,
  defaultErrorState: null,
  getTimezone: (e, t) => t == null || !e.isValid(t) ? null : e.getTimezone(t),
  setTimezone: (e, t, n) => n == null ? null : e.setTimezone(n, t)
}, Tk = {
  updateReferenceValue: (e, t, n) => t == null || !e.isValid(t) ? n : t,
  getSectionsFromValue: (e, t, n, o, a) => !e.isValid(t) && !!n ? n : kh(a(t), o),
  getValueStrFromSections: hk,
  getActiveDateManager: (e, t) => ({
    date: t.value,
    referenceDate: t.referenceValue,
    getSections: (n) => n,
    getNewValuesFromNewActiveDate: (n) => ({
      value: n,
      referenceValue: n == null || !e.isValid(n) ? t.referenceValue : n
    })
  }),
  parseValueStr: (e, t, n) => n(e.trim(), t)
}, Ih = (e) => {
  if (e !== void 0)
    return Object.keys(e).reduce((t, n) => b({}, t, {
      [`${n.slice(0, 1).toLowerCase()}${n.slice(1)}`]: e[n]
    }), {});
};
function wk(e) {
  return Pe("MuiPickersDay", e);
}
const Jn = Ce("MuiPickersDay", ["root", "dayWithMargin", "dayOutsideMonth", "hiddenDaySpacingFiller", "today", "selected", "disabled"]), Ek = ["autoFocus", "className", "day", "disabled", "disableHighlightToday", "disableMargin", "hidden", "isAnimating", "onClick", "onDaySelect", "onFocus", "onBlur", "onKeyDown", "onMouseDown", "onMouseEnter", "outsideCurrentMonth", "selected", "showDaysOutsideCurrentMonth", "children", "today", "isFirstVisibleCell", "isLastVisibleCell"], Ck = (e) => {
  const {
    selected: t,
    disableMargin: n,
    disableHighlightToday: o,
    today: a,
    disabled: s,
    outsideCurrentMonth: i,
    showDaysOutsideCurrentMonth: l,
    classes: c
  } = e, u = i && !l;
  return Se({
    root: ["root", t && !u && "selected", s && "disabled", !n && "dayWithMargin", !o && a && "today", i && l && "dayOutsideMonth", u && "hiddenDaySpacingFiller"],
    hiddenDaySpacingFiller: ["hiddenDaySpacingFiller"]
  }, wk, c);
}, Nh = ({
  theme: e,
  ownerState: t
}) => b({}, e.typography.caption, {
  width: na,
  height: na,
  borderRadius: "50%",
  padding: 0,
  // explicitly setting to `transparent` to avoid potentially getting impacted by change from the overridden component
  backgroundColor: "transparent",
  transition: e.transitions.create("background-color", {
    duration: e.transitions.duration.short
  }),
  color: (e.vars || e).palette.text.primary,
  "@media (pointer: fine)": {
    "&:hover": {
      backgroundColor: e.vars ? `rgba(${e.vars.palette.primary.mainChannel} / ${e.vars.palette.action.hoverOpacity})` : Xr(e.palette.primary.main, e.palette.action.hoverOpacity)
    }
  },
  "&:focus": {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.primary.mainChannel} / ${e.vars.palette.action.focusOpacity})` : Xr(e.palette.primary.main, e.palette.action.focusOpacity),
    [`&.${Jn.selected}`]: {
      willChange: "background-color",
      backgroundColor: (e.vars || e).palette.primary.dark
    }
  },
  [`&.${Jn.selected}`]: {
    color: (e.vars || e).palette.primary.contrastText,
    backgroundColor: (e.vars || e).palette.primary.main,
    fontWeight: e.typography.fontWeightMedium,
    "&:hover": {
      willChange: "background-color",
      backgroundColor: (e.vars || e).palette.primary.dark
    }
  },
  [`&.${Jn.disabled}:not(.${Jn.selected})`]: {
    color: (e.vars || e).palette.text.disabled
  },
  [`&.${Jn.disabled}&.${Jn.selected}`]: {
    opacity: 0.6
  }
}, !t.disableMargin && {
  margin: `0 ${ei}px`
}, t.outsideCurrentMonth && t.showDaysOutsideCurrentMonth && {
  color: (e.vars || e).palette.text.secondary
}, !t.disableHighlightToday && t.today && {
  [`&:not(.${Jn.selected})`]: {
    border: `1px solid ${(e.vars || e).palette.text.secondary}`
  }
}), jh = (e, t) => {
  const {
    ownerState: n
  } = e;
  return [t.root, !n.disableMargin && t.dayWithMargin, !n.disableHighlightToday && n.today && t.today, !n.outsideCurrentMonth && n.showDaysOutsideCurrentMonth && t.dayOutsideMonth, n.outsideCurrentMonth && !n.showDaysOutsideCurrentMonth && t.hiddenDaySpacingFiller];
}, Ok = Z(lr, {
  name: "MuiPickersDay",
  slot: "Root",
  overridesResolver: jh
})(Nh), Sk = Z("div", {
  name: "MuiPickersDay",
  slot: "Root",
  overridesResolver: jh
})(({
  theme: e,
  ownerState: t
}) => b({}, Nh({
  theme: e,
  ownerState: t
}), {
  // visibility: 'hidden' does not work here as it hides the element from screen readers as well
  opacity: 0,
  pointerEvents: "none"
})), $o = () => {
}, Ah = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiPickersDay"
  }), {
    autoFocus: a = !1,
    className: s,
    day: i,
    disabled: l = !1,
    disableHighlightToday: c = !1,
    disableMargin: u = !1,
    isAnimating: d,
    onClick: f,
    onDaySelect: p,
    onFocus: m = $o,
    onBlur: v = $o,
    onKeyDown: h = $o,
    onMouseDown: y = $o,
    onMouseEnter: w = $o,
    outsideCurrentMonth: C,
    selected: E = !1,
    showDaysOutsideCurrentMonth: O = !1,
    children: T,
    today: P = !1
  } = o, S = ie(o, Ek), j = b({}, o, {
    autoFocus: a,
    disabled: l,
    disableHighlightToday: c,
    disableMargin: u,
    selected: E,
    showDaysOutsideCurrentMonth: O,
    today: P
  }), $ = Ck(j), V = Ze(), _ = g.useRef(null), L = Ke(_, n);
  ft(() => {
    a && !l && !d && !C && _.current.focus();
  }, [a, l, d, C]);
  const M = (D) => {
    y(D), C && D.preventDefault();
  }, R = (D) => {
    l || p(i), C && D.currentTarget.focus(), f && f(D);
  };
  return C && !O ? /* @__PURE__ */ x.jsx(Sk, {
    className: pe($.root, $.hiddenDaySpacingFiller, s),
    ownerState: j,
    role: S.role
  }) : /* @__PURE__ */ x.jsx(Ok, b({
    className: pe($.root, s),
    ref: L,
    centerRipple: !0,
    disabled: l,
    tabIndex: E ? 0 : -1,
    onKeyDown: (D) => h(D, i),
    onFocus: (D) => m(D, i),
    onBlur: (D) => v(D, i),
    onMouseEnter: (D) => w(D, i),
    onClick: R,
    onMouseDown: M
  }, S, {
    ownerState: j,
    children: T || V.format(i, "dayOfMonth")
  }));
});
process.env.NODE_ENV !== "production" && (Ah.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * A ref for imperative actions.
   * It currently only supports `focusVisible()` action.
   */
  action: r.oneOfType([r.func, r.shape({
    current: r.shape({
      focusVisible: r.func.isRequired
    })
  })]),
  /**
   * If `true`, the ripples are centered.
   * They won't start at the cursor interaction position.
   * @default false
   */
  centerRipple: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  className: r.string,
  component: r.elementType,
  /**
   * The date to show.
   */
  day: r.any.isRequired,
  /**
   * If `true`, renders as disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, today's date is rendering without highlighting with circle.
   * @default false
   */
  disableHighlightToday: r.bool,
  /**
   * If `true`, days are rendering without margin. Useful for displaying linked range of days.
   * @default false
   */
  disableMargin: r.bool,
  /**
   * If `true`, the ripple effect is disabled.
   *
   * ⚠️ Without a ripple there is no styling for :focus-visible by default. Be sure
   * to highlight the element by applying separate styles with the `.Mui-focusVisible` class.
   * @default false
   */
  disableRipple: r.bool,
  /**
   * If `true`, the touch ripple effect is disabled.
   * @default false
   */
  disableTouchRipple: r.bool,
  /**
   * If `true`, the base button will have a keyboard focus ripple.
   * @default false
   */
  focusRipple: r.bool,
  /**
   * This prop can help identify which element has keyboard focus.
   * The class name will be applied when the element gains the focus through keyboard interaction.
   * It's a polyfill for the [CSS :focus-visible selector](https://drafts.csswg.org/selectors-4/#the-focus-visible-pseudo).
   * The rationale for using this feature [is explained here](https://github.com/WICG/focus-visible/blob/HEAD/explainer.md).
   * A [polyfill can be used](https://github.com/WICG/focus-visible) to apply a `focus-visible` class to other components
   * if needed.
   */
  focusVisibleClassName: r.string,
  isAnimating: r.bool,
  /**
   * If `true`, day is the first visible cell of the month.
   * Either the first day of the month or the first day of the week depending on `showDaysOutsideCurrentMonth`.
   */
  isFirstVisibleCell: r.bool.isRequired,
  /**
   * If `true`, day is the last visible cell of the month.
   * Either the last day of the month or the last day of the week depending on `showDaysOutsideCurrentMonth`.
   */
  isLastVisibleCell: r.bool.isRequired,
  onBlur: r.func,
  onDaySelect: r.func.isRequired,
  onFocus: r.func,
  /**
   * Callback fired when the component is focused with a keyboard.
   * We trigger a `onFocus` callback too.
   */
  onFocusVisible: r.func,
  onKeyDown: r.func,
  onMouseEnter: r.func,
  /**
   * If `true`, day is outside of month and will be hidden.
   */
  outsideCurrentMonth: r.bool.isRequired,
  /**
   * If `true`, renders as selected.
   * @default false
   */
  selected: r.bool,
  /**
   * If `true`, days outside the current month are rendered:
   *
   * - if `fixedWeekNumber` is defined, renders days to have the weeks requested.
   *
   * - if `fixedWeekNumber` is not defined, renders day to fill the first and last week of the current month.
   *
   * - ignored if `calendars` equals more than `1` on range pickers.
   * @default false
   */
  showDaysOutsideCurrentMonth: r.bool,
  style: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * @default 0
   */
  tabIndex: r.number,
  /**
   * If `true`, renders as today date.
   * @default false
   */
  today: r.bool,
  /**
   * Props applied to the `TouchRipple` element.
   */
  TouchRippleProps: r.object,
  /**
   * A ref that points to the `TouchRipple` element.
   */
  touchRippleRef: r.oneOfType([r.func, r.shape({
    current: r.shape({
      pulsate: r.func.isRequired,
      start: r.func.isRequired,
      stop: r.func.isRequired
    })
  })])
});
const Pk = /* @__PURE__ */ g.memo(Ah);
function Fh(e, t, n, o) {
  const {
    value: a,
    onError: s
  } = e, i = wr(), l = g.useRef(o), c = t({
    adapter: i,
    value: a,
    props: e
  });
  return g.useEffect(() => {
    s && !n(c, l.current) && s(c, a), l.current = c;
  }, [n, s, l, c, a]), c;
}
const Rk = (e) => {
  const t = Ze(), n = Hn(), o = wr(), s = Zt().direction === "rtl", {
    valueManager: i,
    fieldValueManager: l,
    valueType: c,
    validator: u,
    internalProps: d,
    internalProps: {
      value: f,
      defaultValue: p,
      referenceDate: m,
      onChange: v,
      format: h,
      formatDensity: y = "dense",
      selectedSections: w,
      onSelectedSectionsChange: C,
      shouldRespectLeadingZeros: E = !1,
      timezone: O
    }
  } = e, {
    timezone: T,
    value: P,
    handleValueChange: S
  } = jc({
    timezone: O,
    value: f,
    defaultValue: p,
    onChange: v,
    valueManager: i
  }), j = g.useMemo(() => bk(t, T), [t, T]), $ = g.useCallback((B, G = null) => l.getSectionsFromValue(t, B, G, s, (ee) => bp(t, T, n, h, ee, y, E, s)), [l, h, n, s, E, t, y, T]), V = g.useMemo(() => l.getValueStrFromSections($(i.emptyValue), s), [l, $, i.emptyValue, s]), [_, L] = g.useState(() => {
    const B = $(P);
    yp(B, c);
    const G = {
      sections: B,
      value: P,
      referenceValue: i.emptyValue,
      tempValueStrAndroid: null
    }, ee = lk(B), W = i.getInitialReferenceValue({
      referenceDate: m,
      value: P,
      utils: t,
      props: d,
      granularity: ee,
      timezone: T
    });
    return b({}, G, {
      referenceValue: W
    });
  }), [M, R] = Ht({
    controlled: w,
    default: null,
    name: "useField",
    state: "selectedSectionIndexes"
  }), D = (B) => {
    R(B), C == null || C(B), L((G) => b({}, G, {
      selectedSectionQuery: null
    }));
  }, F = g.useMemo(() => {
    if (M == null)
      return null;
    if (M === "all")
      return {
        startIndex: 0,
        endIndex: _.sections.length - 1,
        shouldSelectBoundarySelectors: !0
      };
    if (typeof M == "number")
      return {
        startIndex: M,
        endIndex: M
      };
    if (typeof M == "string") {
      const B = _.sections.findIndex((G) => G.type === M);
      return {
        startIndex: B,
        endIndex: B
      };
    }
    return M;
  }, [M, _.sections]), z = ({
    value: B,
    referenceValue: G,
    sections: ee
  }) => {
    if (L((J) => b({}, J, {
      sections: ee,
      value: B,
      referenceValue: G,
      tempValueStrAndroid: null
    })), i.areValuesEqual(t, _.value, B))
      return;
    const W = {
      validationError: u({
        adapter: o,
        value: B,
        props: b({}, d, {
          value: B,
          timezone: T
        })
      })
    };
    S(B, W);
  }, N = (B, G) => {
    const ee = [..._.sections];
    return ee[B] = b({}, ee[B], {
      value: G,
      modified: !0
    }), kh(ee, s);
  }, q = () => {
    z({
      value: i.emptyValue,
      referenceValue: _.referenceValue,
      sections: $(i.emptyValue)
    });
  }, A = () => {
    if (F == null)
      return;
    const B = _.sections[F.startIndex], G = l.getActiveDateManager(t, _, B), W = G.getSections(_.sections).filter((X) => X.value !== "").length === (B.value === "" ? 0 : 1), J = N(F.startIndex, ""), se = W ? null : t.date(/* @__PURE__ */ new Date("")), le = G.getNewValuesFromNewActiveDate(se);
    (se != null && !t.isValid(se)) != (G.date != null && !t.isValid(G.date)) ? z(b({}, le, {
      sections: J
    })) : L((X) => b({}, X, le, {
      sections: J,
      tempValueStrAndroid: null
    }));
  }, H = (B) => {
    const G = (J, se) => {
      const le = t.parse(J, h);
      if (le == null || !t.isValid(le))
        return null;
      const X = bp(t, T, n, h, le, y, E, s);
      return xp(t, T, le, X, se, !1);
    }, ee = l.parseValueStr(B, _.referenceValue, G), W = l.updateReferenceValue(t, ee, _.referenceValue);
    z({
      value: ee,
      referenceValue: W,
      sections: $(ee, _.sections)
    });
  }, te = ({
    activeSection: B,
    newSectionValue: G,
    shouldGoToNextSection: ee
  }) => {
    ee && F && F.startIndex < _.sections.length - 1 ? D(F.startIndex + 1) : F && F.startIndex !== F.endIndex && D(F.startIndex);
    const W = l.getActiveDateManager(t, _, B), J = N(F.startIndex, G), se = W.getSections(J), le = mk(t, se);
    let X, U;
    if (le != null && t.isValid(le)) {
      const K = xp(t, T, le, se, W.referenceDate, !0);
      X = W.getNewValuesFromNewActiveDate(K), U = !0;
    } else
      X = W.getNewValuesFromNewActiveDate(le), U = (le != null && !t.isValid(le)) != (W.date != null && !t.isValid(W.date));
    return U ? z(b({}, X, {
      sections: J
    })) : L((K) => b({}, K, X, {
      sections: J,
      tempValueStrAndroid: null
    }));
  }, re = (B) => L((G) => b({}, G, {
    tempValueStrAndroid: B
  }));
  return g.useEffect(() => {
    const B = $(_.value);
    yp(B, c), L((G) => b({}, G, {
      sections: B
    }));
  }, [h, t.locale]), g.useEffect(() => {
    let B = !1;
    i.areValuesEqual(t, _.value, P) ? B = i.getTimezone(t, _.value) !== i.getTimezone(t, P) : B = !0, B && L((G) => b({}, G, {
      value: P,
      referenceValue: l.updateReferenceValue(t, P, G.referenceValue),
      sections: $(P)
    }));
  }, [P]), {
    state: _,
    selectedSectionIndexes: F,
    setSelectedSections: D,
    clearValue: q,
    clearActiveSection: A,
    updateSectionValue: te,
    updateValueFromValueStr: H,
    setTempAndroidValueStr: re,
    sectionsValueBoundaries: j,
    placeholder: V,
    timezone: T
  };
}, Dk = 5e3, Mr = (e) => e.saveQuery != null, $k = ({
  sections: e,
  updateSectionValue: t,
  sectionsValueBoundaries: n,
  setTempAndroidValueStr: o,
  timezone: a
}) => {
  const s = Ze(), [i, l] = g.useState(null), c = we(() => l(null));
  g.useEffect(() => {
    var m;
    i != null && ((m = e[i.sectionIndex]) == null ? void 0 : m.type) !== i.sectionType && c();
  }, [e, i, c]), g.useEffect(() => {
    if (i != null) {
      const m = setTimeout(() => c(), Dk);
      return () => {
        window.clearTimeout(m);
      };
    }
    return () => {
    };
  }, [i, c]);
  const u = ({
    keyPressed: m,
    sectionIndex: v
  }, h, y) => {
    const w = m.toLowerCase(), C = e[v];
    if (i != null && (!y || y(i.value)) && i.sectionIndex === v) {
      const O = `${i.value}${w}`, T = h(O, C);
      if (!Mr(T))
        return l({
          sectionIndex: v,
          value: O,
          sectionType: C.type
        }), T;
    }
    const E = h(w, C);
    return Mr(E) && !E.saveQuery ? (c(), null) : (l({
      sectionIndex: v,
      value: w,
      sectionType: C.type
    }), Mr(E) ? null : E);
  }, d = (m) => {
    const v = (w, C, E) => {
      const O = C.filter((T) => T.toLowerCase().startsWith(E));
      return O.length === 0 ? {
        saveQuery: !1
      } : {
        sectionValue: O[0],
        shouldGoToNextSection: O.length === 1
      };
    }, h = (w, C, E, O) => {
      const T = (P) => Rh(s, a, C.type, P);
      if (C.contentType === "letter")
        return v(C.format, T(C.format), w);
      if (E && O != null && Fc(s, E).contentType === "letter") {
        const P = T(E), S = v(E, P, w);
        return Mr(S) ? {
          saveQuery: !1
        } : b({}, S, {
          sectionValue: O(S.sectionValue, P)
        });
      }
      return {
        saveQuery: !1
      };
    };
    return u(m, (w, C) => {
      switch (C.type) {
        case "month": {
          const E = (O) => hp(s, O, s.formats.month, C.format);
          return h(w, C, s.formats.month, E);
        }
        case "weekDay": {
          const E = (O, T) => T.indexOf(O).toString();
          return h(w, C, s.formats.weekday, E);
        }
        case "meridiem":
          return h(w, C);
        default:
          return {
            saveQuery: !1
          };
      }
    });
  }, f = (m) => {
    const v = (y, w) => {
      const C = +`${y}`, E = n[w.type]({
        currentDate: null,
        format: w.format,
        contentType: w.contentType
      });
      if (C > E.maximum)
        return {
          saveQuery: !1
        };
      if (C < E.minimum)
        return {
          saveQuery: !0
        };
      const O = +`${y}0` > E.maximum || y.length === E.maximum.toString().length;
      return {
        sectionValue: $h(s, a, C, E, w),
        shouldGoToNextSection: O
      };
    };
    return u(m, (y, w) => {
      if (w.contentType === "digit" || w.contentType === "digit-with-letter")
        return v(y, w);
      if (w.type === "month") {
        const C = Mh(s, a, "digit", "month", "MM"), E = v(y, {
          type: w.type,
          format: "MM",
          hasLeadingZerosInFormat: C,
          hasLeadingZerosInInput: !0,
          contentType: "digit",
          maxLength: 2
        });
        if (Mr(E))
          return E;
        const O = hp(s, E.sectionValue, "MM", w.format);
        return b({}, E, {
          sectionValue: O
        });
      }
      if (w.type === "weekDay") {
        const C = v(y, w);
        if (Mr(C))
          return C;
        const E = ni(s, a, w.format)[Number(C.sectionValue) - 1];
        return b({}, C, {
          sectionValue: E
        });
      }
      return {
        saveQuery: !1
      };
    }, (y) => !Number.isNaN(Number(y)));
  };
  return {
    applyCharacterEditing: we((m) => {
      const v = e[m.sectionIndex], y = m.keyPressed !== " " && !Number.isNaN(Number(m.keyPressed)) ? f(m) : d(m);
      y == null ? o(null) : t({
        activeSection: v,
        newSectionValue: y.sectionValue,
        shouldGoToNextSection: y.shouldGoToNextSection
      });
    }),
    resetCharacterQuery: c
  };
};
function kk(e, t) {
  return Array.isArray(t) ? t.every((n) => e.indexOf(n) !== -1) : e.indexOf(t) !== -1;
}
const _k = (e, t) => (n) => {
  (n.key === "Enter" || n.key === " ") && (e(n), n.preventDefault(), n.stopPropagation());
}, fs = (e = document) => {
  const t = e.activeElement;
  return t ? t.shadowRoot ? fs(t.shadowRoot) : t : null;
}, Mk = "@media (pointer: fine)", Ik = ["onClick", "onKeyDown", "onFocus", "onBlur", "onMouseUp", "onPaste", "error", "clearable", "onClear", "disabled"], Nk = (e) => {
  const t = Ze(), {
    state: n,
    selectedSectionIndexes: o,
    setSelectedSections: a,
    clearValue: s,
    clearActiveSection: i,
    updateSectionValue: l,
    updateValueFromValueStr: c,
    setTempAndroidValueStr: u,
    sectionsValueBoundaries: d,
    placeholder: f,
    timezone: p
  } = Rk(e), {
    inputRef: m,
    internalProps: v,
    internalProps: {
      readOnly: h = !1,
      unstableFieldRef: y,
      minutesStep: w
    },
    forwardedProps: {
      onClick: C,
      onKeyDown: E,
      onFocus: O,
      onBlur: T,
      onMouseUp: P,
      onPaste: S,
      error: j,
      clearable: $,
      onClear: V,
      disabled: _
    },
    fieldValueManager: L,
    valueManager: M,
    validator: R
  } = e, D = ie(e.forwardedProps, Ik), {
    applyCharacterEditing: F,
    resetCharacterQuery: z
  } = $k({
    sections: n.sections,
    updateSectionValue: l,
    sectionsValueBoundaries: d,
    setTempAndroidValueStr: u,
    timezone: p
  }), N = g.useRef(null), q = Ke(m, N), A = g.useRef(void 0), te = Zt().direction === "rtl", re = g.useMemo(() => vk(n.sections, te), [n.sections, te]), B = () => {
    var oe;
    if (h) {
      a(null);
      return;
    }
    const ce = (oe = N.current.selectionStart) != null ? oe : 0;
    let I;
    ce <= n.sections[0].startInInput || ce >= n.sections[n.sections.length - 1].endInInput ? I = 1 : I = n.sections.findIndex((ne) => ne.startInInput - ne.startSeparator.length > ce);
    const Q = I === -1 ? n.sections.length - 1 : I - 1;
    a(Q);
  }, G = we((oe, ...ce) => {
    oe.isDefaultPrevented() || (C == null || C(oe, ...ce), B());
  }), ee = we((oe) => {
    P == null || P(oe), oe.preventDefault();
  }), W = we((...oe) => {
    O == null || O(...oe);
    const ce = N.current;
    window.clearTimeout(A.current), A.current = setTimeout(() => {
      !ce || ce !== N.current || o != null || h || (// avoid selecting all sections when focusing empty field without value
      ce.value.length && Number(ce.selectionEnd) - Number(ce.selectionStart) === ce.value.length ? a("all") : B());
    });
  }), J = we((...oe) => {
    T == null || T(...oe), a(null);
  }), se = we((oe) => {
    if (S == null || S(oe), h) {
      oe.preventDefault();
      return;
    }
    const ce = oe.clipboardData.getData("text");
    if (o && o.startIndex === o.endIndex) {
      const I = n.sections[o.startIndex], Q = /^[a-zA-Z]+$/.test(ce), ne = /^[0-9]+$/.test(ce), ue = /^(([a-zA-Z]+)|)([0-9]+)(([a-zA-Z]+)|)$/.test(ce);
      if (I.contentType === "letter" && Q || I.contentType === "digit" && ne || I.contentType === "digit-with-letter" && ue) {
        z(), l({
          activeSection: I,
          newSectionValue: ce,
          shouldGoToNextSection: !0
        }), oe.preventDefault();
        return;
      }
      if (Q || ne) {
        oe.preventDefault();
        return;
      }
    }
    oe.preventDefault(), z(), c(ce);
  }), le = we((oe) => {
    if (h)
      return;
    const ce = oe.target.value;
    if (ce === "") {
      z(), s();
      return;
    }
    const I = oe.nativeEvent.data, Q = I && I.length > 1, ne = Q ? I : ce, ue = Lr(ne);
    if (o == null || Q) {
      c(Q ? I : ue);
      return;
    }
    let ge;
    if (o.startIndex === 0 && o.endIndex === n.sections.length - 1 && ue.length === 1)
      ge = ue;
    else {
      const ye = Lr(L.getValueStrFromSections(n.sections, te));
      let xe = -1, be = -1;
      for (let Qe = 0; Qe < ye.length; Qe += 1)
        xe === -1 && ye[Qe] !== ue[Qe] && (xe = Qe), be === -1 && ye[ye.length - Qe - 1] !== ue[ue.length - Qe - 1] && (be = Qe);
      const _e = n.sections[o.startIndex];
      if (xe < _e.start || ye.length - be - 1 > _e.end)
        return;
      const rt = ue.length - ye.length + _e.end - Lr(_e.endSeparator || "").length;
      ge = ue.slice(_e.start + Lr(_e.startSeparator || "").length, rt);
    }
    if (ge.length === 0) {
      yk() ? u(ne) : (z(), i());
      return;
    }
    F({
      keyPressed: ge,
      sectionIndex: o.startIndex
    });
  }), X = we((oe) => {
    switch (E == null || E(oe), !0) {
      case (oe.key === "a" && (oe.ctrlKey || oe.metaKey)): {
        oe.preventDefault(), a("all");
        break;
      }
      case oe.key === "ArrowRight": {
        if (oe.preventDefault(), o == null)
          a(re.startIndex);
        else if (o.startIndex !== o.endIndex)
          a(o.endIndex);
        else {
          const ce = re.neighbors[o.startIndex].rightIndex;
          ce !== null && a(ce);
        }
        break;
      }
      case oe.key === "ArrowLeft": {
        if (oe.preventDefault(), o == null)
          a(re.endIndex);
        else if (o.startIndex !== o.endIndex)
          a(o.startIndex);
        else {
          const ce = re.neighbors[o.startIndex].leftIndex;
          ce !== null && a(ce);
        }
        break;
      }
      case oe.key === "Delete": {
        if (oe.preventDefault(), h)
          break;
        o == null || o.startIndex === 0 && o.endIndex === n.sections.length - 1 ? s() : i(), z();
        break;
      }
      case ["ArrowUp", "ArrowDown", "Home", "End", "PageUp", "PageDown"].includes(oe.key): {
        if (oe.preventDefault(), h || o == null)
          break;
        const ce = n.sections[o.startIndex], I = L.getActiveDateManager(t, n, ce), Q = dk(t, p, ce, oe.key, d, I.date, {
          minutesStep: w
        });
        l({
          activeSection: ce,
          newSectionValue: Q,
          shouldGoToNextSection: !1
        });
        break;
      }
    }
  });
  ft(() => {
    if (!N.current)
      return;
    if (o == null) {
      N.current.scrollLeft && (N.current.scrollLeft = 0);
      return;
    }
    const oe = n.sections[o.startIndex], ce = n.sections[o.endIndex];
    let I = oe.startInInput, Q = ce.endInInput;
    if (o.shouldSelectBoundarySelectors && (I -= oe.startSeparator.length, Q += ce.endSeparator.length), I !== N.current.selectionStart || Q !== N.current.selectionEnd) {
      const ne = N.current.scrollTop;
      N.current === fs(document) && N.current.setSelectionRange(I, Q), N.current.scrollTop = ne;
    }
  });
  const U = Fh(b({}, v, {
    value: n.value,
    timezone: p
  }), R, M.isSameError, M.defaultErrorState), K = g.useMemo(() => j !== void 0 ? j : M.hasError(U), [M, U, j]);
  g.useEffect(() => {
    !K && !o && z();
  }, [n.referenceValue, o, K]), g.useEffect(() => (N.current && N.current === document.activeElement && a("all"), () => window.clearTimeout(A.current)), []), g.useEffect(() => {
    n.tempValueStrAndroid != null && o != null && (z(), i());
  }, [n.tempValueStrAndroid]);
  const Y = g.useMemo(() => {
    var oe;
    return (oe = n.tempValueStrAndroid) != null ? oe : L.getValueStrFromSections(n.sections, te);
  }, [n.sections, L, n.tempValueStrAndroid, te]), he = g.useMemo(() => o == null || n.sections[o.startIndex].contentType === "letter" ? "text" : "numeric", [o, n.sections]), Oe = N.current && N.current === fs(document), Ne = M.areValuesEqual(t, n.value, M.emptyValue), fe = !Oe && Ne;
  g.useImperativeHandle(y, () => ({
    getSections: () => n.sections,
    getActiveSectionIndex: () => {
      var oe, ce, I;
      const Q = (oe = N.current.selectionStart) != null ? oe : 0, ne = (ce = N.current.selectionEnd) != null ? ce : 0, ue = !!((I = N.current) != null && I.readOnly);
      if (Q === 0 && ne === 0 || ue)
        return null;
      const ge = Q <= n.sections[0].startInInput ? 1 : n.sections.findIndex((ye) => ye.startInInput - ye.startSeparator.length > Q);
      return ge === -1 ? n.sections.length - 1 : ge - 1;
    },
    setSelectedSections: (oe) => a(oe)
  }));
  const ve = we((oe, ...ce) => {
    var I;
    oe.preventDefault(), V == null || V(oe, ...ce), s(), N == null || (I = N.current) == null || I.focus(), a(0);
  });
  return b({
    placeholder: f,
    autoComplete: "off",
    disabled: !!_
  }, D, {
    value: fe ? "" : Y,
    inputMode: he,
    readOnly: h,
    onClick: G,
    onFocus: W,
    onBlur: J,
    onPaste: se,
    onChange: le,
    onKeyDown: X,
    onMouseUp: ee,
    onClear: ve,
    error: K,
    ref: q,
    clearable: !!($ && !Ne && !h && !_)
  });
}, ri = ({
  props: e,
  value: t,
  adapter: n
}) => {
  if (t === null)
    return null;
  const {
    shouldDisableDate: o,
    shouldDisableMonth: a,
    shouldDisableYear: s,
    disablePast: i,
    disableFuture: l,
    timezone: c
  } = e, u = n.utils.dateWithTimezone(void 0, c), d = Gt(n.utils, e.minDate, n.defaultDates.minDate), f = Gt(n.utils, e.maxDate, n.defaultDates.maxDate);
  switch (!0) {
    case !n.utils.isValid(t):
      return "invalidDate";
    case !!(o && o(t)):
      return "shouldDisableDate";
    case !!(a && a(t)):
      return "shouldDisableMonth";
    case !!(s && s(t)):
      return "shouldDisableYear";
    case !!(l && n.utils.isAfterDay(t, u)):
      return "disableFuture";
    case !!(i && n.utils.isBeforeDay(t, u)):
      return "disablePast";
    case !!(d && n.utils.isBeforeDay(t, d)):
      return "minDate";
    case !!(f && n.utils.isAfterDay(t, f)):
      return "maxDate";
    default:
      return null;
  }
}, Vh = ["disablePast", "disableFuture", "minDate", "maxDate", "shouldDisableDate", "shouldDisableMonth", "shouldDisableYear"], jk = ["disablePast", "disableFuture", "minTime", "maxTime", "shouldDisableClock", "shouldDisableTime", "minutesStep", "ampm", "disableIgnoringDatePartForTimeValidation"], Ak = ["minDateTime", "maxDateTime"], Fk = [...Vh, ...jk, ...Ak], Lh = (e) => Fk.reduce((t, n) => (e.hasOwnProperty(n) && (t[n] = e[n]), t), {}), Vk = ["value", "defaultValue", "referenceDate", "format", "formatDensity", "onChange", "timezone", "readOnly", "onError", "shouldRespectLeadingZeros", "selectedSections", "onSelectedSectionsChange", "unstableFieldRef"], Lk = (e, t) => {
  const n = b({}, e), o = {}, a = (s) => {
    n.hasOwnProperty(s) && (o[s] = n[s], delete n[s]);
  };
  return Vk.forEach(a), Vh.forEach(a), {
    forwardedProps: n,
    internalProps: o
  };
}, Bk = (e) => {
  var t, n, o;
  const a = Ze(), s = Ta();
  return b({}, e, {
    disablePast: (t = e.disablePast) != null ? t : !1,
    disableFuture: (n = e.disableFuture) != null ? n : !1,
    format: (o = e.format) != null ? o : a.formats.keyboardDate,
    minDate: Gt(a, e.minDate, s.minDate),
    maxDate: Gt(a, e.maxDate, s.maxDate)
  });
}, zk = ({
  props: e,
  inputRef: t
}) => {
  const n = Bk(e), {
    forwardedProps: o,
    internalProps: a
  } = Lk(n);
  return Nk({
    inputRef: t,
    forwardedProps: o,
    internalProps: a,
    valueManager: Cn,
    fieldValueManager: Tk,
    validator: ri,
    valueType: "date"
  });
}, Wk = Z(ec)({
  [`& .${Ao.container}`]: {
    outline: 0
  },
  [`& .${Ao.paper}`]: {
    outline: 0,
    minWidth: ti
  }
}), Uk = Z(zs)({
  "&:first-of-type": {
    padding: 0
  }
});
function Hk(e) {
  var t, n;
  const {
    children: o,
    onDismiss: a,
    open: s,
    slots: i,
    slotProps: l
  } = e, c = (t = i == null ? void 0 : i.dialog) != null ? t : Wk, u = (n = i == null ? void 0 : i.mobileTransition) != null ? n : mr;
  return /* @__PURE__ */ x.jsx(c, b({
    open: s,
    onClose: a
  }, l == null ? void 0 : l.dialog, {
    TransitionComponent: u,
    TransitionProps: l == null ? void 0 : l.mobileTransition,
    PaperComponent: i == null ? void 0 : i.mobilePaper,
    PaperProps: l == null ? void 0 : l.mobilePaper,
    children: /* @__PURE__ */ x.jsx(Uk, {
      children: o
    })
  }));
}
function qk(e) {
  return Pe("MuiPickersPopper", e);
}
Ce("MuiPickersPopper", ["root", "paper"]);
const Yk = "@media (prefers-reduced-motion: reduce)", qr = typeof navigator < "u" && navigator.userAgent.match(/android\s(\d+)|OS\s(\d+)/i), Tp = qr && qr[1] ? parseInt(qr[1], 10) : null, wp = qr && qr[2] ? parseInt(qr[2], 10) : null, Kk = Tp && Tp < 10 || wp && wp < 13 || !1, Bh = () => Df(Yk, {
  defaultMatches: !1
}) || Kk, Gk = ["PaperComponent", "popperPlacement", "ownerState", "children", "paperSlotProps", "paperClasses", "onPaperClick", "onPaperTouchStart"], Xk = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"],
    paper: ["paper"]
  }, qk, t);
}, Zk = Z(js, {
  name: "MuiPickersPopper",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})(({
  theme: e
}) => ({
  zIndex: e.zIndex.modal
})), Jk = Z(pa, {
  name: "MuiPickersPopper",
  slot: "Paper",
  overridesResolver: (e, t) => t.paper
})(({
  ownerState: e
}) => b({
  outline: 0,
  transformOrigin: "top center"
}, e.placement.includes("top") && {
  transformOrigin: "bottom center"
}));
function Qk(e, t) {
  return t.documentElement.clientWidth < e.clientX || t.documentElement.clientHeight < e.clientY;
}
function e_(e, t) {
  const n = g.useRef(!1), o = g.useRef(!1), a = g.useRef(null), s = g.useRef(!1);
  g.useEffect(() => {
    if (!e)
      return;
    function c() {
      s.current = !0;
    }
    return document.addEventListener("mousedown", c, !0), document.addEventListener("touchstart", c, !0), () => {
      document.removeEventListener("mousedown", c, !0), document.removeEventListener("touchstart", c, !0), s.current = !1;
    };
  }, [e]);
  const i = we((c) => {
    if (!s.current)
      return;
    const u = o.current;
    o.current = !1;
    const d = dt(a.current);
    if (!a.current || // is a TouchEvent?
    "clientX" in c && Qk(c, d))
      return;
    if (n.current) {
      n.current = !1;
      return;
    }
    let f;
    c.composedPath ? f = c.composedPath().indexOf(a.current) > -1 : f = !d.documentElement.contains(c.target) || a.current.contains(c.target), !f && !u && t(c);
  }), l = () => {
    o.current = !0;
  };
  return g.useEffect(() => {
    if (e) {
      const c = dt(a.current), u = () => {
        n.current = !0;
      };
      return c.addEventListener("touchstart", i), c.addEventListener("touchmove", u), () => {
        c.removeEventListener("touchstart", i), c.removeEventListener("touchmove", u);
      };
    }
  }, [e, i]), g.useEffect(() => {
    if (e) {
      const c = dt(a.current);
      return c.addEventListener("click", i), () => {
        c.removeEventListener("click", i), o.current = !1;
      };
    }
  }, [e, i]), [a, l, l];
}
const t_ = /* @__PURE__ */ g.forwardRef((e, t) => {
  const {
    PaperComponent: n,
    popperPlacement: o,
    ownerState: a,
    children: s,
    paperSlotProps: i,
    paperClasses: l,
    onPaperClick: c,
    onPaperTouchStart: u
    // picks up the style props provided by `Transition`
    // https://mui.com/material-ui/transitions/#child-requirement
  } = e, d = ie(e, Gk), f = b({}, a, {
    placement: o
  }), p = Ye({
    elementType: n,
    externalSlotProps: i,
    additionalProps: {
      tabIndex: -1,
      elevation: 8,
      ref: t
    },
    className: l,
    ownerState: f
  });
  return /* @__PURE__ */ x.jsx(n, b({}, d, p, {
    onClick: (m) => {
      var v;
      c(m), (v = p.onClick) == null || v.call(p, m);
    },
    onTouchStart: (m) => {
      var v;
      u(m), (v = p.onTouchStart) == null || v.call(p, m);
    },
    ownerState: f,
    children: s
  }));
});
function n_(e) {
  var t, n, o, a;
  const s = Ee({
    props: e,
    name: "MuiPickersPopper"
  }), {
    anchorEl: i,
    children: l,
    containerRef: c = null,
    shouldRestoreFocus: u,
    onBlur: d,
    onDismiss: f,
    open: p,
    role: m,
    placement: v,
    slots: h,
    slotProps: y,
    reduceAnimations: w
  } = s;
  g.useEffect(() => {
    function q(A) {
      p && (A.key === "Escape" || A.key === "Esc") && f();
    }
    return document.addEventListener("keydown", q), () => {
      document.removeEventListener("keydown", q);
    };
  }, [f, p]);
  const C = g.useRef(null);
  g.useEffect(() => {
    m === "tooltip" || u && !u() || (p ? C.current = fs(document) : C.current && C.current instanceof HTMLElement && setTimeout(() => {
      C.current instanceof HTMLElement && C.current.focus();
    }));
  }, [p, m, u]);
  const [E, O, T] = e_(p, d ?? f), P = g.useRef(null), S = Ke(P, c), j = Ke(S, E), $ = s, V = Xk($), _ = Bh(), L = w ?? _, M = (q) => {
    q.key === "Escape" && (q.stopPropagation(), f());
  }, R = ((t = h == null ? void 0 : h.desktopTransition) != null ? t : L) ? mr : ro, D = (n = h == null ? void 0 : h.desktopTrapFocus) != null ? n : Ko, F = (o = h == null ? void 0 : h.desktopPaper) != null ? o : Jk, z = (a = h == null ? void 0 : h.popper) != null ? a : Zk, N = Ye({
    elementType: z,
    externalSlotProps: y == null ? void 0 : y.popper,
    additionalProps: {
      transition: !0,
      role: m,
      open: p,
      anchorEl: i,
      placement: v,
      onKeyDown: M
    },
    className: V.root,
    ownerState: s
  });
  return /* @__PURE__ */ x.jsx(z, b({}, N, {
    children: ({
      TransitionProps: q,
      placement: A
    }) => /* @__PURE__ */ x.jsx(D, b({
      open: p,
      disableAutoFocus: !0,
      disableRestoreFocus: !0,
      disableEnforceFocus: m === "tooltip",
      isEnabled: () => !0
    }, y == null ? void 0 : y.desktopTrapFocus, {
      children: /* @__PURE__ */ x.jsx(R, b({}, q, y == null ? void 0 : y.desktopTransition, {
        children: /* @__PURE__ */ x.jsx(t_, {
          PaperComponent: F,
          ownerState: $,
          popperPlacement: A,
          ref: j,
          onPaperClick: O,
          onPaperTouchStart: T,
          paperClasses: V.paper,
          paperSlotProps: y == null ? void 0 : y.desktopPaper,
          children: l
        })
      }))
    }))
  }));
}
function r_(e) {
  return Pe("MuiPickersToolbar", e);
}
Ce("MuiPickersToolbar", ["root", "content"]);
const o_ = (e) => {
  const {
    classes: t,
    isLandscape: n
  } = e;
  return Se({
    root: ["root"],
    content: ["content"],
    penIconButton: ["penIconButton", n && "penIconButtonLandscape"]
  }, r_, t);
}, a_ = Z("div", {
  name: "MuiPickersToolbar",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})(({
  theme: e,
  ownerState: t
}) => b({
  display: "flex",
  flexDirection: "column",
  alignItems: "flex-start",
  justifyContent: "space-between",
  padding: e.spacing(2, 3)
}, t.isLandscape && {
  height: "auto",
  maxWidth: 160,
  padding: 16,
  justifyContent: "flex-start",
  flexWrap: "wrap"
})), s_ = Z("div", {
  name: "MuiPickersToolbar",
  slot: "Content",
  overridesResolver: (e, t) => t.content
})(({
  ownerState: e
}) => {
  var t;
  return {
    display: "flex",
    flexWrap: "wrap",
    width: "100%",
    justifyContent: e.isLandscape ? "flex-start" : "space-between",
    flexDirection: e.isLandscape ? (t = e.landscapeDirection) != null ? t : "column" : "row",
    flex: 1,
    alignItems: e.isLandscape ? "flex-start" : "center"
  };
}), i_ = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiPickersToolbar"
  }), {
    children: a,
    className: s,
    toolbarTitle: i,
    hidden: l,
    titleId: c
  } = o, u = o, d = o_(u);
  return l ? null : /* @__PURE__ */ x.jsxs(a_, {
    ref: n,
    className: pe(d.root, s),
    ownerState: u,
    children: [/* @__PURE__ */ x.jsx(Rt, {
      color: "text.secondary",
      variant: "overline",
      id: c,
      children: i
    }), /* @__PURE__ */ x.jsx(s_, {
      className: d.content,
      ownerState: u,
      children: a
    })]
  });
}), l_ = ({
  open: e,
  onOpen: t,
  onClose: n
}) => {
  const o = g.useRef(typeof e == "boolean").current, [a, s] = g.useState(!1);
  g.useEffect(() => {
    if (o) {
      if (typeof e != "boolean")
        throw new Error("You must not mix controlling and uncontrolled mode for `open` prop");
      s(e);
    }
  }, [o, e]);
  const i = g.useCallback((l) => {
    o || s(l), l && t && t(), !l && n && n();
  }, [o, t, n]);
  return {
    isOpen: a,
    setIsOpen: i
  };
}, c_ = (e) => {
  const {
    action: t,
    hasChanged: n,
    dateState: o,
    isControlled: a
  } = e, s = !a && !o.hasBeenModifiedSinceMount;
  return t.name === "setValueFromField" ? !0 : t.name === "setValueFromAction" ? s && ["accept", "today", "clear"].includes(t.pickerAction) ? !0 : n(o.lastPublishedValue) : t.name === "setValueFromView" && t.selectionState !== "shallow" || t.name === "setValueFromShortcut" ? s ? !0 : n(o.lastPublishedValue) : !1;
}, u_ = (e) => {
  const {
    action: t,
    hasChanged: n,
    dateState: o,
    isControlled: a,
    closeOnSelect: s
  } = e, i = !a && !o.hasBeenModifiedSinceMount;
  return t.name === "setValueFromAction" ? i && ["accept", "today", "clear"].includes(t.pickerAction) ? !0 : n(o.lastCommittedValue) : t.name === "setValueFromView" && t.selectionState === "finish" && s ? i ? !0 : n(o.lastCommittedValue) : t.name === "setValueFromShortcut" ? t.changeImportance === "accept" && n(o.lastCommittedValue) : !1;
}, d_ = (e) => {
  const {
    action: t,
    closeOnSelect: n
  } = e;
  return t.name === "setValueFromAction" ? !0 : t.name === "setValueFromView" ? t.selectionState === "finish" && n : t.name === "setValueFromShortcut" ? t.changeImportance === "accept" : !1;
}, p_ = ({
  props: e,
  valueManager: t,
  valueType: n,
  wrapperVariant: o,
  validator: a
}) => {
  const {
    onAccept: s,
    onChange: i,
    value: l,
    defaultValue: c,
    closeOnSelect: u = o === "desktop",
    selectedSections: d,
    onSelectedSectionsChange: f,
    timezone: p
  } = e, {
    current: m
  } = g.useRef(c), {
    current: v
  } = g.useRef(l !== void 0);
  process.env.NODE_ENV !== "production" && (g.useEffect(() => {
    v !== (l !== void 0) && console.error([`MUI: A component is changing the ${v ? "" : "un"}controlled value of a picker to be ${v ? "un" : ""}controlled.`, "Elements should not switch from uncontrolled to controlled (or vice versa).", "Decide between using a controlled or uncontrolled valuefor the lifetime of the component.", "The nature of the state is determined during the first render. It's considered controlled if the value is not `undefined`.", "More info: https://fb.me/react-controlled-components"].join(`
`));
  }, [l]), g.useEffect(() => {
    !v && m !== c && console.error(["MUI: A component is changing the defaultValue of an uncontrolled picker after being initialized. To suppress this warning opt to use a controlled value."].join(`
`));
  }, [JSON.stringify(m)]));
  const h = Ze(), y = wr(), [w, C] = Ht({
    controlled: d,
    default: null,
    name: "usePickerValue",
    state: "selectedSections"
  }), {
    isOpen: E,
    setIsOpen: O
  } = l_(e), [T, P] = g.useState(() => {
    let W;
    return l !== void 0 ? W = l : m !== void 0 ? W = m : W = t.emptyValue, {
      draft: W,
      lastPublishedValue: W,
      lastCommittedValue: W,
      lastControlledValue: l,
      hasBeenModifiedSinceMount: !1
    };
  }), {
    timezone: S,
    handleValueChange: j
  } = jc({
    timezone: p,
    value: l,
    defaultValue: m,
    onChange: i,
    valueManager: t
  });
  Fh(b({}, e, {
    value: T.draft,
    timezone: S
  }), a, t.isSameError, t.defaultErrorState);
  const $ = we((W) => {
    const J = {
      action: W,
      dateState: T,
      hasChanged: (U) => !t.areValuesEqual(h, W.value, U),
      isControlled: v,
      closeOnSelect: u
    }, se = c_(J), le = u_(J), X = d_(J);
    if (P((U) => b({}, U, {
      draft: W.value,
      lastPublishedValue: se ? W.value : U.lastPublishedValue,
      lastCommittedValue: le ? W.value : U.lastCommittedValue,
      hasBeenModifiedSinceMount: !0
    })), se) {
      const K = {
        validationError: W.name === "setValueFromField" ? W.context.validationError : a({
          adapter: y,
          value: W.value,
          props: b({}, e, {
            value: W.value,
            timezone: S
          })
        })
      };
      W.name === "setValueFromShortcut" && W.shortcut != null && (K.shortcut = W.shortcut), j(W.value, K);
    }
    le && s && s(W.value), X && O(!1);
  });
  if (l !== void 0 && (T.lastControlledValue === void 0 || !t.areValuesEqual(h, T.lastControlledValue, l))) {
    const W = t.areValuesEqual(h, T.draft, l);
    P((J) => b({}, J, {
      lastControlledValue: l
    }, W ? {} : {
      lastCommittedValue: l,
      lastPublishedValue: l,
      draft: l,
      hasBeenModifiedSinceMount: !0
    }));
  }
  const V = we(() => {
    $({
      value: t.emptyValue,
      name: "setValueFromAction",
      pickerAction: "clear"
    });
  }), _ = we(() => {
    $({
      value: T.lastPublishedValue,
      name: "setValueFromAction",
      pickerAction: "accept"
    });
  }), L = we(() => {
    $({
      value: T.lastPublishedValue,
      name: "setValueFromAction",
      pickerAction: "dismiss"
    });
  }), M = we(() => {
    $({
      value: T.lastCommittedValue,
      name: "setValueFromAction",
      pickerAction: "cancel"
    });
  }), R = we(() => {
    $({
      value: t.getTodayValue(h, S, n),
      name: "setValueFromAction",
      pickerAction: "today"
    });
  }), D = we(() => O(!0)), F = we(() => O(!1)), z = we((W, J = "partial") => $({
    name: "setValueFromView",
    value: W,
    selectionState: J
  })), N = we((W, J, se) => $({
    name: "setValueFromShortcut",
    value: W,
    changeImportance: J ?? "accept",
    shortcut: se
  })), q = we((W, J) => $({
    name: "setValueFromField",
    value: W,
    context: J
  })), A = we((W) => {
    C(W), f == null || f(W);
  }), H = {
    onClear: V,
    onAccept: _,
    onDismiss: L,
    onCancel: M,
    onSetToday: R,
    onOpen: D,
    onClose: F
  }, te = {
    value: T.draft,
    onChange: q,
    selectedSections: w,
    onSelectedSectionsChange: A
  }, re = g.useMemo(() => t.cleanValue(h, T.draft), [h, t, T.draft]), B = {
    value: re,
    onChange: z,
    onClose: F,
    open: E,
    onSelectedSectionsChange: A
  }, ee = b({}, H, {
    value: re,
    onChange: z,
    onSelectShortcut: N,
    isValid: (W) => {
      const J = a({
        adapter: y,
        value: W,
        props: b({}, e, {
          value: W,
          timezone: S
        })
      });
      return !t.hasError(J);
    }
  });
  return {
    open: E,
    fieldProps: te,
    viewProps: B,
    layoutProps: ee,
    actions: H
  };
}, f_ = ["className", "sx"], m_ = ({
  props: e,
  propsFromPickerValue: t,
  additionalViewProps: n,
  inputRef: o,
  autoFocusView: a
}) => {
  const {
    onChange: s,
    open: i,
    onSelectedSectionsChange: l,
    onClose: c
  } = t, {
    view: u,
    views: d,
    openTo: f,
    onViewChange: p,
    viewRenderers: m,
    timezone: v
  } = e, h = ie(e, f_), {
    view: y,
    setView: w,
    defaultView: C,
    focusedView: E,
    setFocusedView: O,
    setValueAndGoToNextView: T
  } = Sh({
    view: u,
    views: d,
    openTo: f,
    onChange: s,
    onViewChange: p,
    autoFocus: a
  }), {
    hasUIView: P,
    viewModeLookup: S
  } = g.useMemo(() => d.reduce((R, D) => {
    let F;
    return m[D] != null ? F = "UI" : F = "field", R.viewModeLookup[D] = F, F === "UI" && (R.hasUIView = !0), R;
  }, {
    hasUIView: !1,
    viewModeLookup: {}
  }), [m, d]), j = g.useMemo(() => d.reduce((R, D) => m[D] != null && J$(D) ? R + 1 : R, 0), [m, d]), $ = S[y], V = we(() => $ === "UI"), [_, L] = g.useState($ === "UI" ? y : null);
  return _ !== y && S[y] === "UI" && L(y), ft(() => {
    $ === "field" && i && (c(), setTimeout(() => {
      o == null || o.current.focus(), l(y);
    }));
  }, [y]), ft(() => {
    if (!i)
      return;
    let R = y;
    $ === "field" && _ != null && (R = _), R !== C && S[R] === "UI" && S[C] === "UI" && (R = C), R !== y && w(R), O(R, !0);
  }, [i]), {
    hasUIView: P,
    shouldRestoreFocus: V,
    layoutProps: {
      views: d,
      view: _,
      onViewChange: w
    },
    renderCurrentView: () => {
      if (_ == null)
        return null;
      const R = m[_];
      return R == null ? null : R(b({}, h, n, t, {
        views: d,
        timezone: v,
        onChange: T,
        view: _,
        onViewChange: w,
        focusedView: E,
        onFocusedViewChange: O,
        showViewSwitcher: j > 1,
        timeViewsCount: j
      }));
    }
  };
};
function Ep() {
  return typeof window > "u" ? "portrait" : window.screen && window.screen.orientation && window.screen.orientation.angle ? Math.abs(window.screen.orientation.angle) === 90 ? "landscape" : "portrait" : window.orientation && Math.abs(Number(window.orientation)) === 90 ? "landscape" : "portrait";
}
const h_ = (e, t) => {
  const [n, o] = g.useState(Ep);
  return ft(() => {
    const s = () => {
      o(Ep());
    };
    return window.addEventListener("orientationchange", s), () => {
      window.removeEventListener("orientationchange", s);
    };
  }, []), kk(e, ["hours", "minutes", "seconds"]) ? !1 : (t || n) === "landscape";
}, b_ = ({
  props: e,
  propsFromPickerValue: t,
  propsFromPickerViews: n,
  wrapperVariant: o
}) => {
  const {
    orientation: a
  } = e, s = h_(n.views, a);
  return {
    layoutProps: b({}, n, t, {
      isLandscape: s,
      wrapperVariant: o,
      disabled: e.disabled,
      readOnly: e.readOnly
    })
  };
}, g_ = (e, t = "warning") => {
  let n = !1;
  const o = Array.isArray(e) ? e.join(`
`) : e;
  return () => {
    n || (n = !0, t === "error" ? console.error(o) : console.warn(o));
  };
}, y_ = g_(["The `renderInput` prop has been removed in version 6.0 of the Date and Time Pickers.", "You can replace it with the `textField` component slot in most cases.", "For more information, please have a look at the migration guide (https://mui.com/x/migration/migration-pickers-v5/#input-renderer-required-in-v5)."]), zh = ({
  props: e,
  valueManager: t,
  valueType: n,
  wrapperVariant: o,
  inputRef: a,
  additionalViewProps: s,
  validator: i,
  autoFocusView: l
}) => {
  process.env.NODE_ENV !== "production" && e.renderInput != null && y_();
  const c = p_({
    props: e,
    valueManager: t,
    valueType: n,
    wrapperVariant: o,
    validator: i
  }), u = m_({
    props: e,
    inputRef: a,
    additionalViewProps: s,
    autoFocusView: l,
    propsFromPickerValue: c.viewProps
  }), d = b_({
    props: e,
    wrapperVariant: o,
    propsFromPickerValue: c.layoutProps,
    propsFromPickerViews: u.layoutProps
  });
  return {
    // Picker value
    open: c.open,
    actions: c.actions,
    fieldProps: c.fieldProps,
    // Picker views
    renderCurrentView: u.renderCurrentView,
    hasUIView: u.hasUIView,
    shouldRestoreFocus: u.shouldRestoreFocus,
    // Picker layout
    layoutProps: d.layoutProps
  };
};
function Wh(e) {
  return Pe("MuiPickersLayout", e);
}
const ki = Ce("MuiPickersLayout", ["root", "landscape", "contentWrapper", "toolbar", "actionBar", "tabs", "shortcuts"]), v_ = ["onAccept", "onClear", "onCancel", "onSetToday", "actions"];
function Uh(e) {
  const {
    onAccept: t,
    onClear: n,
    onCancel: o,
    onSetToday: a,
    actions: s
  } = e, i = ie(e, v_), l = Hn();
  if (s == null || s.length === 0)
    return null;
  const c = s == null ? void 0 : s.map((u) => {
    switch (u) {
      case "clear":
        return /* @__PURE__ */ x.jsx(Ar, {
          onClick: n,
          children: l.clearButtonLabel
        }, u);
      case "cancel":
        return /* @__PURE__ */ x.jsx(Ar, {
          onClick: o,
          children: l.cancelButtonLabel
        }, u);
      case "accept":
        return /* @__PURE__ */ x.jsx(Ar, {
          onClick: t,
          children: l.okButtonLabel
        }, u);
      case "today":
        return /* @__PURE__ */ x.jsx(Ar, {
          onClick: a,
          children: l.todayButtonLabel
        }, u);
      default:
        return null;
    }
  });
  return /* @__PURE__ */ x.jsx(tc, b({}, i, {
    children: c
  }));
}
process.env.NODE_ENV !== "production" && (Uh.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * Ordered array of actions to display.
   * If empty, does not display that action bar.
   * @default `['cancel', 'accept']` for mobile and `[]` for desktop
   */
  actions: r.arrayOf(r.oneOf(["accept", "cancel", "clear", "today"]).isRequired),
  /**
   * If `true`, the actions do not have additional margin.
   * @default false
   */
  disableSpacing: r.bool,
  onAccept: r.func.isRequired,
  onCancel: r.func.isRequired,
  onClear: r.func.isRequired,
  onSetToday: r.func.isRequired,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
const x_ = ["items", "changeImportance", "isLandscape", "onChange", "isValid"], T_ = ["getValue"];
function Hh(e) {
  const {
    items: t,
    changeImportance: n,
    onChange: o,
    isValid: a
  } = e, s = ie(e, x_);
  if (t == null || t.length === 0)
    return null;
  const i = t.map((l) => {
    let {
      getValue: c
    } = l, u = ie(l, T_);
    const d = c({
      isValid: a
    });
    return {
      label: u.label,
      onClick: () => {
        o(d, n, u);
      },
      disabled: !a(d)
    };
  });
  return /* @__PURE__ */ x.jsx(ac, b({
    dense: !0,
    sx: [{
      maxHeight: Mc,
      maxWidth: 200,
      overflow: "auto"
    }, ...Array.isArray(s.sx) ? s.sx : [s.sx]]
  }, s, {
    children: i.map((l) => /* @__PURE__ */ x.jsx(um, {
      children: /* @__PURE__ */ x.jsx(em, b({}, l))
    }, l.label))
  }));
}
process.env.NODE_ENV !== "production" && (Hh.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * Importance of the change when picking a shortcut:
   * - "accept": fires `onChange`, fires `onAccept` and closes the picker.
   * - "set": fires `onChange` but do not fire `onAccept` and does not close the picker.
   * @default "accept"
   */
  changeImportance: r.oneOf(["accept", "set"]),
  className: r.string,
  component: r.elementType,
  /**
   * If `true`, compact vertical padding designed for keyboard and mouse input is used for
   * the list and list items.
   * The prop is available to descendant components as the `dense` context.
   * @default false
   */
  dense: r.bool,
  /**
   * If `true`, vertical padding is removed from the list.
   * @default false
   */
  disablePadding: r.bool,
  isLandscape: r.bool.isRequired,
  isValid: r.func.isRequired,
  /**
   * Ordered array of shortcuts to display.
   * If empty, does not display the shortcuts.
   * @default `[]`
   */
  items: r.arrayOf(r.shape({
    getValue: r.func.isRequired,
    label: r.string.isRequired
  })),
  onChange: r.func.isRequired,
  style: r.object,
  /**
   * The content of the subheader, normally `ListSubheader`.
   */
  subheader: r.node,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
});
function w_(e) {
  return e.view !== null;
}
const E_ = (e) => {
  const {
    classes: t,
    isLandscape: n
  } = e;
  return Se({
    root: ["root", n && "landscape"],
    contentWrapper: ["contentWrapper"],
    toolbar: ["toolbar"],
    actionBar: ["actionBar"],
    tabs: ["tabs"],
    landscape: ["landscape"],
    shortcuts: ["shortcuts"]
  }, Wh, t);
}, C_ = (e) => {
  var t, n;
  const {
    wrapperVariant: o,
    onAccept: a,
    onClear: s,
    onCancel: i,
    onSetToday: l,
    view: c,
    views: u,
    onViewChange: d,
    value: f,
    onChange: p,
    onSelectShortcut: m,
    isValid: v,
    isLandscape: h,
    disabled: y,
    readOnly: w,
    children: C,
    components: E,
    componentsProps: O,
    slots: T,
    slotProps: P
    // TODO: Remove this "as" hack. It get introduced to mark `value` prop in PickersLayoutProps as not required.
    // The true type should be
    // - For pickers value: TDate | null
    // - For range pickers value: [TDate | null, TDate | null]
  } = e, S = T ?? Ih(E), j = P ?? O, $ = E_(e), V = (t = S == null ? void 0 : S.actionBar) != null ? t : Uh, _ = Ye({
    elementType: V,
    externalSlotProps: j == null ? void 0 : j.actionBar,
    additionalProps: {
      onAccept: a,
      onClear: s,
      onCancel: i,
      onSetToday: l,
      actions: o === "desktop" ? [] : ["cancel", "accept"],
      className: $.actionBar
    },
    ownerState: b({}, e, {
      wrapperVariant: o
    })
  }), L = /* @__PURE__ */ x.jsx(V, b({}, _)), M = S == null ? void 0 : S.toolbar, R = Ye({
    elementType: M,
    externalSlotProps: j == null ? void 0 : j.toolbar,
    additionalProps: {
      isLandscape: h,
      onChange: p,
      value: f,
      view: c,
      onViewChange: d,
      views: u,
      disabled: y,
      readOnly: w,
      className: $.toolbar
    },
    ownerState: b({}, e, {
      wrapperVariant: o
    })
  }), D = w_(R) && M ? /* @__PURE__ */ x.jsx(M, b({}, R)) : null, F = C, z = S == null ? void 0 : S.tabs, N = c && z ? /* @__PURE__ */ x.jsx(z, b({
    view: c,
    onViewChange: d,
    className: $.tabs
  }, j == null ? void 0 : j.tabs)) : null, q = (n = S == null ? void 0 : S.shortcuts) != null ? n : Hh, A = Ye({
    elementType: q,
    externalSlotProps: j == null ? void 0 : j.shortcuts,
    additionalProps: {
      isValid: v,
      isLandscape: h,
      onChange: m,
      className: $.shortcuts
    },
    ownerState: {
      isValid: v,
      isLandscape: h,
      onChange: m,
      className: $.shortcuts,
      wrapperVariant: o
    }
  }), H = c && q ? /* @__PURE__ */ x.jsx(q, b({}, A)) : null;
  return {
    toolbar: D,
    content: F,
    tabs: N,
    actionBar: L,
    shortcuts: H
  };
}, O_ = (e) => {
  const {
    isLandscape: t,
    classes: n
  } = e;
  return Se({
    root: ["root", t && "landscape"],
    contentWrapper: ["contentWrapper"]
  }, Wh, n);
}, qh = Z("div", {
  name: "MuiPickersLayout",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})(({
  theme: e,
  ownerState: t
}) => ({
  display: "grid",
  gridAutoColumns: "max-content auto max-content",
  gridAutoRows: "max-content auto max-content",
  [`& .${ki.toolbar}`]: t.isLandscape ? {
    gridColumn: e.direction === "rtl" ? 3 : 1,
    gridRow: "2 / 3"
  } : {
    gridColumn: "2 / 4",
    gridRow: 1
  },
  [`.${ki.shortcuts}`]: t.isLandscape ? {
    gridColumn: "2 / 4",
    gridRow: 1
  } : {
    gridColumn: e.direction === "rtl" ? 3 : 1,
    gridRow: "2 / 3"
  },
  [`& .${ki.actionBar}`]: {
    gridColumn: "1 / 4",
    gridRow: 3
  }
}));
qh.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  as: r.elementType,
  ownerState: r.shape({
    isLandscape: r.bool.isRequired
  }).isRequired,
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object])
};
const S_ = Z("div", {
  name: "MuiPickersLayout",
  slot: "ContentWrapper",
  overridesResolver: (e, t) => t.contentWrapper
})({
  gridColumn: 2,
  gridRow: 2,
  display: "flex",
  flexDirection: "column"
}), Lc = function(t) {
  const n = Ee({
    props: t,
    name: "MuiPickersLayout"
  }), {
    toolbar: o,
    content: a,
    tabs: s,
    actionBar: i,
    shortcuts: l
  } = C_(n), {
    sx: c,
    className: u,
    isLandscape: d,
    ref: f,
    wrapperVariant: p
  } = n, m = n, v = O_(m);
  return /* @__PURE__ */ x.jsxs(qh, {
    ref: f,
    sx: c,
    className: pe(u, v.root),
    ownerState: m,
    children: [d ? l : o, d ? o : l, /* @__PURE__ */ x.jsx(S_, {
      className: v.contentWrapper,
      children: p === "desktop" ? /* @__PURE__ */ x.jsxs(g.Fragment, {
        children: [a, s]
      }) : /* @__PURE__ */ x.jsxs(g.Fragment, {
        children: [s, a]
      })
    }), i]
  });
};
process.env.NODE_ENV !== "production" && (Lc.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  children: r.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  className: r.string,
  /**
   * Overridable components.
   * @default {}
   * @deprecated Please use `slots`.
   */
  components: r.object,
  /**
   * The props used for each component slot.
   * @default {}
   * @deprecated Please use `slotProps`.
   */
  componentsProps: r.object,
  disabled: r.bool,
  isLandscape: r.bool.isRequired,
  isValid: r.func.isRequired,
  onAccept: r.func.isRequired,
  onCancel: r.func.isRequired,
  onChange: r.func.isRequired,
  onClear: r.func.isRequired,
  onClose: r.func.isRequired,
  onDismiss: r.func.isRequired,
  onOpen: r.func.isRequired,
  onSelectShortcut: r.func.isRequired,
  onSetToday: r.func.isRequired,
  onViewChange: r.func.isRequired,
  /**
   * Force rendering in particular orientation.
   */
  orientation: r.oneOf(["landscape", "portrait"]),
  readOnly: r.bool,
  /**
   * The props used for each component slot.
   * @default {}
   */
  slotProps: r.object,
  /**
   * Overridable component slots.
   * @default {}
   */
  slots: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  value: r.any,
  view: r.oneOf(["day", "hours", "meridiem", "minutes", "month", "seconds", "year"]),
  views: r.arrayOf(r.oneOf(["day", "hours", "meridiem", "minutes", "month", "seconds", "year"]).isRequired).isRequired,
  wrapperVariant: r.oneOf(["desktop", "mobile"])
});
const P_ = (e) => Pe("MuiPickersSlideTransition", e), Ft = Ce("MuiPickersSlideTransition", ["root", "slideEnter-left", "slideEnter-right", "slideEnterActive", "slideExit", "slideExitActiveLeft-left", "slideExitActiveLeft-right"]), R_ = ["children", "className", "reduceAnimations", "slideDirection", "transKey", "classes"], D_ = (e) => {
  const {
    classes: t,
    slideDirection: n
  } = e, o = {
    root: ["root"],
    exit: ["slideExit"],
    enterActive: ["slideEnterActive"],
    enter: [`slideEnter-${n}`],
    exitActive: [`slideExitActiveLeft-${n}`]
  };
  return Se(o, P_, t);
}, $_ = Z(da, {
  name: "MuiPickersSlideTransition",
  slot: "Root",
  overridesResolver: (e, t) => [t.root, {
    [`.${Ft["slideEnter-left"]}`]: t["slideEnter-left"]
  }, {
    [`.${Ft["slideEnter-right"]}`]: t["slideEnter-right"]
  }, {
    [`.${Ft.slideEnterActive}`]: t.slideEnterActive
  }, {
    [`.${Ft.slideExit}`]: t.slideExit
  }, {
    [`.${Ft["slideExitActiveLeft-left"]}`]: t["slideExitActiveLeft-left"]
  }, {
    [`.${Ft["slideExitActiveLeft-right"]}`]: t["slideExitActiveLeft-right"]
  }]
})(({
  theme: e
}) => {
  const t = e.transitions.create("transform", {
    duration: e.transitions.duration.complex,
    easing: "cubic-bezier(0.35, 0.8, 0.4, 1)"
  });
  return {
    display: "block",
    position: "relative",
    overflowX: "hidden",
    "& > *": {
      position: "absolute",
      top: 0,
      right: 0,
      left: 0
    },
    [`& .${Ft["slideEnter-left"]}`]: {
      willChange: "transform",
      transform: "translate(100%)",
      zIndex: 1
    },
    [`& .${Ft["slideEnter-right"]}`]: {
      willChange: "transform",
      transform: "translate(-100%)",
      zIndex: 1
    },
    [`& .${Ft.slideEnterActive}`]: {
      transform: "translate(0%)",
      transition: t
    },
    [`& .${Ft.slideExit}`]: {
      transform: "translate(0%)"
    },
    [`& .${Ft["slideExitActiveLeft-left"]}`]: {
      willChange: "transform",
      transform: "translate(-100%)",
      transition: t,
      zIndex: 0
    },
    [`& .${Ft["slideExitActiveLeft-right"]}`]: {
      willChange: "transform",
      transform: "translate(100%)",
      transition: t,
      zIndex: 0
    }
  };
});
function k_(e) {
  const t = Ee({
    props: e,
    name: "MuiPickersSlideTransition"
  }), {
    children: n,
    className: o,
    reduceAnimations: a,
    transKey: s
    // extracting `classes` from `other`
  } = t, i = ie(t, R_), l = D_(t), c = Zt();
  if (a)
    return /* @__PURE__ */ x.jsx("div", {
      className: pe(l.root, o),
      children: n
    });
  const u = {
    exit: l.exit,
    enterActive: l.enterActive,
    enter: l.enter,
    exitActive: l.exitActive
  };
  return /* @__PURE__ */ x.jsx($_, {
    className: pe(l.root, o),
    childFactory: (d) => /* @__PURE__ */ g.cloneElement(d, {
      classNames: u
    }),
    role: "presentation",
    children: /* @__PURE__ */ x.jsx(Vl, b({
      mountOnEnter: !0,
      unmountOnExit: !0,
      timeout: c.transitions.duration.complex,
      classNames: u
    }, i, {
      children: n
    }), s)
  });
}
const Yh = ({
  shouldDisableDate: e,
  shouldDisableMonth: t,
  shouldDisableYear: n,
  minDate: o,
  maxDate: a,
  disableFuture: s,
  disablePast: i,
  timezone: l
}) => {
  const c = wr();
  return g.useCallback((u) => ri({
    adapter: c,
    value: u,
    props: {
      shouldDisableDate: e,
      shouldDisableMonth: t,
      shouldDisableYear: n,
      minDate: o,
      maxDate: a,
      disableFuture: s,
      disablePast: i,
      timezone: l
    }
  }) !== null, [c, e, t, n, o, a, s, i, l]);
}, __ = (e) => Pe("MuiDayCalendar", e);
Ce("MuiDayCalendar", ["root", "header", "weekDayLabel", "loadingContainer", "slideTransition", "monthContainer", "weekContainer", "weekNumberLabel", "weekNumber"]);
const M_ = ["parentProps", "day", "focusableDay", "selectedDays", "isDateDisabled", "currentMonthNumber", "isViewFocused"], I_ = ["ownerState"], N_ = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"],
    header: ["header"],
    weekDayLabel: ["weekDayLabel"],
    loadingContainer: ["loadingContainer"],
    slideTransition: ["slideTransition"],
    monthContainer: ["monthContainer"],
    weekContainer: ["weekContainer"],
    weekNumberLabel: ["weekNumberLabel"],
    weekNumber: ["weekNumber"]
  }, __, t);
}, Kh = (na + ei * 2) * 6, j_ = Z("div", {
  name: "MuiDayCalendar",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({}), A_ = Z("div", {
  name: "MuiDayCalendar",
  slot: "Header",
  overridesResolver: (e, t) => t.header
})({
  display: "flex",
  justifyContent: "center",
  alignItems: "center"
}), F_ = Z(Rt, {
  name: "MuiDayCalendar",
  slot: "WeekDayLabel",
  overridesResolver: (e, t) => t.weekDayLabel
})(({
  theme: e
}) => ({
  width: 36,
  height: 40,
  margin: "0 2px",
  textAlign: "center",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  color: (e.vars || e).palette.text.secondary
})), V_ = Z(Rt, {
  name: "MuiDayCalendar",
  slot: "WeekNumberLabel",
  overridesResolver: (e, t) => t.weekNumberLabel
})(({
  theme: e
}) => ({
  width: 36,
  height: 40,
  margin: "0 2px",
  textAlign: "center",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  color: e.palette.text.disabled
})), L_ = Z(Rt, {
  name: "MuiDayCalendar",
  slot: "WeekNumber",
  overridesResolver: (e, t) => t.weekNumber
})(({
  theme: e
}) => b({}, e.typography.caption, {
  width: na,
  height: na,
  padding: 0,
  margin: `0 ${ei}px`,
  color: e.palette.text.disabled,
  fontSize: "0.75rem",
  alignItems: "center",
  justifyContent: "center",
  display: "inline-flex"
})), B_ = Z("div", {
  name: "MuiDayCalendar",
  slot: "LoadingContainer",
  overridesResolver: (e, t) => t.loadingContainer
})({
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  minHeight: Kh
}), z_ = Z(k_, {
  name: "MuiDayCalendar",
  slot: "SlideTransition",
  overridesResolver: (e, t) => t.slideTransition
})({
  minHeight: Kh
}), W_ = Z("div", {
  name: "MuiDayCalendar",
  slot: "MonthContainer",
  overridesResolver: (e, t) => t.monthContainer
})({
  overflow: "hidden"
}), U_ = Z("div", {
  name: "MuiDayCalendar",
  slot: "WeekContainer",
  overridesResolver: (e, t) => t.weekContainer
})({
  margin: `${ei}px 0`,
  display: "flex",
  justifyContent: "center"
});
function H_(e) {
  var t, n, o;
  let {
    parentProps: a,
    day: s,
    focusableDay: i,
    selectedDays: l,
    isDateDisabled: c,
    currentMonthNumber: u,
    isViewFocused: d
  } = e, f = ie(e, M_);
  const {
    disabled: p,
    disableHighlightToday: m,
    isMonthSwitchingAnimating: v,
    showDaysOutsideCurrentMonth: h,
    components: y,
    componentsProps: w,
    slots: C,
    slotProps: E,
    timezone: O
  } = a, T = Ze(), P = Qs(O), S = i !== null && T.isSameDay(s, i), j = l.some((z) => T.isSameDay(z, s)), $ = T.isSameDay(s, P), V = (t = (n = C == null ? void 0 : C.day) != null ? n : y == null ? void 0 : y.Day) != null ? t : Pk, _ = Ye({
    elementType: V,
    externalSlotProps: (o = E == null ? void 0 : E.day) != null ? o : w == null ? void 0 : w.day,
    additionalProps: b({
      disableHighlightToday: m,
      showDaysOutsideCurrentMonth: h,
      role: "gridcell",
      isAnimating: v,
      // it is used in date range dragging logic by accessing `dataset.timestamp`
      "data-timestamp": T.toJsDate(s).valueOf()
    }, f),
    ownerState: b({}, a, {
      day: s,
      selected: j
    })
  }), L = ie(_, I_), M = g.useMemo(() => p || c(s), [p, c, s]), R = g.useMemo(() => T.getMonth(s) !== u, [T, s, u]), D = g.useMemo(() => {
    const z = T.startOfMonth(T.setMonth(s, u));
    return h ? T.isSameDay(s, T.startOfWeek(z)) : T.isSameDay(s, z);
  }, [u, s, h, T]), F = g.useMemo(() => {
    const z = T.endOfMonth(T.setMonth(s, u));
    return h ? T.isSameDay(s, T.endOfWeek(z)) : T.isSameDay(s, z);
  }, [u, s, h, T]);
  return /* @__PURE__ */ x.jsx(V, b({}, L, {
    day: s,
    disabled: M,
    autoFocus: d && S,
    today: $,
    outsideCurrentMonth: R,
    isFirstVisibleCell: D,
    isLastVisibleCell: F,
    selected: j,
    tabIndex: S ? 0 : -1,
    "aria-selected": j,
    "aria-current": $ ? "date" : void 0
  }));
}
function q_(e) {
  const t = Ee({
    props: e,
    name: "MuiDayCalendar"
  }), {
    onFocusedDayChange: n,
    className: o,
    currentMonth: a,
    selectedDays: s,
    focusedDay: i,
    loading: l,
    onSelectedDaysChange: c,
    onMonthSwitchingAnimationEnd: u,
    readOnly: d,
    reduceAnimations: f,
    renderLoading: p = () => /* @__PURE__ */ x.jsx("span", {
      children: "..."
    }),
    slideDirection: m,
    TransitionProps: v,
    disablePast: h,
    disableFuture: y,
    minDate: w,
    maxDate: C,
    shouldDisableDate: E,
    shouldDisableMonth: O,
    shouldDisableYear: T,
    dayOfWeekFormatter: P,
    hasFocus: S,
    onFocusedViewChange: j,
    gridLabelId: $,
    displayWeekNumber: V,
    fixedWeekNumber: _,
    autoFocus: L,
    timezone: M
  } = t, R = Qs(M), D = Ze(), F = N_(t), N = Zt().direction === "rtl", q = P || ((fe, ve) => D.format(ve, "weekdayShort").charAt(0).toUpperCase()), A = Yh({
    shouldDisableDate: E,
    shouldDisableMonth: O,
    shouldDisableYear: T,
    minDate: w,
    maxDate: C,
    disablePast: h,
    disableFuture: y,
    timezone: M
  }), H = Hn(), [te, re] = Ht({
    name: "DayCalendar",
    state: "hasFocus",
    controlled: S,
    default: L ?? !1
  }), [B, G] = g.useState(() => i || R), ee = we((fe) => {
    d || c(fe);
  }), W = (fe) => {
    A(fe) || (n(fe), G(fe), j == null || j(!0), re(!0));
  }, J = we((fe, ve) => {
    switch (fe.key) {
      case "ArrowUp":
        W(D.addDays(ve, -7)), fe.preventDefault();
        break;
      case "ArrowDown":
        W(D.addDays(ve, 7)), fe.preventDefault();
        break;
      case "ArrowLeft": {
        const oe = D.addDays(ve, N ? 1 : -1), ce = D.addMonths(ve, N ? 1 : -1), I = Bo({
          utils: D,
          date: oe,
          minDate: N ? oe : D.startOfMonth(ce),
          maxDate: N ? D.endOfMonth(ce) : oe,
          isDateDisabled: A,
          timezone: M
        });
        W(I || oe), fe.preventDefault();
        break;
      }
      case "ArrowRight": {
        const oe = D.addDays(ve, N ? -1 : 1), ce = D.addMonths(ve, N ? -1 : 1), I = Bo({
          utils: D,
          date: oe,
          minDate: N ? D.startOfMonth(ce) : oe,
          maxDate: N ? oe : D.endOfMonth(ce),
          isDateDisabled: A,
          timezone: M
        });
        W(I || oe), fe.preventDefault();
        break;
      }
      case "Home":
        W(D.startOfWeek(ve)), fe.preventDefault();
        break;
      case "End":
        W(D.endOfWeek(ve)), fe.preventDefault();
        break;
      case "PageUp":
        W(D.addMonths(ve, 1)), fe.preventDefault();
        break;
      case "PageDown":
        W(D.addMonths(ve, -1)), fe.preventDefault();
        break;
    }
  }), se = we((fe, ve) => W(ve)), le = we((fe, ve) => {
    te && D.isSameDay(B, ve) && (j == null || j(!1));
  }), X = D.getMonth(a), U = g.useMemo(() => s.filter((fe) => !!fe).map((fe) => D.startOfDay(fe)), [D, s]), K = X, Y = g.useMemo(() => /* @__PURE__ */ g.createRef(), [K]), he = D.startOfWeek(R), Oe = g.useMemo(() => {
    const fe = D.startOfMonth(a), ve = D.endOfMonth(a);
    return A(B) || D.isAfterDay(B, ve) || D.isBeforeDay(B, fe) ? Bo({
      utils: D,
      date: B,
      minDate: fe,
      maxDate: ve,
      disablePast: h,
      disableFuture: y,
      isDateDisabled: A,
      timezone: M
    }) : B;
  }, [a, y, h, B, A, D, M]), Ne = g.useMemo(() => {
    const fe = D.setTimezone(a, M), ve = D.getWeekArray(fe);
    let oe = D.addMonths(fe, 1);
    for (; _ && ve.length < _; ) {
      const ce = D.getWeekArray(oe), I = D.isSameDay(ve[ve.length - 1][0], ce[0][0]);
      ce.slice(I ? 1 : 0).forEach((Q) => {
        ve.length < _ && ve.push(Q);
      }), oe = D.addMonths(oe, 1);
    }
    return ve;
  }, [a, _, D, M]);
  return /* @__PURE__ */ x.jsxs(j_, {
    role: "grid",
    "aria-labelledby": $,
    className: F.root,
    children: [/* @__PURE__ */ x.jsxs(A_, {
      role: "row",
      className: F.header,
      children: [V && /* @__PURE__ */ x.jsx(V_, {
        variant: "caption",
        role: "columnheader",
        "aria-label": H.calendarWeekNumberHeaderLabel,
        className: F.weekNumberLabel,
        children: H.calendarWeekNumberHeaderText
      }), ik(D, R).map((fe, ve) => {
        var oe;
        const ce = D.format(fe, "weekdayShort");
        return /* @__PURE__ */ x.jsx(F_, {
          variant: "caption",
          role: "columnheader",
          "aria-label": D.format(D.addDays(he, ve), "weekday"),
          className: F.weekDayLabel,
          children: (oe = q == null ? void 0 : q(ce, fe)) != null ? oe : ce
        }, ce + ve.toString());
      })]
    }), l ? /* @__PURE__ */ x.jsx(B_, {
      className: F.loadingContainer,
      children: p()
    }) : /* @__PURE__ */ x.jsx(z_, b({
      transKey: K,
      onExited: u,
      reduceAnimations: f,
      slideDirection: m,
      className: pe(o, F.slideTransition)
    }, v, {
      nodeRef: Y,
      children: /* @__PURE__ */ x.jsx(W_, {
        ref: Y,
        role: "rowgroup",
        className: F.monthContainer,
        children: Ne.map((fe, ve) => /* @__PURE__ */ x.jsxs(U_, {
          role: "row",
          className: F.weekContainer,
          "aria-rowindex": ve + 1,
          children: [V && /* @__PURE__ */ x.jsx(L_, {
            className: F.weekNumber,
            role: "rowheader",
            "aria-label": H.calendarWeekNumberAriaLabelText(D.getWeekNumber(fe[0])),
            children: H.calendarWeekNumberText(D.getWeekNumber(fe[0]))
          }), fe.map((oe, ce) => /* @__PURE__ */ x.jsx(H_, {
            parentProps: t,
            day: oe,
            selectedDays: U,
            focusableDay: Oe,
            onKeyDown: J,
            onFocus: se,
            onBlur: le,
            onDaySelect: ee,
            isDateDisabled: A,
            currentMonthNumber: X,
            isViewFocused: te,
            "aria-colindex": ce + 1
          }, oe.toString()))]
        }, `week-${fe[0]}`))
      })
    }))]
  });
}
const Y_ = (e, t, n) => (o, a) => {
  switch (a.type) {
    case "changeMonth":
      return b({}, o, {
        slideDirection: a.direction,
        currentMonth: a.newMonth,
        isMonthSwitchingAnimating: !e
      });
    case "finishMonthSwitchingAnimation":
      return b({}, o, {
        isMonthSwitchingAnimating: !1
      });
    case "changeFocusedDay": {
      if (o.focusedDay != null && a.focusedDay != null && n.isSameDay(a.focusedDay, o.focusedDay))
        return o;
      const s = a.focusedDay != null && !t && !n.isSameMonth(o.currentMonth, a.focusedDay);
      return b({}, o, {
        focusedDay: a.focusedDay,
        isMonthSwitchingAnimating: s && !e && !a.withoutMonthSwitchingAnimation,
        currentMonth: s ? n.startOfMonth(a.focusedDay) : o.currentMonth,
        slideDirection: a.focusedDay != null && n.isAfterDay(a.focusedDay, o.currentMonth) ? "left" : "right"
      });
    }
    default:
      throw new Error("missing support");
  }
}, K_ = (e) => {
  const {
    value: t,
    referenceDate: n,
    defaultCalendarMonth: o,
    disableFuture: a,
    disablePast: s,
    disableSwitchToMonthOnDayFocus: i = !1,
    maxDate: l,
    minDate: c,
    onMonthChange: u,
    reduceAnimations: d,
    shouldDisableDate: f,
    timezone: p
  } = e, m = Ze(), v = g.useRef(Y_(!!d, i, m)).current, h = g.useMemo(
    () => {
      let S = null;
      return n ? S = n : o && (S = m.startOfMonth(o)), Cn.getInitialReferenceValue({
        value: t,
        utils: m,
        timezone: p,
        props: e,
        referenceDate: S,
        granularity: cn.day
      });
    },
    []
    // eslint-disable-line react-hooks/exhaustive-deps
  ), [y, w] = g.useReducer(v, {
    isMonthSwitchingAnimating: !1,
    focusedDay: h,
    currentMonth: m.startOfMonth(h),
    slideDirection: "left"
  }), C = g.useCallback((S) => {
    w(b({
      type: "changeMonth"
    }, S)), u && u(S.newMonth);
  }, [u]), E = g.useCallback((S) => {
    const j = S;
    m.isSameMonth(j, y.currentMonth) || C({
      newMonth: m.startOfMonth(j),
      direction: m.isAfterDay(j, y.currentMonth) ? "left" : "right"
    });
  }, [y.currentMonth, C, m]), O = Yh({
    shouldDisableDate: f,
    minDate: c,
    maxDate: l,
    disableFuture: a,
    disablePast: s,
    timezone: p
  }), T = g.useCallback(() => {
    w({
      type: "finishMonthSwitchingAnimation"
    });
  }, []), P = we((S, j) => {
    O(S) || w({
      type: "changeFocusedDay",
      focusedDay: S,
      withoutMonthSwitchingAnimation: j
    });
  });
  return {
    referenceDate: h,
    calendarState: y,
    changeMonth: E,
    changeFocusedDay: P,
    isDateDisabled: O,
    onMonthSwitchingAnimationEnd: T,
    handleChangeMonth: C
  };
}, G_ = ["ownerState"], X_ = ({
  clearable: e,
  fieldProps: t,
  InputProps: n,
  onClear: o,
  slots: a,
  slotProps: s,
  components: i,
  componentsProps: l
}) => {
  var c, u, d, f, p, m;
  const v = Hn(), h = (c = (u = a == null ? void 0 : a.clearButton) != null ? u : i == null ? void 0 : i.ClearButton) != null ? c : pr, y = Ye({
    elementType: h,
    externalSlotProps: (d = s == null ? void 0 : s.clearButton) != null ? d : l == null ? void 0 : l.clearButton,
    ownerState: {},
    className: "clearButton",
    additionalProps: {
      title: v.fieldClearLabel
    }
  }), w = ie(y, G_), C = (f = (p = a == null ? void 0 : a.clearIcon) != null ? p : i == null ? void 0 : i.ClearIcon) != null ? f : B$, E = Ye({
    elementType: C,
    externalSlotProps: (m = s == null ? void 0 : s.clearIcon) != null ? m : l == null ? void 0 : l.clearIcon,
    ownerState: {}
  }), O = b({}, n, {
    endAdornment: /* @__PURE__ */ x.jsxs(g.Fragment, {
      children: [e && /* @__PURE__ */ x.jsx(oc, {
        position: "end",
        sx: {
          marginRight: n != null && n.endAdornment ? -1 : -1.5
        },
        children: /* @__PURE__ */ x.jsx(h, b({}, w, {
          onClick: o,
          children: /* @__PURE__ */ x.jsx(C, b({
            fontSize: "small"
          }, E))
        }))
      }), n == null ? void 0 : n.endAdornment]
    })
  }), T = b({}, t, {
    sx: [{
      "& .clearButton": {
        opacity: 1
      },
      "@media (pointer: fine)": {
        "& .clearButton": {
          opacity: 0
        },
        "&:hover, &:focus-within": {
          ".clearButton": {
            opacity: 1
          }
        }
      }
    }, ...Array.isArray(t.sx) ? t.sx : [t.sx]]
  });
  return {
    InputProps: O,
    fieldProps: T
  };
}, Z_ = ["components", "componentsProps", "slots", "slotProps", "InputProps", "inputProps"], J_ = ["inputRef"], Q_ = ["ref", "onPaste", "onKeyDown", "inputMode", "readOnly", "clearable", "onClear"], Bc = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s;
  const i = Ee({
    props: t,
    name: "MuiDateField"
  }), {
    components: l,
    componentsProps: c,
    slots: u,
    slotProps: d,
    InputProps: f,
    inputProps: p
  } = i, m = ie(i, Z_), v = i, h = (o = (a = u == null ? void 0 : u.textField) != null ? a : l == null ? void 0 : l.TextField) != null ? o : os, y = Ye({
    elementType: h,
    externalSlotProps: (s = d == null ? void 0 : d.textField) != null ? s : c == null ? void 0 : c.textField,
    externalForwardedProps: m,
    ownerState: v
  }), {
    inputRef: w
  } = y, C = ie(y, J_);
  C.inputProps = b({}, p, C.inputProps), C.InputProps = b({}, f, C.InputProps);
  const E = zk({
    props: C,
    inputRef: w
  }), {
    ref: O,
    onPaste: T,
    onKeyDown: P,
    inputMode: S,
    readOnly: j,
    clearable: $,
    onClear: V
  } = E, _ = ie(E, Q_), {
    InputProps: L,
    fieldProps: M
  } = X_({
    onClear: V,
    clearable: $,
    fieldProps: _,
    InputProps: _.InputProps,
    slots: u,
    slotProps: d,
    components: l,
    componentsProps: c
  });
  return /* @__PURE__ */ x.jsx(h, b({
    ref: n
  }, M, {
    InputProps: b({}, L, {
      readOnly: j
    }),
    inputProps: b({}, _.inputProps, {
      inputMode: S,
      onPaste: T,
      onKeyDown: P,
      ref: O
    })
  }));
});
process.env.NODE_ENV !== "production" && (Bc.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * If `true`, the `input` element is focused during the first mount.
   * @default false
   */
  autoFocus: r.bool,
  className: r.string,
  /**
   * If `true`, a clear button will be shown in the field allowing value clearing.
   * @default false
   */
  clearable: r.bool,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * @default 'primary'
   */
  color: r.oneOf(["error", "info", "primary", "secondary", "success", "warning"]),
  component: r.elementType,
  /**
   * Overridable components.
   * @default {}
   * @deprecated Please use `slots`.
   */
  components: r.object,
  /**
   * The props used for each component slot.
   * @default {}
   * @deprecated Please use `slotProps`.
   */
  componentsProps: r.object,
  /**
   * The default value. Use when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the component is disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, disable values after the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disableFuture: r.bool,
  /**
   * If `true`, disable values before the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disablePast: r.bool,
  /**
   * If `true`, the component is displayed in focused state.
   */
  focused: r.bool,
  /**
   * Format of the date when rendered in the input(s).
   */
  format: r.string,
  /**
   * Density of the format when rendered in the input.
   * Setting `formatDensity` to `"spacious"` will add a space before and after each `/`, `-` and `.` character.
   * @default "dense"
   */
  formatDensity: r.oneOf(["dense", "spacious"]),
  /**
   * Props applied to the [`FormHelperText`](/material-ui/api/form-helper-text/) element.
   */
  FormHelperTextProps: r.object,
  /**
   * If `true`, the input will take up the full width of its container.
   * @default false
   */
  fullWidth: r.bool,
  /**
   * The helper text content.
   */
  helperText: r.node,
  /**
   * If `true`, the label is hidden.
   * This is used to increase density for a `FilledInput`.
   * Be sure to add `aria-label` to the `input` element.
   * @default false
   */
  hiddenLabel: r.bool,
  /**
   * The id of the `input` element.
   * Use this prop to make `label` and `helperText` accessible for screen readers.
   */
  id: r.string,
  /**
   * Props applied to the [`InputLabel`](/material-ui/api/input-label/) element.
   * Pointer events like `onClick` are enabled if and only if `shrink` is `true`.
   */
  InputLabelProps: r.object,
  /**
   * [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Attributes) applied to the `input` element.
   */
  inputProps: r.object,
  /**
   * Props applied to the Input element.
   * It will be a [`FilledInput`](/material-ui/api/filled-input/),
   * [`OutlinedInput`](/material-ui/api/outlined-input/) or [`Input`](/material-ui/api/input/)
   * component depending on the `variant` prop value.
   */
  InputProps: r.object,
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * The label content.
   */
  label: r.node,
  /**
   * If `dense` or `normal`, will adjust vertical spacing of this and contained components.
   * @default 'none'
   */
  margin: r.oneOf(["dense", "none", "normal"]),
  /**
   * Maximal selectable date.
   */
  maxDate: r.any,
  /**
   * Minimal selectable date.
   */
  minDate: r.any,
  /**
   * Name attribute of the `input` element.
   */
  name: r.string,
  onBlur: r.func,
  /**
   * Callback fired when the value changes.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TError The validation error type. Will be either `string` or a `null`. Can be in `[start, end]` format in case of range value.
   * @param {TValue} value The new value.
   * @param {FieldChangeHandlerContext<TError>} context The context containing the validation result of the current value.
   */
  onChange: r.func,
  /**
   * Callback fired when the clear button is clicked.
   */
  onClear: r.func,
  /**
   * Callback fired when the error associated to the current value changes.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TError The validation error type. Will be either `string` or a `null`. Can be in `[start, end]` format in case of range value.
   * @param {TError} error The new error.
   * @param {TValue} value The value associated to the error.
   */
  onError: r.func,
  onFocus: r.func,
  /**
   * Callback fired when the selected sections change.
   * @param {FieldSelectedSections} newValue The new selected sections.
   */
  onSelectedSectionsChange: r.func,
  /**
   * It prevents the user from changing the value of the field
   * (not from interacting with the field).
   * @default false
   */
  readOnly: r.bool,
  /**
   * The date used to generate a part of the new value that is not present in the format when both `value` and `defaultValue` are empty.
   * For example, on time fields it will be used to determine the date to set.
   * @default The closest valid date using the validation props, except callbacks such as `shouldDisableDate`. Value is rounded to the most granular section used.
   */
  referenceDate: r.any,
  /**
   * If `true`, the label is displayed as required and the `input` element is required.
   * @default false
   */
  required: r.bool,
  /**
   * The currently selected sections.
   * This prop accept four formats:
   * 1. If a number is provided, the section at this index will be selected.
   * 2. If an object with a `startIndex` and `endIndex` properties are provided, the sections between those two indexes will be selected.
   * 3. If a string of type `FieldSectionType` is provided, the first section with that name will be selected.
   * 4. If `null` is provided, no section will be selected
   * If not provided, the selected sections will be handled internally.
   */
  selectedSections: r.oneOfType([r.oneOf(["all", "day", "hours", "meridiem", "minutes", "month", "seconds", "weekDay", "year"]), r.number, r.shape({
    endIndex: r.number.isRequired,
    startIndex: r.number.isRequired
  })]),
  /**
   * Disable specific date.
   *
   * Warning: This function can be called multiple times (e.g. when rendering date calendar, checking if focus can be moved to a certain date, etc.). Expensive computations can impact performance.
   *
   * @template TDate
   * @param {TDate} day The date to test.
   * @returns {boolean} If `true` the date will be disabled.
   */
  shouldDisableDate: r.func,
  /**
   * Disable specific month.
   * @template TDate
   * @param {TDate} month The month to test.
   * @returns {boolean} If `true`, the month will be disabled.
   */
  shouldDisableMonth: r.func,
  /**
   * Disable specific year.
   * @template TDate
   * @param {TDate} year The year to test.
   * @returns {boolean} If `true`, the year will be disabled.
   */
  shouldDisableYear: r.func,
  /**
   * If `true`, the format will respect the leading zeroes (e.g: on dayjs, the format `M/D/YYYY` will render `8/16/2018`)
   * If `false`, the format will always add leading zeroes (e.g: on dayjs, the format `M/D/YYYY` will render `08/16/2018`)
   *
   * Warning n°1: Luxon is not able to respect the leading zeroes when using macro tokens (e.g: "DD"), so `shouldRespectLeadingZeros={true}` might lead to inconsistencies when using `AdapterLuxon`.
   *
   * Warning n°2: When `shouldRespectLeadingZeros={true}`, the field will add an invisible character on the sections containing a single digit to make sure `onChange` is fired.
   * If you need to get the clean value from the input, you can remove this character using `input.value.replace(/\u200e/g, '')`.
   *
   * Warning n°3: When used in strict mode, dayjs and moment require to respect the leading zeros.
   * This mean that when using `shouldRespectLeadingZeros={false}`, if you retrieve the value directly from the input (not listening to `onChange`) and your format contains tokens without leading zeros, the value will not be parsed by your library.
   *
   * @default `false`
   */
  shouldRespectLeadingZeros: r.bool,
  /**
   * The size of the component.
   */
  size: r.oneOf(["medium", "small"]),
  /**
   * The props used for each component slot.
   * @default {}
   */
  slotProps: r.object,
  /**
   * Overridable component slots.
   * @default {}
   */
  slots: r.object,
  style: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Choose which timezone to use for the value.
   * Example: "default", "system", "UTC", "America/New_York".
   * If you pass values from other timezones to some props, they will be converted to this timezone before being used.
   * @see See the {@link https://mui.com/x/react-date-pickers/timezone/ timezones documention} for more details.
   * @default The timezone of the `value` or `defaultValue` prop is defined, 'default' otherwise.
   */
  timezone: r.string,
  /**
   * The ref object used to imperatively interact with the field.
   */
  unstableFieldRef: r.oneOfType([r.func, r.object]),
  /**
   * The selected value.
   * Used when the component is controlled.
   */
  value: r.any,
  /**
   * The variant to use.
   * @default 'outlined'
   */
  variant: r.oneOf(["filled", "outlined", "standard"])
});
const eM = (e) => Pe("MuiPickersFadeTransitionGroup", e);
Ce("MuiPickersFadeTransitionGroup", ["root"]);
const tM = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"]
  }, eM, t);
}, nM = Z(da, {
  name: "MuiPickersFadeTransitionGroup",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "block",
  position: "relative"
});
function Gh(e) {
  const t = Ee({
    props: e,
    name: "MuiPickersFadeTransitionGroup"
  }), {
    children: n,
    className: o,
    reduceAnimations: a,
    transKey: s
  } = t, i = tM(t), l = Zt();
  return a ? n : /* @__PURE__ */ x.jsx(nM, {
    className: pe(i.root, o),
    children: /* @__PURE__ */ x.jsx(mr, {
      appear: !1,
      mountOnEnter: !0,
      unmountOnExit: !0,
      timeout: {
        appear: l.transitions.duration.enteringScreen,
        enter: l.transitions.duration.enteringScreen,
        exit: 0
      },
      children: n
    }, s)
  });
}
function rM(e) {
  return Pe("MuiPickersMonth", e);
}
const Na = Ce("MuiPickersMonth", ["root", "monthButton", "disabled", "selected"]), oM = ["autoFocus", "children", "disabled", "selected", "value", "tabIndex", "onClick", "onKeyDown", "onFocus", "onBlur", "aria-current", "aria-label", "monthsPerRow"], aM = (e) => {
  const {
    disabled: t,
    selected: n,
    classes: o
  } = e;
  return Se({
    root: ["root"],
    monthButton: ["monthButton", t && "disabled", n && "selected"]
  }, rM, o);
}, sM = Z("div", {
  name: "MuiPickersMonth",
  slot: "Root",
  overridesResolver: (e, t) => [t.root]
})(({
  ownerState: e
}) => ({
  flexBasis: e.monthsPerRow === 3 ? "33.3%" : "25%",
  display: "flex",
  alignItems: "center",
  justifyContent: "center"
})), iM = Z("button", {
  name: "MuiPickersMonth",
  slot: "MonthButton",
  overridesResolver: (e, t) => [t.monthButton, {
    [`&.${Na.disabled}`]: t.disabled
  }, {
    [`&.${Na.selected}`]: t.selected
  }]
})(({
  theme: e
}) => b({
  color: "unset",
  backgroundColor: "transparent",
  border: 0,
  outline: 0
}, e.typography.subtitle1, {
  margin: "8px 0",
  height: 36,
  width: 72,
  borderRadius: 18,
  cursor: "pointer",
  "&:focus": {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.action.activeChannel} / ${e.vars.palette.action.hoverOpacity})` : Xr(e.palette.action.active, e.palette.action.hoverOpacity)
  },
  "&:hover": {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.action.activeChannel} / ${e.vars.palette.action.hoverOpacity})` : Xr(e.palette.action.active, e.palette.action.hoverOpacity)
  },
  "&:disabled": {
    cursor: "auto",
    pointerEvents: "none"
  },
  [`&.${Na.disabled}`]: {
    color: (e.vars || e).palette.text.secondary
  },
  [`&.${Na.selected}`]: {
    color: (e.vars || e).palette.primary.contrastText,
    backgroundColor: (e.vars || e).palette.primary.main,
    "&:focus, &:hover": {
      backgroundColor: (e.vars || e).palette.primary.dark
    }
  }
})), lM = /* @__PURE__ */ g.memo(function(t) {
  const n = Ee({
    props: t,
    name: "MuiPickersMonth"
  }), {
    autoFocus: o,
    children: a,
    disabled: s,
    selected: i,
    value: l,
    tabIndex: c,
    onClick: u,
    onKeyDown: d,
    onFocus: f,
    onBlur: p,
    "aria-current": m,
    "aria-label": v
    // We don't want to forward this prop to the root element
  } = n, h = ie(n, oM), y = g.useRef(null), w = aM(n);
  return ft(() => {
    if (o) {
      var C;
      (C = y.current) == null || C.focus();
    }
  }, [o]), /* @__PURE__ */ x.jsx(sM, b({
    className: w.root,
    ownerState: n
  }, h, {
    children: /* @__PURE__ */ x.jsx(iM, {
      ref: y,
      disabled: s,
      type: "button",
      role: "radio",
      tabIndex: s ? -1 : c,
      "aria-current": m,
      "aria-checked": i,
      "aria-label": v,
      onClick: (C) => u(C, l),
      onKeyDown: (C) => d(C, l),
      onFocus: (C) => f(C, l),
      onBlur: (C) => p(C, l),
      className: w.monthButton,
      ownerState: n,
      children: a
    })
  }));
});
function cM(e) {
  return Pe("MuiMonthCalendar", e);
}
Ce("MuiMonthCalendar", ["root"]);
const uM = ["className", "value", "defaultValue", "referenceDate", "disabled", "disableFuture", "disablePast", "maxDate", "minDate", "onChange", "shouldDisableMonth", "readOnly", "disableHighlightToday", "autoFocus", "onMonthFocus", "hasFocus", "onFocusedViewChange", "monthsPerRow", "timezone", "gridLabelId"], dM = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"]
  }, cM, t);
};
function pM(e, t) {
  const n = Ze(), o = Ta(), a = Ee({
    props: e,
    name: t
  });
  return b({
    disableFuture: !1,
    disablePast: !1
  }, a, {
    minDate: Gt(n, a.minDate, o.minDate),
    maxDate: Gt(n, a.maxDate, o.maxDate)
  });
}
const fM = Z("div", {
  name: "MuiMonthCalendar",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "flex",
  flexWrap: "wrap",
  alignContent: "stretch",
  padding: "0 4px",
  width: ti,
  // avoid padding increasing width over defined
  boxSizing: "border-box"
}), Xh = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = pM(t, "MuiMonthCalendar"), {
    className: a,
    value: s,
    defaultValue: i,
    referenceDate: l,
    disabled: c,
    disableFuture: u,
    disablePast: d,
    maxDate: f,
    minDate: p,
    onChange: m,
    shouldDisableMonth: v,
    readOnly: h,
    disableHighlightToday: y,
    autoFocus: w = !1,
    onMonthFocus: C,
    hasFocus: E,
    onFocusedViewChange: O,
    monthsPerRow: T = 3,
    timezone: P,
    gridLabelId: S
  } = o, j = ie(o, uM), {
    value: $,
    handleValueChange: V,
    timezone: _
  } = Ac({
    name: "MonthCalendar",
    timezone: P,
    value: s,
    defaultValue: i,
    onChange: m,
    valueManager: Cn
  }), L = Qs(_), M = lo(), R = Ze(), D = g.useMemo(
    () => Cn.getInitialReferenceValue({
      value: $,
      utils: R,
      props: o,
      timezone: _,
      referenceDate: l,
      granularity: cn.month
    }),
    []
    // eslint-disable-line react-hooks/exhaustive-deps
  ), F = o, z = dM(F), N = g.useMemo(() => R.getMonth(L), [R, L]), q = g.useMemo(() => $ != null ? R.getMonth($) : y ? null : R.getMonth(D), [$, R, y, D]), [A, H] = g.useState(() => q || N), [te, re] = Ht({
    name: "MonthCalendar",
    state: "hasFocus",
    controlled: E,
    default: w ?? !1
  }), B = we((X) => {
    re(X), O && O(X);
  }), G = g.useCallback((X) => {
    const U = R.startOfMonth(d && R.isAfter(L, p) ? L : p), K = R.startOfMonth(u && R.isBefore(L, f) ? L : f), Y = R.startOfMonth(X);
    return R.isBefore(Y, U) || R.isAfter(Y, K) ? !0 : v ? v(Y) : !1;
  }, [u, d, f, p, L, v, R]), ee = we((X, U) => {
    if (h)
      return;
    const K = R.setMonth($ ?? D, U);
    V(K);
  }), W = we((X) => {
    G(R.setMonth($ ?? D, X)) || (H(X), B(!0), C && C(X));
  });
  g.useEffect(() => {
    H((X) => q !== null && X !== q ? q : X);
  }, [q]);
  const J = we((X, U) => {
    switch (X.key) {
      case "ArrowUp":
        W((12 + U - 3) % 12), X.preventDefault();
        break;
      case "ArrowDown":
        W((12 + U + 3) % 12), X.preventDefault();
        break;
      case "ArrowLeft":
        W((12 + U + (M.direction === "ltr" ? -1 : 1)) % 12), X.preventDefault();
        break;
      case "ArrowRight":
        W((12 + U + (M.direction === "ltr" ? 1 : -1)) % 12), X.preventDefault();
        break;
    }
  }), se = we((X, U) => {
    W(U);
  }), le = we((X, U) => {
    A === U && B(!1);
  });
  return /* @__PURE__ */ x.jsx(fM, b({
    ref: n,
    className: pe(z.root, a),
    ownerState: F,
    role: "radiogroup",
    "aria-labelledby": S
  }, j, {
    children: Ic(R, $ ?? D).map((X) => {
      const U = R.getMonth(X), K = R.format(X, "monthShort"), Y = R.format(X, "month"), he = U === q, Oe = c || G(X);
      return /* @__PURE__ */ x.jsx(lM, {
        selected: he,
        value: U,
        onClick: ee,
        onKeyDown: J,
        autoFocus: te && U === A,
        disabled: Oe,
        tabIndex: U === A ? 0 : -1,
        onFocus: se,
        onBlur: le,
        "aria-current": N === U ? "date" : void 0,
        "aria-label": Y,
        monthsPerRow: T,
        children: K
      }, K);
    })
  }));
});
process.env.NODE_ENV !== "production" && (Xh.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  autoFocus: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * className applied to the root element.
   */
  className: r.string,
  /**
   * The default selected value.
   * Used when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true` picker is disabled
   */
  disabled: r.bool,
  /**
   * If `true`, disable values after the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disableFuture: r.bool,
  /**
   * If `true`, today's date is rendering without highlighting with circle.
   * @default false
   */
  disableHighlightToday: r.bool,
  /**
   * If `true`, disable values before the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disablePast: r.bool,
  gridLabelId: r.string,
  hasFocus: r.bool,
  /**
   * Maximal selectable date.
   */
  maxDate: r.any,
  /**
   * Minimal selectable date.
   */
  minDate: r.any,
  /**
   * Months rendered per row.
   * @default 3
   */
  monthsPerRow: r.oneOf([3, 4]),
  /**
   * Callback fired when the value changes.
   * @template TDate
   * @param {TDate} value The new value.
   */
  onChange: r.func,
  onFocusedViewChange: r.func,
  onMonthFocus: r.func,
  /**
   * If `true` picker is readonly
   */
  readOnly: r.bool,
  /**
   * The date used to generate the new value when both `value` and `defaultValue` are empty.
   * @default The closest valid month using the validation props, except callbacks such as `shouldDisableMonth`.
   */
  referenceDate: r.any,
  /**
   * Disable specific month.
   * @template TDate
   * @param {TDate} month The month to test.
   * @returns {boolean} If `true`, the month will be disabled.
   */
  shouldDisableMonth: r.func,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Choose which timezone to use for the value.
   * Example: "default", "system", "UTC", "America/New_York".
   * If you pass values from other timezones to some props, they will be converted to this timezone before being used.
   * @see See the {@link https://mui.com/x/react-date-pickers/timezone/ timezones documention} for more details.
   * @default The timezone of the `value` or `defaultValue` prop is defined, 'default' otherwise.
   */
  timezone: r.string,
  /**
   * The selected value.
   * Used when the component is controlled.
   */
  value: r.any
});
function mM(e) {
  return Pe("MuiPickersYear", e);
}
const ja = Ce("MuiPickersYear", ["root", "yearButton", "selected", "disabled"]), hM = ["autoFocus", "className", "children", "disabled", "selected", "value", "tabIndex", "onClick", "onKeyDown", "onFocus", "onBlur", "aria-current", "yearsPerRow"], bM = (e) => {
  const {
    disabled: t,
    selected: n,
    classes: o
  } = e;
  return Se({
    root: ["root"],
    yearButton: ["yearButton", t && "disabled", n && "selected"]
  }, mM, o);
}, gM = Z("div", {
  name: "MuiPickersYear",
  slot: "Root",
  overridesResolver: (e, t) => [t.root]
})(({
  ownerState: e
}) => ({
  flexBasis: e.yearsPerRow === 3 ? "33.3%" : "25%",
  display: "flex",
  alignItems: "center",
  justifyContent: "center"
})), yM = Z("button", {
  name: "MuiPickersYear",
  slot: "YearButton",
  overridesResolver: (e, t) => [t.yearButton, {
    [`&.${ja.disabled}`]: t.disabled
  }, {
    [`&.${ja.selected}`]: t.selected
  }]
})(({
  theme: e
}) => b({
  color: "unset",
  backgroundColor: "transparent",
  border: 0,
  outline: 0
}, e.typography.subtitle1, {
  margin: "6px 0",
  height: 36,
  width: 72,
  borderRadius: 18,
  cursor: "pointer",
  "&:focus": {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.action.activeChannel} / ${e.vars.palette.action.focusOpacity})` : Xr(e.palette.action.active, e.palette.action.focusOpacity)
  },
  "&:hover": {
    backgroundColor: e.vars ? `rgba(${e.vars.palette.action.activeChannel} / ${e.vars.palette.action.hoverOpacity})` : Xr(e.palette.action.active, e.palette.action.hoverOpacity)
  },
  "&:disabled": {
    cursor: "auto",
    pointerEvents: "none"
  },
  [`&.${ja.disabled}`]: {
    color: (e.vars || e).palette.text.secondary
  },
  [`&.${ja.selected}`]: {
    color: (e.vars || e).palette.primary.contrastText,
    backgroundColor: (e.vars || e).palette.primary.main,
    "&:focus, &:hover": {
      backgroundColor: (e.vars || e).palette.primary.dark
    }
  }
})), vM = /* @__PURE__ */ g.memo(function(t) {
  const n = Ee({
    props: t,
    name: "MuiPickersYear"
  }), {
    autoFocus: o,
    className: a,
    children: s,
    disabled: i,
    selected: l,
    value: c,
    tabIndex: u,
    onClick: d,
    onKeyDown: f,
    onFocus: p,
    onBlur: m,
    "aria-current": v
    // We don't want to forward this prop to the root element
  } = n, h = ie(n, hM), y = g.useRef(null), w = bM(n);
  return g.useEffect(() => {
    o && y.current.focus();
  }, [o]), /* @__PURE__ */ x.jsx(gM, b({
    className: pe(w.root, a),
    ownerState: n
  }, h, {
    children: /* @__PURE__ */ x.jsx(yM, {
      ref: y,
      disabled: i,
      type: "button",
      role: "radio",
      tabIndex: i ? -1 : u,
      "aria-current": v,
      "aria-checked": l,
      onClick: (C) => d(C, c),
      onKeyDown: (C) => f(C, c),
      onFocus: (C) => p(C, c),
      onBlur: (C) => m(C, c),
      className: w.yearButton,
      ownerState: n,
      children: s
    })
  }));
});
function xM(e) {
  return Pe("MuiYearCalendar", e);
}
Ce("MuiYearCalendar", ["root"]);
const TM = ["autoFocus", "className", "value", "defaultValue", "referenceDate", "disabled", "disableFuture", "disablePast", "maxDate", "minDate", "onChange", "readOnly", "shouldDisableYear", "disableHighlightToday", "onYearFocus", "hasFocus", "onFocusedViewChange", "yearsPerRow", "timezone", "gridLabelId"], wM = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"]
  }, xM, t);
};
function EM(e, t) {
  var n;
  const o = Ze(), a = Ta(), s = Ee({
    props: e,
    name: t
  });
  return b({
    disablePast: !1,
    disableFuture: !1
  }, s, {
    yearsPerRow: (n = s.yearsPerRow) != null ? n : 3,
    minDate: Gt(o, s.minDate, a.minDate),
    maxDate: Gt(o, s.maxDate, a.maxDate)
  });
}
const CM = Z("div", {
  name: "MuiYearCalendar",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "flex",
  flexDirection: "row",
  flexWrap: "wrap",
  overflowY: "auto",
  height: "100%",
  padding: "0 4px",
  width: ti,
  maxHeight: nk,
  // avoid padding increasing width over defined
  boxSizing: "border-box",
  position: "relative"
}), Zh = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = EM(t, "MuiYearCalendar"), {
    autoFocus: a,
    className: s,
    value: i,
    defaultValue: l,
    referenceDate: c,
    disabled: u,
    disableFuture: d,
    disablePast: f,
    maxDate: p,
    minDate: m,
    onChange: v,
    readOnly: h,
    shouldDisableYear: y,
    disableHighlightToday: w,
    onYearFocus: C,
    hasFocus: E,
    onFocusedViewChange: O,
    yearsPerRow: T,
    timezone: P,
    gridLabelId: S
  } = o, j = ie(o, TM), {
    value: $,
    handleValueChange: V,
    timezone: _
  } = Ac({
    name: "YearCalendar",
    timezone: P,
    value: i,
    defaultValue: l,
    onChange: v,
    valueManager: Cn
  }), L = Qs(_), M = lo(), R = Ze(), D = g.useMemo(
    () => Cn.getInitialReferenceValue({
      value: $,
      utils: R,
      props: o,
      timezone: _,
      referenceDate: c,
      granularity: cn.year
    }),
    []
    // eslint-disable-line react-hooks/exhaustive-deps
  ), F = o, z = wM(F), N = g.useMemo(() => R.getYear(L), [R, L]), q = g.useMemo(() => $ != null ? R.getYear($) : w ? null : R.getYear(D), [$, R, w, D]), [A, H] = g.useState(() => q || N), [te, re] = Ht({
    name: "YearCalendar",
    state: "hasFocus",
    controlled: E,
    default: a ?? !1
  }), B = we((K) => {
    re(K), O && O(K);
  }), G = g.useCallback((K) => {
    if (f && R.isBeforeYear(K, L) || d && R.isAfterYear(K, L) || m && R.isBeforeYear(K, m) || p && R.isAfterYear(K, p))
      return !0;
    if (!y)
      return !1;
    const Y = R.startOfYear(K);
    return y(Y);
  }, [d, f, p, m, L, y, R]), ee = we((K, Y) => {
    if (h)
      return;
    const he = R.setYear($ ?? D, Y);
    V(he);
  }), W = we((K) => {
    G(R.setYear($ ?? D, K)) || (H(K), B(!0), C == null || C(K));
  });
  g.useEffect(() => {
    H((K) => q !== null && K !== q ? q : K);
  }, [q]);
  const J = we((K, Y) => {
    switch (K.key) {
      case "ArrowUp":
        W(Y - T), K.preventDefault();
        break;
      case "ArrowDown":
        W(Y + T), K.preventDefault();
        break;
      case "ArrowLeft":
        W(Y + (M.direction === "ltr" ? -1 : 1)), K.preventDefault();
        break;
      case "ArrowRight":
        W(Y + (M.direction === "ltr" ? 1 : -1)), K.preventDefault();
        break;
    }
  }), se = we((K, Y) => {
    W(Y);
  }), le = we((K, Y) => {
    A === Y && B(!1);
  }), X = g.useRef(null), U = Ke(n, X);
  return g.useEffect(() => {
    if (a || X.current === null)
      return;
    const K = X.current.querySelector('[tabindex="0"]');
    if (!K)
      return;
    const Y = K.offsetHeight, he = K.offsetTop, Oe = X.current.clientHeight, Ne = X.current.scrollTop, fe = he + Y;
    Y > Oe || he < Ne || (X.current.scrollTop = fe - Oe / 2 - Y / 2);
  }, [a]), /* @__PURE__ */ x.jsx(CM, b({
    ref: U,
    className: pe(z.root, s),
    ownerState: F,
    role: "radiogroup",
    "aria-labelledby": S
  }, j, {
    children: R.getYearRange(m, p).map((K) => {
      const Y = R.getYear(K), he = Y === q, Oe = u || G(K);
      return /* @__PURE__ */ x.jsx(vM, {
        selected: he,
        value: Y,
        onClick: ee,
        onKeyDown: J,
        autoFocus: te && Y === A,
        disabled: Oe,
        tabIndex: Y === A ? 0 : -1,
        onFocus: se,
        onBlur: le,
        "aria-current": N === Y ? "date" : void 0,
        yearsPerRow: T,
        children: R.format(K, "year")
      }, R.format(K, "year"));
    })
  }));
});
process.env.NODE_ENV !== "production" && (Zh.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  autoFocus: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * className applied to the root element.
   */
  className: r.string,
  /**
   * The default selected value.
   * Used when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true` picker is disabled
   */
  disabled: r.bool,
  /**
   * If `true`, disable values after the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disableFuture: r.bool,
  /**
   * If `true`, today's date is rendering without highlighting with circle.
   * @default false
   */
  disableHighlightToday: r.bool,
  /**
   * If `true`, disable values before the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disablePast: r.bool,
  gridLabelId: r.string,
  hasFocus: r.bool,
  /**
   * Maximal selectable date.
   */
  maxDate: r.any,
  /**
   * Minimal selectable date.
   */
  minDate: r.any,
  /**
   * Callback fired when the value changes.
   * @template TDate
   * @param {TDate} value The new value.
   */
  onChange: r.func,
  onFocusedViewChange: r.func,
  onYearFocus: r.func,
  /**
   * If `true` picker is readonly
   */
  readOnly: r.bool,
  /**
   * The date used to generate the new value when both `value` and `defaultValue` are empty.
   * @default The closest valid year using the validation props, except callbacks such as `shouldDisableYear`.
   */
  referenceDate: r.any,
  /**
   * Disable specific year.
   * @template TDate
   * @param {TDate} year The year to test.
   * @returns {boolean} If `true`, the year will be disabled.
   */
  shouldDisableYear: r.func,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Choose which timezone to use for the value.
   * Example: "default", "system", "UTC", "America/New_York".
   * If you pass values from other timezones to some props, they will be converted to this timezone before being used.
   * @see See the {@link https://mui.com/x/react-date-pickers/timezone/ timezones documention} for more details.
   * @default The timezone of the `value` or `defaultValue` prop is defined, 'default' otherwise.
   */
  timezone: r.string,
  /**
   * The selected value.
   * Used when the component is controlled.
   */
  value: r.any,
  /**
   * Years rendered per row.
   * @default 3
   */
  yearsPerRow: r.oneOf([3, 4])
});
const OM = (e) => Pe("MuiPickersCalendarHeader", e), SM = Ce("MuiPickersCalendarHeader", ["root", "labelContainer", "label", "switchViewButton", "switchViewIcon"]), PM = ["slots", "slotProps", "components", "componentsProps", "currentMonth", "disabled", "disableFuture", "disablePast", "maxDate", "minDate", "onMonthChange", "onViewChange", "view", "reduceAnimations", "views", "labelId", "className", "timezone"], RM = ["ownerState"], DM = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"],
    labelContainer: ["labelContainer"],
    label: ["label"],
    switchViewButton: ["switchViewButton"],
    switchViewIcon: ["switchViewIcon"]
  }, OM, t);
}, $M = Z("div", {
  name: "MuiPickersCalendarHeader",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "flex",
  alignItems: "center",
  marginTop: 16,
  marginBottom: 8,
  paddingLeft: 24,
  paddingRight: 12,
  // prevent jumping in safari
  maxHeight: 30,
  minHeight: 30
}), kM = Z("div", {
  name: "MuiPickersCalendarHeader",
  slot: "LabelContainer",
  overridesResolver: (e, t) => t.labelContainer
})(({
  theme: e
}) => b({
  display: "flex",
  overflow: "hidden",
  alignItems: "center",
  cursor: "pointer",
  marginRight: "auto"
}, e.typography.body1, {
  fontWeight: e.typography.fontWeightMedium
})), _M = Z("div", {
  name: "MuiPickersCalendarHeader",
  slot: "Label",
  overridesResolver: (e, t) => t.label
})({
  marginRight: 6
}), MM = Z(pr, {
  name: "MuiPickersCalendarHeader",
  slot: "SwitchViewButton",
  overridesResolver: (e, t) => t.switchViewButton
})(({
  ownerState: e
}) => b({
  marginRight: "auto"
}, e.view === "year" && {
  [`.${SM.switchViewIcon}`]: {
    transform: "rotate(180deg)"
  }
})), IM = Z(A$, {
  name: "MuiPickersCalendarHeader",
  slot: "SwitchViewIcon",
  overridesResolver: (e, t) => t.switchViewIcon
})(({
  theme: e
}) => ({
  willChange: "transform",
  transition: e.transitions.create("transform"),
  transform: "rotate(0deg)"
})), Jh = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s, i;
  const l = Hn(), c = Ze(), u = Ee({
    props: t,
    name: "MuiPickersCalendarHeader"
  }), {
    slots: d,
    slotProps: f,
    components: p,
    currentMonth: m,
    disabled: v,
    disableFuture: h,
    disablePast: y,
    maxDate: w,
    minDate: C,
    onMonthChange: E,
    onViewChange: O,
    view: T,
    reduceAnimations: P,
    views: S,
    labelId: j,
    className: $,
    timezone: V
  } = u, _ = ie(u, PM), L = u, M = DM(u), R = (o = (a = d == null ? void 0 : d.switchViewButton) != null ? a : p == null ? void 0 : p.SwitchViewButton) != null ? o : MM, D = Ye({
    elementType: R,
    externalSlotProps: f == null ? void 0 : f.switchViewButton,
    additionalProps: {
      size: "small",
      "aria-label": l.calendarViewSwitchingButtonAriaLabel(T)
    },
    ownerState: L,
    className: M.switchViewButton
  }), F = (s = (i = d == null ? void 0 : d.switchViewIcon) != null ? i : p == null ? void 0 : p.SwitchViewIcon) != null ? s : IM, z = Ye({
    elementType: F,
    externalSlotProps: f == null ? void 0 : f.switchViewIcon,
    ownerState: void 0,
    className: M.switchViewIcon
  }), N = ie(z, RM), q = () => E(c.addMonths(m, 1), "left"), A = () => E(c.addMonths(m, -1), "right"), H = ek(m, {
    disableFuture: h,
    maxDate: w,
    timezone: V
  }), te = tk(m, {
    disablePast: y,
    minDate: C,
    timezone: V
  }), re = () => {
    if (!(S.length === 1 || !O || v))
      if (S.length === 2)
        O(S.find((B) => B !== T) || S[0]);
      else {
        const B = S.indexOf(T) !== 0 ? 0 : 1;
        O(S[B]);
      }
  };
  return S.length === 1 && S[0] === "year" ? null : /* @__PURE__ */ x.jsxs($M, b({}, _, {
    ownerState: L,
    className: pe($, M.root),
    ref: n,
    children: [/* @__PURE__ */ x.jsxs(kM, {
      role: "presentation",
      onClick: re,
      ownerState: L,
      "aria-live": "polite",
      className: M.labelContainer,
      children: [/* @__PURE__ */ x.jsx(Gh, {
        reduceAnimations: P,
        transKey: c.format(m, "monthAndYear"),
        children: /* @__PURE__ */ x.jsx(_M, {
          id: j,
          ownerState: L,
          className: M.label,
          children: c.format(m, "monthAndYear")
        })
      }), S.length > 1 && !v && /* @__PURE__ */ x.jsx(R, b({}, D, {
        children: /* @__PURE__ */ x.jsx(F, b({}, N))
      }))]
    }), /* @__PURE__ */ x.jsx(mr, {
      in: T === "day",
      children: /* @__PURE__ */ x.jsx(G$, {
        slots: d,
        slotProps: f,
        onGoToPrevious: A,
        isPreviousDisabled: te,
        previousLabel: l.previousMonth,
        onGoToNext: q,
        isNextDisabled: H,
        nextLabel: l.nextMonth
      })
    })]
  }));
});
process.env.NODE_ENV !== "production" && (Jh.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * className applied to the root element.
   */
  className: r.string,
  /**
   * Overridable components.
   * @default {}
   * @deprecated Please use `slots`.
   */
  components: r.object,
  /**
   * The props used for each component slot.
   * @default {}
   * @deprecated Please use `slotProps`.
   */
  componentsProps: r.object,
  currentMonth: r.any.isRequired,
  disabled: r.bool,
  disableFuture: r.bool,
  disablePast: r.bool,
  labelId: r.string,
  maxDate: r.any.isRequired,
  minDate: r.any.isRequired,
  onMonthChange: r.func.isRequired,
  onViewChange: r.func,
  reduceAnimations: r.bool.isRequired,
  /**
   * The props used for each component slot.
   * @default {}
   */
  slotProps: r.object,
  /**
   * Overridable component slots.
   * @default {}
   */
  slots: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  timezone: r.string.isRequired,
  view: r.oneOf(["day", "month", "year"]).isRequired,
  views: r.arrayOf(r.oneOf(["day", "month", "year"]).isRequired).isRequired
});
const NM = (e) => Pe("MuiDateCalendar", e);
Ce("MuiDateCalendar", ["root", "viewTransitionContainer"]);
const jM = ["autoFocus", "onViewChange", "value", "defaultValue", "referenceDate", "disableFuture", "disablePast", "defaultCalendarMonth", "onChange", "onYearChange", "onMonthChange", "reduceAnimations", "shouldDisableDate", "shouldDisableMonth", "shouldDisableYear", "view", "views", "openTo", "className", "disabled", "readOnly", "minDate", "maxDate", "disableHighlightToday", "focusedView", "onFocusedViewChange", "showDaysOutsideCurrentMonth", "fixedWeekNumber", "dayOfWeekFormatter", "components", "componentsProps", "slots", "slotProps", "loading", "renderLoading", "displayWeekNumber", "yearsPerRow", "monthsPerRow", "timezone"], AM = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"],
    viewTransitionContainer: ["viewTransitionContainer"]
  }, NM, t);
};
function FM(e, t) {
  var n, o, a, s, i, l, c;
  const u = Ze(), d = Ta(), f = Bh(), p = Ee({
    props: e,
    name: t
  });
  return b({}, p, {
    loading: (n = p.loading) != null ? n : !1,
    disablePast: (o = p.disablePast) != null ? o : !1,
    disableFuture: (a = p.disableFuture) != null ? a : !1,
    openTo: (s = p.openTo) != null ? s : "day",
    views: (i = p.views) != null ? i : ["year", "day"],
    reduceAnimations: (l = p.reduceAnimations) != null ? l : f,
    renderLoading: (c = p.renderLoading) != null ? c : () => /* @__PURE__ */ x.jsx("span", {
      children: "..."
    }),
    minDate: Gt(u, p.minDate, d.minDate),
    maxDate: Gt(u, p.maxDate, d.maxDate)
  });
}
const VM = Z(rk, {
  name: "MuiDateCalendar",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({
  display: "flex",
  flexDirection: "column",
  height: Mc
}), LM = Z(Gh, {
  name: "MuiDateCalendar",
  slot: "ViewTransitionContainer",
  overridesResolver: (e, t) => t.viewTransitionContainer
})({}), Qh = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s;
  const i = Ze(), l = Bn(), c = FM(t, "MuiDateCalendar"), {
    autoFocus: u,
    onViewChange: d,
    value: f,
    defaultValue: p,
    referenceDate: m,
    disableFuture: v,
    disablePast: h,
    defaultCalendarMonth: y,
    onChange: w,
    onYearChange: C,
    onMonthChange: E,
    reduceAnimations: O,
    shouldDisableDate: T,
    shouldDisableMonth: P,
    shouldDisableYear: S,
    view: j,
    views: $,
    openTo: V,
    className: _,
    disabled: L,
    readOnly: M,
    minDate: R,
    maxDate: D,
    disableHighlightToday: F,
    focusedView: z,
    onFocusedViewChange: N,
    showDaysOutsideCurrentMonth: q,
    fixedWeekNumber: A,
    dayOfWeekFormatter: H,
    components: te,
    componentsProps: re,
    slots: B,
    slotProps: G,
    loading: ee,
    renderLoading: W,
    displayWeekNumber: J,
    yearsPerRow: se,
    monthsPerRow: le,
    timezone: X
  } = c, U = ie(c, jM), {
    value: K,
    handleValueChange: Y,
    timezone: he
  } = Ac({
    name: "DateCalendar",
    timezone: X,
    value: f,
    defaultValue: p,
    onChange: w,
    valueManager: Cn
  }), {
    view: Oe,
    setView: Ne,
    focusedView: fe,
    setFocusedView: ve,
    goToNextView: oe,
    setValueAndGoToNextView: ce
  } = Sh({
    view: j,
    views: $,
    openTo: V,
    onChange: Y,
    onViewChange: d,
    autoFocus: u,
    focusedView: z,
    onFocusedViewChange: N
  }), {
    referenceDate: I,
    calendarState: Q,
    changeFocusedDay: ne,
    changeMonth: ue,
    handleChangeMonth: ge,
    isDateDisabled: ye,
    onMonthSwitchingAnimationEnd: xe
  } = K_({
    value: K,
    defaultCalendarMonth: y,
    referenceDate: m,
    reduceAnimations: O,
    onMonthChange: E,
    minDate: R,
    maxDate: D,
    shouldDisableDate: T,
    disablePast: h,
    disableFuture: v,
    timezone: he
  }), be = L && K || R, _e = L && K || D, st = `${l}-grid-label`, rt = fe !== null, Qe = (o = (a = B == null ? void 0 : B.calendarHeader) != null ? a : te == null ? void 0 : te.CalendarHeader) != null ? o : Jh, Te = Ye({
    elementType: Qe,
    externalSlotProps: (s = G == null ? void 0 : G.calendarHeader) != null ? s : re == null ? void 0 : re.calendarHeader,
    additionalProps: {
      views: $,
      view: Oe,
      currentMonth: Q.currentMonth,
      onViewChange: Ne,
      onMonthChange: (Ue, gt) => ge({
        newMonth: Ue,
        direction: gt
      }),
      minDate: be,
      maxDate: _e,
      disabled: L,
      disablePast: h,
      disableFuture: v,
      reduceAnimations: O,
      timezone: he,
      labelId: st,
      slots: B,
      slotProps: G
    },
    ownerState: c
  }), $e = we((Ue) => {
    const gt = i.startOfMonth(Ue), an = i.endOfMonth(Ue), Nt = ye(Ue) ? Bo({
      utils: i,
      date: Ue,
      minDate: i.isBefore(R, gt) ? gt : R,
      maxDate: i.isAfter(D, an) ? an : D,
      disablePast: h,
      disableFuture: v,
      isDateDisabled: ye,
      timezone: he
    }) : Ue;
    Nt ? (ce(Nt, "finish"), E == null || E(gt)) : (oe(), ue(gt)), ne(Nt, !0);
  }), Ge = we((Ue) => {
    const gt = i.startOfYear(Ue), an = i.endOfYear(Ue), Nt = ye(Ue) ? Bo({
      utils: i,
      date: Ue,
      minDate: i.isBefore(R, gt) ? gt : R,
      maxDate: i.isAfter(D, an) ? an : D,
      disablePast: h,
      disableFuture: v,
      isDateDisabled: ye,
      timezone: he
    }) : Ue;
    Nt ? (ce(Nt, "finish"), C == null || C(Nt)) : (oe(), ue(gt)), ne(Nt, !0);
  }), xt = we((Ue) => Y(Ue && ps(i, Ue, K ?? I), "finish", Oe));
  g.useEffect(() => {
    K != null && i.isValid(K) && ue(K);
  }, [K]);
  const Qt = c, Rn = AM(Qt), Dn = {
    disablePast: h,
    disableFuture: v,
    maxDate: D,
    minDate: R
  }, It = {
    disableHighlightToday: F,
    readOnly: M,
    disabled: L,
    timezone: he,
    gridLabelId: st
  }, on = g.useRef(Oe);
  g.useEffect(() => {
    on.current !== Oe && (fe === on.current && ve(Oe, !0), on.current = Oe);
  }, [fe, ve, Oe]);
  const $n = g.useMemo(() => [K], [K]);
  return /* @__PURE__ */ x.jsxs(VM, b({
    ref: n,
    className: pe(Rn.root, _),
    ownerState: Qt
  }, U, {
    children: [/* @__PURE__ */ x.jsx(Qe, b({}, Te)), /* @__PURE__ */ x.jsx(LM, {
      reduceAnimations: O,
      className: Rn.viewTransitionContainer,
      transKey: Oe,
      ownerState: Qt,
      children: /* @__PURE__ */ x.jsxs("div", {
        children: [Oe === "year" && /* @__PURE__ */ x.jsx(Zh, b({}, Dn, It, {
          value: K,
          onChange: Ge,
          shouldDisableYear: S,
          hasFocus: rt,
          onFocusedViewChange: (Ue) => ve("year", Ue),
          yearsPerRow: se,
          referenceDate: I
        })), Oe === "month" && /* @__PURE__ */ x.jsx(Xh, b({}, Dn, It, {
          hasFocus: rt,
          className: _,
          value: K,
          onChange: $e,
          shouldDisableMonth: P,
          onFocusedViewChange: (Ue) => ve("month", Ue),
          monthsPerRow: le,
          referenceDate: I
        })), Oe === "day" && /* @__PURE__ */ x.jsx(q_, b({}, Q, Dn, It, {
          onMonthSwitchingAnimationEnd: xe,
          onFocusedDayChange: ne,
          reduceAnimations: O,
          selectedDays: $n,
          onSelectedDaysChange: xt,
          shouldDisableDate: T,
          shouldDisableMonth: P,
          shouldDisableYear: S,
          hasFocus: rt,
          onFocusedViewChange: (Ue) => ve("day", Ue),
          showDaysOutsideCurrentMonth: q,
          fixedWeekNumber: A,
          dayOfWeekFormatter: H,
          displayWeekNumber: J,
          components: te,
          componentsProps: re,
          slots: B,
          slotProps: G,
          loading: ee,
          renderLoading: W
        }))]
      })
    })]
  }));
});
process.env.NODE_ENV !== "production" && (Qh.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * If `true`, the main element is focused during the first mount.
   * This main element is:
   * - the element chosen by the visible view if any (i.e: the selected day on the `day` view).
   * - the `input` element if there is a field rendered.
   */
  autoFocus: r.bool,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  className: r.string,
  /**
   * Overridable components.
   * @default {}
   * @deprecated Please use `slots`.
   */
  components: r.object,
  /**
   * The props used for each component slot.
   * @default {}
   * @deprecated Please use `slotProps`.
   */
  componentsProps: r.object,
  /**
   * Formats the day of week displayed in the calendar header.
   * @param {string} day The day of week provided by the adapter.  Deprecated, will be removed in v7: Use `date` instead.
   * @param {TDate} date The date of the day of week provided by the adapter.
   * @returns {string} The name to display.
   * @default (_day: string, date: TDate) => adapter.format(date, 'weekdayShort').charAt(0).toUpperCase()
   */
  dayOfWeekFormatter: r.func,
  /**
   * Default calendar month displayed when `value` and `defaultValue` are empty.
   * @deprecated Consider using `referenceDate` instead.
   */
  defaultCalendarMonth: r.any,
  /**
   * The default selected value.
   * Used when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the picker and text field are disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, disable values after the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disableFuture: r.bool,
  /**
   * If `true`, today's date is rendering without highlighting with circle.
   * @default false
   */
  disableHighlightToday: r.bool,
  /**
   * If `true`, disable values before the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disablePast: r.bool,
  /**
   * If `true`, the week number will be display in the calendar.
   */
  displayWeekNumber: r.bool,
  /**
   * Calendar will show more weeks in order to match this value.
   * Put it to 6 for having fix number of week in Gregorian calendars
   * @default undefined
   */
  fixedWeekNumber: r.number,
  /**
   * Controlled focused view.
   */
  focusedView: r.oneOf(["day", "month", "year"]),
  /**
   * If `true`, calls `renderLoading` instead of rendering the day calendar.
   * Can be used to preload information and show it in calendar.
   * @default false
   */
  loading: r.bool,
  /**
   * Maximal selectable date.
   */
  maxDate: r.any,
  /**
   * Minimal selectable date.
   */
  minDate: r.any,
  /**
   * Months rendered per row.
   * @default 3
   */
  monthsPerRow: r.oneOf([3, 4]),
  /**
   * Callback fired when the value changes.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TView The view type. Will be one of date or time views.
   * @param {TValue} value The new value.
   * @param {PickerSelectionState | undefined} selectionState Indicates if the date selection is complete.
   * @param {TView | undefined} selectedView Indicates the view in which the selection has been made.
   */
  onChange: r.func,
  /**
   * Callback fired on focused view change.
   * @template TView
   * @param {TView} view The new view to focus or not.
   * @param {boolean} hasFocus `true` if the view should be focused.
   */
  onFocusedViewChange: r.func,
  /**
   * Callback fired on month change.
   * @template TDate
   * @param {TDate} month The new month.
   */
  onMonthChange: r.func,
  /**
   * Callback fired on view change.
   * @template TView
   * @param {TView} view The new view.
   */
  onViewChange: r.func,
  /**
   * Callback fired on year change.
   * @template TDate
   * @param {TDate} year The new year.
   */
  onYearChange: r.func,
  /**
   * The default visible view.
   * Used when the component view is not controlled.
   * Must be a valid option from `views` list.
   */
  openTo: r.oneOf(["day", "month", "year"]),
  /**
   * Make picker read only.
   * @default false
   */
  readOnly: r.bool,
  /**
   * If `true`, disable heavy animations.
   * @default `@media(prefers-reduced-motion: reduce)` || `navigator.userAgent` matches Android <10 or iOS <13
   */
  reduceAnimations: r.bool,
  /**
   * The date used to generate the new value when both `value` and `defaultValue` are empty.
   * @default The closest valid date using the validation props, except callbacks such as `shouldDisableDate`.
   */
  referenceDate: r.any,
  /**
   * Component displaying when passed `loading` true.
   * @returns {React.ReactNode} The node to render when loading.
   * @default () => <span data-mui-test="loading-progress">...</span>
   */
  renderLoading: r.func,
  /**
   * Disable specific date.
   *
   * Warning: This function can be called multiple times (e.g. when rendering date calendar, checking if focus can be moved to a certain date, etc.). Expensive computations can impact performance.
   *
   * @template TDate
   * @param {TDate} day The date to test.
   * @returns {boolean} If `true` the date will be disabled.
   */
  shouldDisableDate: r.func,
  /**
   * Disable specific month.
   * @template TDate
   * @param {TDate} month The month to test.
   * @returns {boolean} If `true`, the month will be disabled.
   */
  shouldDisableMonth: r.func,
  /**
   * Disable specific year.
   * @template TDate
   * @param {TDate} year The year to test.
   * @returns {boolean} If `true`, the year will be disabled.
   */
  shouldDisableYear: r.func,
  /**
   * If `true`, days outside the current month are rendered:
   *
   * - if `fixedWeekNumber` is defined, renders days to have the weeks requested.
   *
   * - if `fixedWeekNumber` is not defined, renders day to fill the first and last week of the current month.
   *
   * - ignored if `calendars` equals more than `1` on range pickers.
   * @default false
   */
  showDaysOutsideCurrentMonth: r.bool,
  /**
   * The props used for each component slot.
   * @default {}
   */
  slotProps: r.object,
  /**
   * Overridable component slots.
   * @default {}
   */
  slots: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Choose which timezone to use for the value.
   * Example: "default", "system", "UTC", "America/New_York".
   * If you pass values from other timezones to some props, they will be converted to this timezone before being used.
   * @see See the {@link https://mui.com/x/react-date-pickers/timezone/ timezones documention} for more details.
   * @default The timezone of the `value` or `defaultValue` prop is defined, 'default' otherwise.
   */
  timezone: r.string,
  /**
   * The selected value.
   * Used when the component is controlled.
   */
  value: r.any,
  /**
   * The visible view.
   * Used when the component view is controlled.
   * Must be a valid option from `views` list.
   */
  view: r.oneOf(["day", "month", "year"]),
  /**
   * Available views.
   */
  views: r.arrayOf(r.oneOf(["day", "month", "year"]).isRequired),
  /**
   * Years rendered per row.
   * @default 3
   */
  yearsPerRow: r.oneOf([3, 4])
});
function BM(e) {
  return Pe("MuiDatePickerToolbar", e);
}
Ce("MuiDatePickerToolbar", ["root", "title"]);
const zM = ["value", "isLandscape", "onChange", "toolbarFormat", "toolbarPlaceholder", "views", "className"], WM = (e) => {
  const {
    classes: t
  } = e;
  return Se({
    root: ["root"],
    title: ["title"]
  }, BM, t);
}, UM = Z(i_, {
  name: "MuiDatePickerToolbar",
  slot: "Root",
  overridesResolver: (e, t) => t.root
})({}), HM = Z(Rt, {
  name: "MuiDatePickerToolbar",
  slot: "Title",
  overridesResolver: (e, t) => t.title
})(({
  ownerState: e
}) => b({}, e.isLandscape && {
  margin: "auto 16px auto auto"
})), eb = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiDatePickerToolbar"
  }), {
    value: a,
    isLandscape: s,
    toolbarFormat: i,
    toolbarPlaceholder: l = "––",
    views: c,
    className: u
  } = o, d = ie(o, zM), f = Ze(), p = Hn(), m = WM(o), v = g.useMemo(() => {
    if (!a)
      return l;
    const y = Nc(f, {
      format: i,
      views: c
    }, !0);
    return f.formatByString(a, y);
  }, [a, i, l, f, c]), h = o;
  return /* @__PURE__ */ x.jsx(UM, b({
    ref: n,
    toolbarTitle: p.datePickerToolbarTitle,
    isLandscape: s,
    className: pe(m.root, u)
  }, d, {
    children: /* @__PURE__ */ x.jsx(HM, {
      variant: "h4",
      align: s ? "left" : "center",
      ownerState: h,
      className: m.title,
      children: v
    })
  }));
});
process.env.NODE_ENV !== "production" && (eb.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * Override or extend the styles applied to the component.
   */
  classes: r.object,
  /**
   * className applied to the root component.
   */
  className: r.string,
  disabled: r.bool,
  /**
   * If `true`, show the toolbar even in desktop mode.
   * @default `true` for Desktop, `false` for Mobile.
   */
  hidden: r.bool,
  isLandscape: r.bool.isRequired,
  onChange: r.func.isRequired,
  /**
   * Callback called when a toolbar is clicked
   * @template TView
   * @param {TView} view The view to open
   */
  onViewChange: r.func.isRequired,
  readOnly: r.bool,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  titleId: r.string,
  /**
   * Toolbar date format.
   */
  toolbarFormat: r.string,
  /**
   * Toolbar value placeholder—it is displayed when the value is empty.
   * @default "––"
   */
  toolbarPlaceholder: r.node,
  value: r.any,
  /**
   * Currently visible picker view.
   */
  view: r.oneOf(["day", "month", "year"]).isRequired,
  views: r.arrayOf(r.oneOf(["day", "month", "year"]).isRequired).isRequired
});
function tb(e, t) {
  var n, o, a, s;
  const i = Ze(), l = Ta(), c = Ee({
    props: e,
    name: t
  }), u = g.useMemo(() => {
    var f;
    return ((f = c.localeText) == null ? void 0 : f.toolbarTitle) == null ? c.localeText : b({}, c.localeText, {
      datePickerToolbarTitle: c.localeText.toolbarTitle
    });
  }, [c.localeText]), d = (n = c.slots) != null ? n : Ih(c.components);
  return b({}, c, {
    localeText: u
  }, X$({
    views: c.views,
    openTo: c.openTo,
    defaultViews: ["year", "day"],
    defaultOpenTo: "day"
  }), {
    disableFuture: (o = c.disableFuture) != null ? o : !1,
    disablePast: (a = c.disablePast) != null ? a : !1,
    minDate: Gt(i, c.minDate, l.minDate),
    maxDate: Gt(i, c.maxDate, l.maxDate),
    slots: b({
      toolbar: eb
    }, d),
    slotProps: (s = c.slotProps) != null ? s : c.componentsProps
  });
}
const qM = ["props", "getOpenDialogAriaText"], YM = ["ownerState"], KM = ["ownerState"], GM = (e) => {
  var t, n, o, a, s;
  let {
    props: i,
    getOpenDialogAriaText: l
  } = e, c = ie(e, qM);
  const {
    slots: u,
    slotProps: d,
    className: f,
    sx: p,
    format: m,
    formatDensity: v,
    timezone: h,
    name: y,
    label: w,
    inputRef: C,
    readOnly: E,
    disabled: O,
    autoFocus: T,
    localeText: P,
    reduceAnimations: S
  } = i, j = Ze(), $ = g.useRef(null), V = g.useRef(null), _ = Bn(), L = (t = d == null || (n = d.toolbar) == null ? void 0 : n.hidden) != null ? t : !1, {
    open: M,
    actions: R,
    hasUIView: D,
    layoutProps: F,
    renderCurrentView: z,
    shouldRestoreFocus: N,
    fieldProps: q
  } = zh(b({}, c, {
    props: i,
    inputRef: $,
    autoFocusView: !0,
    additionalViewProps: {},
    wrapperVariant: "desktop"
  })), A = (o = u.inputAdornment) != null ? o : oc, H = Ye({
    elementType: A,
    externalSlotProps: d == null ? void 0 : d.inputAdornment,
    additionalProps: {
      position: "end"
    },
    ownerState: i
  }), te = ie(H, YM), re = (a = u.openPickerButton) != null ? a : pr, B = Ye({
    elementType: re,
    externalSlotProps: d == null ? void 0 : d.openPickerButton,
    additionalProps: {
      disabled: O || E,
      onClick: M ? R.onClose : R.onOpen,
      "aria-label": l(q.value, j),
      edge: te.position
    },
    ownerState: i
  }), G = ie(B, KM), ee = u.openPickerIcon, W = u.field, J = Ye({
    elementType: W,
    externalSlotProps: d == null ? void 0 : d.field,
    additionalProps: b({}, q, L && {
      id: _
    }, {
      readOnly: E,
      disabled: O,
      className: f,
      sx: p,
      format: m,
      formatDensity: v,
      timezone: h,
      label: w,
      name: y,
      autoFocus: T && !i.open,
      focused: M ? !0 : void 0
    }),
    ownerState: i
  });
  D && (J.InputProps = b({}, J.InputProps, {
    ref: V
  }, !i.disableOpenPicker && {
    [`${te.position}Adornment`]: /* @__PURE__ */ x.jsx(A, b({}, te, {
      children: /* @__PURE__ */ x.jsx(re, b({}, G, {
        children: /* @__PURE__ */ x.jsx(ee, b({}, d == null ? void 0 : d.openPickerIcon))
      }))
    }))
  }));
  const se = b({
    textField: u.textField,
    clearIcon: u.clearIcon,
    clearButton: u.clearButton
  }, J.slots), le = (s = u.layout) != null ? s : Lc, X = Ke($, J.inputRef, C);
  let U = _;
  L && (w ? U = `${_}-label` : U = void 0);
  const K = b({}, d, {
    toolbar: b({}, d == null ? void 0 : d.toolbar, {
      titleId: _
    }),
    popper: b({
      "aria-labelledby": U
    }, d == null ? void 0 : d.popper)
  });
  return {
    renderPicker: () => /* @__PURE__ */ x.jsxs(_c, {
      localeText: P,
      children: [/* @__PURE__ */ x.jsx(W, b({}, J, {
        slots: se,
        slotProps: K,
        inputRef: X
      })), /* @__PURE__ */ x.jsx(n_, b({
        role: "dialog",
        placement: "bottom-start",
        anchorEl: V.current
      }, R, {
        open: M,
        slots: u,
        slotProps: K,
        shouldRestoreFocus: N,
        reduceAnimations: S,
        children: /* @__PURE__ */ x.jsx(le, b({}, F, K == null ? void 0 : K.layout, {
          slots: u,
          slotProps: K,
          children: z()
        }))
      }))]
    })
  };
}, Yr = ({
  view: e,
  onViewChange: t,
  views: n,
  focusedView: o,
  onFocusedViewChange: a,
  value: s,
  defaultValue: i,
  referenceDate: l,
  onChange: c,
  className: u,
  classes: d,
  disableFuture: f,
  disablePast: p,
  minDate: m,
  maxDate: v,
  shouldDisableDate: h,
  shouldDisableMonth: y,
  shouldDisableYear: w,
  reduceAnimations: C,
  onMonthChange: E,
  monthsPerRow: O,
  onYearChange: T,
  yearsPerRow: P,
  defaultCalendarMonth: S,
  components: j,
  componentsProps: $,
  slots: V,
  slotProps: _,
  loading: L,
  renderLoading: M,
  disableHighlightToday: R,
  readOnly: D,
  disabled: F,
  showDaysOutsideCurrentMonth: z,
  dayOfWeekFormatter: N,
  sx: q,
  autoFocus: A,
  fixedWeekNumber: H,
  displayWeekNumber: te,
  timezone: re
}) => /* @__PURE__ */ x.jsx(Qh, {
  view: e,
  onViewChange: t,
  views: n.filter(mp),
  focusedView: o && mp(o) ? o : null,
  onFocusedViewChange: a,
  value: s,
  defaultValue: i,
  referenceDate: l,
  onChange: c,
  className: u,
  classes: d,
  disableFuture: f,
  disablePast: p,
  minDate: m,
  maxDate: v,
  shouldDisableDate: h,
  shouldDisableMonth: y,
  shouldDisableYear: w,
  reduceAnimations: C,
  onMonthChange: E,
  monthsPerRow: O,
  onYearChange: T,
  yearsPerRow: P,
  defaultCalendarMonth: S,
  components: j,
  componentsProps: $,
  slots: V,
  slotProps: _,
  loading: L,
  renderLoading: M,
  disableHighlightToday: R,
  readOnly: D,
  disabled: F,
  showDaysOutsideCurrentMonth: z,
  dayOfWeekFormatter: N,
  sx: q,
  autoFocus: A,
  fixedWeekNumber: H,
  displayWeekNumber: te,
  timezone: re
}), nb = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s, i;
  const l = Hn(), c = Ze(), u = tb(t, "MuiDesktopDatePicker"), d = b({
    day: Yr,
    month: Yr,
    year: Yr
  }, u.viewRenderers), f = b({}, u, {
    viewRenderers: d,
    format: Nc(c, u, !1),
    yearsPerRow: (o = u.yearsPerRow) != null ? o : 4,
    slots: b({
      openPickerIcon: L$,
      field: Bc
    }, u.slots),
    slotProps: b({}, u.slotProps, {
      field: (m) => {
        var v;
        return b({}, Bl((v = u.slotProps) == null ? void 0 : v.field, m), Lh(u), {
          ref: n
        });
      },
      toolbar: b({
        hidden: !0
      }, (a = u.slotProps) == null ? void 0 : a.toolbar)
    })
  }), {
    renderPicker: p
  } = GM({
    props: f,
    valueManager: Cn,
    valueType: "date",
    getOpenDialogAriaText: (s = (i = f.localeText) == null ? void 0 : i.openDatePickerDialogue) != null ? s : l.openDatePickerDialogue,
    validator: ri
  });
  return p();
});
nb.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * If `true`, the main element is focused during the first mount.
   * This main element is:
   * - the element chosen by the visible view if any (i.e: the selected day on the `day` view).
   * - the `input` element if there is a field rendered.
   */
  autoFocus: r.bool,
  /**
   * Class name applied to the root element.
   */
  className: r.string,
  /**
   * If `true`, the popover or modal will close after submitting the full date.
   * @default `true` for desktop, `false` for mobile (based on the chosen wrapper and `desktopModeMediaQuery` prop).
   */
  closeOnSelect: r.bool,
  /**
   * Overridable components.
   * @default {}
   * @deprecated Please use `slots`.
   */
  components: r.object,
  /**
   * The props used for each component slot.
   * @default {}
   * @deprecated Please use `slotProps`.
   */
  componentsProps: r.object,
  /**
   * Formats the day of week displayed in the calendar header.
   * @param {string} day The day of week provided by the adapter.  Deprecated, will be removed in v7: Use `date` instead.
   * @param {TDate} date The date of the day of week provided by the adapter.
   * @returns {string} The name to display.
   * @default (_day: string, date: TDate) => adapter.format(date, 'weekdayShort').charAt(0).toUpperCase()
   */
  dayOfWeekFormatter: r.func,
  /**
   * Default calendar month displayed when `value` and `defaultValue` are empty.
   * @deprecated Consider using `referenceDate` instead.
   */
  defaultCalendarMonth: r.any,
  /**
   * The default value.
   * Used when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the picker and text field are disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, disable values after the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disableFuture: r.bool,
  /**
   * If `true`, today's date is rendering without highlighting with circle.
   * @default false
   */
  disableHighlightToday: r.bool,
  /**
   * If `true`, the open picker button will not be rendered (renders only the field).
   * @default false
   */
  disableOpenPicker: r.bool,
  /**
   * If `true`, disable values before the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disablePast: r.bool,
  /**
   * If `true`, the week number will be display in the calendar.
   */
  displayWeekNumber: r.bool,
  /**
   * Calendar will show more weeks in order to match this value.
   * Put it to 6 for having fix number of week in Gregorian calendars
   * @default undefined
   */
  fixedWeekNumber: r.number,
  /**
   * Format of the date when rendered in the input(s).
   * Defaults to localized format based on the used `views`.
   */
  format: r.string,
  /**
   * Density of the format when rendered in the input.
   * Setting `formatDensity` to `"spacious"` will add a space before and after each `/`, `-` and `.` character.
   * @default "dense"
   */
  formatDensity: r.oneOf(["dense", "spacious"]),
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * The label content.
   */
  label: r.node,
  /**
   * If `true`, calls `renderLoading` instead of rendering the day calendar.
   * Can be used to preload information and show it in calendar.
   * @default false
   */
  loading: r.bool,
  /**
   * Locale for components texts.
   * Allows overriding texts coming from `LocalizationProvider` and `theme`.
   */
  localeText: r.object,
  /**
   * Maximal selectable date.
   */
  maxDate: r.any,
  /**
   * Minimal selectable date.
   */
  minDate: r.any,
  /**
   * Months rendered per row.
   * @default 3
   */
  monthsPerRow: r.oneOf([3, 4]),
  /**
   * Name attribute used by the `input` element in the Field.
   */
  name: r.string,
  /**
   * Callback fired when the value is accepted.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @param {TValue} value The value that was just accepted.
   */
  onAccept: r.func,
  /**
   * Callback fired when the value changes.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TError The validation error type. Will be either `string` or a `null`. Can be in `[start, end]` format in case of range value.
   * @param {TValue} value The new value.
   * @param {FieldChangeHandlerContext<TError>} context The context containing the validation result of the current value.
   */
  onChange: r.func,
  /**
   * Callback fired when the popup requests to be closed.
   * Use in controlled mode (see `open`).
   */
  onClose: r.func,
  /**
   * Callback fired when the error associated to the current value changes.
   * If the error has a non-null value, then the `TextField` will be rendered in `error` state.
   *
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TError The validation error type. Will be either `string` or a `null`. Can be in `[start, end]` format in case of range value.
   * @param {TError} error The new error describing why the current value is not valid.
   * @param {TValue} value The value associated to the error.
   */
  onError: r.func,
  /**
   * Callback fired on month change.
   * @template TDate
   * @param {TDate} month The new month.
   */
  onMonthChange: r.func,
  /**
   * Callback fired when the popup requests to be opened.
   * Use in controlled mode (see `open`).
   */
  onOpen: r.func,
  /**
   * Callback fired when the selected sections change.
   * @param {FieldSelectedSections} newValue The new selected sections.
   */
  onSelectedSectionsChange: r.func,
  /**
   * Callback fired on view change.
   * @template TView
   * @param {TView} view The new view.
   */
  onViewChange: r.func,
  /**
   * Callback fired on year change.
   * @template TDate
   * @param {TDate} year The new year.
   */
  onYearChange: r.func,
  /**
   * Control the popup or dialog open state.
   * @default false
   */
  open: r.bool,
  /**
   * The default visible view.
   * Used when the component view is not controlled.
   * Must be a valid option from `views` list.
   */
  openTo: r.oneOf(["day", "month", "year"]),
  /**
   * Force rendering in particular orientation.
   */
  orientation: r.oneOf(["landscape", "portrait"]),
  readOnly: r.bool,
  /**
   * If `true`, disable heavy animations.
   * @default `@media(prefers-reduced-motion: reduce)` || `navigator.userAgent` matches Android <10 or iOS <13
   */
  reduceAnimations: r.bool,
  /**
   * The date used to generate the new value when both `value` and `defaultValue` are empty.
   * @default The closest valid date-time using the validation props, except callbacks like `shouldDisable<...>`.
   */
  referenceDate: r.any,
  /**
   * Component displaying when passed `loading` true.
   * @returns {React.ReactNode} The node to render when loading.
   * @default () => <span data-mui-test="loading-progress">...</span>
   */
  renderLoading: r.func,
  /**
   * The currently selected sections.
   * This prop accept four formats:
   * 1. If a number is provided, the section at this index will be selected.
   * 2. If an object with a `startIndex` and `endIndex` properties are provided, the sections between those two indexes will be selected.
   * 3. If a string of type `FieldSectionType` is provided, the first section with that name will be selected.
   * 4. If `null` is provided, no section will be selected
   * If not provided, the selected sections will be handled internally.
   */
  selectedSections: r.oneOfType([r.oneOf(["all", "day", "hours", "meridiem", "minutes", "month", "seconds", "weekDay", "year"]), r.number, r.shape({
    endIndex: r.number.isRequired,
    startIndex: r.number.isRequired
  })]),
  /**
   * Disable specific date.
   *
   * Warning: This function can be called multiple times (e.g. when rendering date calendar, checking if focus can be moved to a certain date, etc.). Expensive computations can impact performance.
   *
   * @template TDate
   * @param {TDate} day The date to test.
   * @returns {boolean} If `true` the date will be disabled.
   */
  shouldDisableDate: r.func,
  /**
   * Disable specific month.
   * @template TDate
   * @param {TDate} month The month to test.
   * @returns {boolean} If `true`, the month will be disabled.
   */
  shouldDisableMonth: r.func,
  /**
   * Disable specific year.
   * @template TDate
   * @param {TDate} year The year to test.
   * @returns {boolean} If `true`, the year will be disabled.
   */
  shouldDisableYear: r.func,
  /**
   * If `true`, days outside the current month are rendered:
   *
   * - if `fixedWeekNumber` is defined, renders days to have the weeks requested.
   *
   * - if `fixedWeekNumber` is not defined, renders day to fill the first and last week of the current month.
   *
   * - ignored if `calendars` equals more than `1` on range pickers.
   * @default false
   */
  showDaysOutsideCurrentMonth: r.bool,
  /**
   * The props used for each component slot.
   * @default {}
   */
  slotProps: r.object,
  /**
   * Overridable component slots.
   * @default {}
   */
  slots: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Choose which timezone to use for the value.
   * Example: "default", "system", "UTC", "America/New_York".
   * If you pass values from other timezones to some props, they will be converted to this timezone before being used.
   * @see See the {@link https://mui.com/x/react-date-pickers/timezone/ timezones documention} for more details.
   * @default The timezone of the `value` or `defaultValue` prop is defined, 'default' otherwise.
   */
  timezone: r.string,
  /**
   * The selected value.
   * Used when the component is controlled.
   */
  value: r.any,
  /**
   * The visible view.
   * Used when the component view is controlled.
   * Must be a valid option from `views` list.
   */
  view: r.oneOf(["day", "month", "year"]),
  /**
   * Define custom view renderers for each section.
   * If `null`, the section will only have field editing.
   * If `undefined`, internally defined view will be the used.
   */
  viewRenderers: r.shape({
    day: r.func,
    month: r.func,
    year: r.func
  }),
  /**
   * Available views.
   */
  views: r.arrayOf(r.oneOf(["day", "month", "year"]).isRequired),
  /**
   * Years rendered per row.
   * @default 4
   */
  yearsPerRow: r.oneOf([3, 4])
};
const XM = ["props", "getOpenDialogAriaText"], ZM = (e) => {
  var t, n, o;
  let {
    props: a,
    getOpenDialogAriaText: s
  } = e, i = ie(e, XM);
  const {
    slots: l,
    slotProps: c,
    className: u,
    sx: d,
    format: f,
    formatDensity: p,
    timezone: m,
    name: v,
    label: h,
    inputRef: y,
    readOnly: w,
    disabled: C,
    localeText: E
  } = a, O = Ze(), T = g.useRef(null), P = Bn(), S = (t = c == null || (n = c.toolbar) == null ? void 0 : n.hidden) != null ? t : !1, {
    open: j,
    actions: $,
    layoutProps: V,
    renderCurrentView: _,
    fieldProps: L
  } = zh(b({}, i, {
    props: a,
    inputRef: T,
    autoFocusView: !0,
    additionalViewProps: {},
    wrapperVariant: "mobile"
  })), M = l.field, R = Ye({
    elementType: M,
    externalSlotProps: c == null ? void 0 : c.field,
    additionalProps: b({}, L, S && {
      id: P
    }, !(C || w) && {
      onClick: $.onOpen,
      onKeyDown: _k($.onOpen)
    }, {
      readOnly: w ?? !0,
      disabled: C,
      className: u,
      sx: d,
      format: f,
      formatDensity: p,
      timezone: m,
      label: h,
      name: v
    }),
    ownerState: a
  });
  R.inputProps = b({}, R.inputProps, {
    "aria-label": s(L.value, O)
  });
  const D = b({
    textField: l.textField
  }, R.slots), F = (o = l.layout) != null ? o : Lc, z = Ke(T, R.inputRef, y);
  let N = P;
  S && (h ? N = `${P}-label` : N = void 0);
  const q = b({}, c, {
    toolbar: b({}, c == null ? void 0 : c.toolbar, {
      titleId: P
    }),
    mobilePaper: b({
      "aria-labelledby": N
    }, c == null ? void 0 : c.mobilePaper)
  });
  return {
    renderPicker: () => /* @__PURE__ */ x.jsxs(_c, {
      localeText: E,
      children: [/* @__PURE__ */ x.jsx(M, b({}, R, {
        slots: D,
        slotProps: q,
        inputRef: z
      })), /* @__PURE__ */ x.jsx(Hk, b({}, $, {
        open: j,
        slots: l,
        slotProps: q,
        children: /* @__PURE__ */ x.jsx(F, b({}, V, q == null ? void 0 : q.layout, {
          slots: l,
          slotProps: q,
          children: _()
        }))
      }))]
    })
  };
}, rb = /* @__PURE__ */ g.forwardRef(function(t, n) {
  var o, a, s;
  const i = Hn(), l = Ze(), c = tb(t, "MuiMobileDatePicker"), u = b({
    day: Yr,
    month: Yr,
    year: Yr
  }, c.viewRenderers), d = b({}, c, {
    viewRenderers: u,
    format: Nc(l, c, !1),
    slots: b({
      field: Bc
    }, c.slots),
    slotProps: b({}, c.slotProps, {
      field: (p) => {
        var m;
        return b({}, Bl((m = c.slotProps) == null ? void 0 : m.field, p), Lh(c), {
          ref: n
        });
      },
      toolbar: b({
        hidden: !1
      }, (o = c.slotProps) == null ? void 0 : o.toolbar)
    })
  }), {
    renderPicker: f
  } = ZM({
    props: d,
    valueManager: Cn,
    valueType: "date",
    getOpenDialogAriaText: (a = (s = d.localeText) == null ? void 0 : s.openDatePickerDialogue) != null ? a : i.openDatePickerDialogue,
    validator: ri
  });
  return f();
});
rb.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * If `true`, the main element is focused during the first mount.
   * This main element is:
   * - the element chosen by the visible view if any (i.e: the selected day on the `day` view).
   * - the `input` element if there is a field rendered.
   */
  autoFocus: r.bool,
  /**
   * Class name applied to the root element.
   */
  className: r.string,
  /**
   * If `true`, the popover or modal will close after submitting the full date.
   * @default `true` for desktop, `false` for mobile (based on the chosen wrapper and `desktopModeMediaQuery` prop).
   */
  closeOnSelect: r.bool,
  /**
   * Overridable components.
   * @default {}
   * @deprecated Please use `slots`.
   */
  components: r.object,
  /**
   * The props used for each component slot.
   * @default {}
   * @deprecated Please use `slotProps`.
   */
  componentsProps: r.object,
  /**
   * Formats the day of week displayed in the calendar header.
   * @param {string} day The day of week provided by the adapter.  Deprecated, will be removed in v7: Use `date` instead.
   * @param {TDate} date The date of the day of week provided by the adapter.
   * @returns {string} The name to display.
   * @default (_day: string, date: TDate) => adapter.format(date, 'weekdayShort').charAt(0).toUpperCase()
   */
  dayOfWeekFormatter: r.func,
  /**
   * Default calendar month displayed when `value` and `defaultValue` are empty.
   * @deprecated Consider using `referenceDate` instead.
   */
  defaultCalendarMonth: r.any,
  /**
   * The default value.
   * Used when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * If `true`, the picker and text field are disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, disable values after the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disableFuture: r.bool,
  /**
   * If `true`, today's date is rendering without highlighting with circle.
   * @default false
   */
  disableHighlightToday: r.bool,
  /**
   * If `true`, the open picker button will not be rendered (renders only the field).
   * @default false
   */
  disableOpenPicker: r.bool,
  /**
   * If `true`, disable values before the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disablePast: r.bool,
  /**
   * If `true`, the week number will be display in the calendar.
   */
  displayWeekNumber: r.bool,
  /**
   * Calendar will show more weeks in order to match this value.
   * Put it to 6 for having fix number of week in Gregorian calendars
   * @default undefined
   */
  fixedWeekNumber: r.number,
  /**
   * Format of the date when rendered in the input(s).
   * Defaults to localized format based on the used `views`.
   */
  format: r.string,
  /**
   * Density of the format when rendered in the input.
   * Setting `formatDensity` to `"spacious"` will add a space before and after each `/`, `-` and `.` character.
   * @default "dense"
   */
  formatDensity: r.oneOf(["dense", "spacious"]),
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * The label content.
   */
  label: r.node,
  /**
   * If `true`, calls `renderLoading` instead of rendering the day calendar.
   * Can be used to preload information and show it in calendar.
   * @default false
   */
  loading: r.bool,
  /**
   * Locale for components texts.
   * Allows overriding texts coming from `LocalizationProvider` and `theme`.
   */
  localeText: r.object,
  /**
   * Maximal selectable date.
   */
  maxDate: r.any,
  /**
   * Minimal selectable date.
   */
  minDate: r.any,
  /**
   * Months rendered per row.
   * @default 3
   */
  monthsPerRow: r.oneOf([3, 4]),
  /**
   * Name attribute used by the `input` element in the Field.
   */
  name: r.string,
  /**
   * Callback fired when the value is accepted.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @param {TValue} value The value that was just accepted.
   */
  onAccept: r.func,
  /**
   * Callback fired when the value changes.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TError The validation error type. Will be either `string` or a `null`. Can be in `[start, end]` format in case of range value.
   * @param {TValue} value The new value.
   * @param {FieldChangeHandlerContext<TError>} context The context containing the validation result of the current value.
   */
  onChange: r.func,
  /**
   * Callback fired when the popup requests to be closed.
   * Use in controlled mode (see `open`).
   */
  onClose: r.func,
  /**
   * Callback fired when the error associated to the current value changes.
   * If the error has a non-null value, then the `TextField` will be rendered in `error` state.
   *
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TError The validation error type. Will be either `string` or a `null`. Can be in `[start, end]` format in case of range value.
   * @param {TError} error The new error describing why the current value is not valid.
   * @param {TValue} value The value associated to the error.
   */
  onError: r.func,
  /**
   * Callback fired on month change.
   * @template TDate
   * @param {TDate} month The new month.
   */
  onMonthChange: r.func,
  /**
   * Callback fired when the popup requests to be opened.
   * Use in controlled mode (see `open`).
   */
  onOpen: r.func,
  /**
   * Callback fired when the selected sections change.
   * @param {FieldSelectedSections} newValue The new selected sections.
   */
  onSelectedSectionsChange: r.func,
  /**
   * Callback fired on view change.
   * @template TView
   * @param {TView} view The new view.
   */
  onViewChange: r.func,
  /**
   * Callback fired on year change.
   * @template TDate
   * @param {TDate} year The new year.
   */
  onYearChange: r.func,
  /**
   * Control the popup or dialog open state.
   * @default false
   */
  open: r.bool,
  /**
   * The default visible view.
   * Used when the component view is not controlled.
   * Must be a valid option from `views` list.
   */
  openTo: r.oneOf(["day", "month", "year"]),
  /**
   * Force rendering in particular orientation.
   */
  orientation: r.oneOf(["landscape", "portrait"]),
  readOnly: r.bool,
  /**
   * If `true`, disable heavy animations.
   * @default `@media(prefers-reduced-motion: reduce)` || `navigator.userAgent` matches Android <10 or iOS <13
   */
  reduceAnimations: r.bool,
  /**
   * The date used to generate the new value when both `value` and `defaultValue` are empty.
   * @default The closest valid date-time using the validation props, except callbacks like `shouldDisable<...>`.
   */
  referenceDate: r.any,
  /**
   * Component displaying when passed `loading` true.
   * @returns {React.ReactNode} The node to render when loading.
   * @default () => <span data-mui-test="loading-progress">...</span>
   */
  renderLoading: r.func,
  /**
   * The currently selected sections.
   * This prop accept four formats:
   * 1. If a number is provided, the section at this index will be selected.
   * 2. If an object with a `startIndex` and `endIndex` properties are provided, the sections between those two indexes will be selected.
   * 3. If a string of type `FieldSectionType` is provided, the first section with that name will be selected.
   * 4. If `null` is provided, no section will be selected
   * If not provided, the selected sections will be handled internally.
   */
  selectedSections: r.oneOfType([r.oneOf(["all", "day", "hours", "meridiem", "minutes", "month", "seconds", "weekDay", "year"]), r.number, r.shape({
    endIndex: r.number.isRequired,
    startIndex: r.number.isRequired
  })]),
  /**
   * Disable specific date.
   *
   * Warning: This function can be called multiple times (e.g. when rendering date calendar, checking if focus can be moved to a certain date, etc.). Expensive computations can impact performance.
   *
   * @template TDate
   * @param {TDate} day The date to test.
   * @returns {boolean} If `true` the date will be disabled.
   */
  shouldDisableDate: r.func,
  /**
   * Disable specific month.
   * @template TDate
   * @param {TDate} month The month to test.
   * @returns {boolean} If `true`, the month will be disabled.
   */
  shouldDisableMonth: r.func,
  /**
   * Disable specific year.
   * @template TDate
   * @param {TDate} year The year to test.
   * @returns {boolean} If `true`, the year will be disabled.
   */
  shouldDisableYear: r.func,
  /**
   * If `true`, days outside the current month are rendered:
   *
   * - if `fixedWeekNumber` is defined, renders days to have the weeks requested.
   *
   * - if `fixedWeekNumber` is not defined, renders day to fill the first and last week of the current month.
   *
   * - ignored if `calendars` equals more than `1` on range pickers.
   * @default false
   */
  showDaysOutsideCurrentMonth: r.bool,
  /**
   * The props used for each component slot.
   * @default {}
   */
  slotProps: r.object,
  /**
   * Overridable component slots.
   * @default {}
   */
  slots: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Choose which timezone to use for the value.
   * Example: "default", "system", "UTC", "America/New_York".
   * If you pass values from other timezones to some props, they will be converted to this timezone before being used.
   * @see See the {@link https://mui.com/x/react-date-pickers/timezone/ timezones documention} for more details.
   * @default The timezone of the `value` or `defaultValue` prop is defined, 'default' otherwise.
   */
  timezone: r.string,
  /**
   * The selected value.
   * Used when the component is controlled.
   */
  value: r.any,
  /**
   * The visible view.
   * Used when the component view is controlled.
   * Must be a valid option from `views` list.
   */
  view: r.oneOf(["day", "month", "year"]),
  /**
   * Define custom view renderers for each section.
   * If `null`, the section will only have field editing.
   * If `undefined`, internally defined view will be the used.
   */
  viewRenderers: r.shape({
    day: r.func,
    month: r.func,
    year: r.func
  }),
  /**
   * Available views.
   */
  views: r.arrayOf(r.oneOf(["day", "month", "year"]).isRequired),
  /**
   * Years rendered per row.
   * @default 3
   */
  yearsPerRow: r.oneOf([3, 4])
};
const JM = ["desktopModeMediaQuery"], fl = /* @__PURE__ */ g.forwardRef(function(t, n) {
  const o = Ee({
    props: t,
    name: "MuiDatePicker"
  }), {
    desktopModeMediaQuery: a = Mk
  } = o, s = ie(o, JM);
  return Df(a, {
    defaultMatches: !0
  }) ? /* @__PURE__ */ x.jsx(nb, b({
    ref: n
  }, s)) : /* @__PURE__ */ x.jsx(rb, b({
    ref: n
  }, s));
});
process.env.NODE_ENV !== "production" && (fl.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  /**
   * If `true`, the main element is focused during the first mount.
   * This main element is:
   * - the element chosen by the visible view if any (i.e: the selected day on the `day` view).
   * - the `input` element if there is a field rendered.
   */
  autoFocus: r.bool,
  /**
   * Class name applied to the root element.
   */
  className: r.string,
  /**
   * If `true`, the popover or modal will close after submitting the full date.
   * @default `true` for desktop, `false` for mobile (based on the chosen wrapper and `desktopModeMediaQuery` prop).
   */
  closeOnSelect: r.bool,
  /**
   * Overridable components.
   * @default {}
   * @deprecated Please use `slots`.
   */
  components: r.object,
  /**
   * The props used for each component slot.
   * @default {}
   * @deprecated Please use `slotProps`.
   */
  componentsProps: r.object,
  /**
   * Formats the day of week displayed in the calendar header.
   * @param {string} day The day of week provided by the adapter.  Deprecated, will be removed in v7: Use `date` instead.
   * @param {TDate} date The date of the day of week provided by the adapter.
   * @returns {string} The name to display.
   * @default (_day: string, date: TDate) => adapter.format(date, 'weekdayShort').charAt(0).toUpperCase()
   */
  dayOfWeekFormatter: r.func,
  /**
   * Default calendar month displayed when `value` and `defaultValue` are empty.
   * @deprecated Consider using `referenceDate` instead.
   */
  defaultCalendarMonth: r.any,
  /**
   * The default value.
   * Used when the component is not controlled.
   */
  defaultValue: r.any,
  /**
   * CSS media query when `Mobile` mode will be changed to `Desktop`.
   * @default '@media (pointer: fine)'
   * @example '@media (min-width: 720px)' or theme.breakpoints.up("sm")
   */
  desktopModeMediaQuery: r.string,
  /**
   * If `true`, the picker and text field are disabled.
   * @default false
   */
  disabled: r.bool,
  /**
   * If `true`, disable values after the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disableFuture: r.bool,
  /**
   * If `true`, today's date is rendering without highlighting with circle.
   * @default false
   */
  disableHighlightToday: r.bool,
  /**
   * If `true`, the open picker button will not be rendered (renders only the field).
   * @default false
   */
  disableOpenPicker: r.bool,
  /**
   * If `true`, disable values before the current date for date components, time for time components and both for date time components.
   * @default false
   */
  disablePast: r.bool,
  /**
   * If `true`, the week number will be display in the calendar.
   */
  displayWeekNumber: r.bool,
  /**
   * Calendar will show more weeks in order to match this value.
   * Put it to 6 for having fix number of week in Gregorian calendars
   * @default undefined
   */
  fixedWeekNumber: r.number,
  /**
   * Format of the date when rendered in the input(s).
   * Defaults to localized format based on the used `views`.
   */
  format: r.string,
  /**
   * Density of the format when rendered in the input.
   * Setting `formatDensity` to `"spacious"` will add a space before and after each `/`, `-` and `.` character.
   * @default "dense"
   */
  formatDensity: r.oneOf(["dense", "spacious"]),
  /**
   * Pass a ref to the `input` element.
   */
  inputRef: vt,
  /**
   * The label content.
   */
  label: r.node,
  /**
   * If `true`, calls `renderLoading` instead of rendering the day calendar.
   * Can be used to preload information and show it in calendar.
   * @default false
   */
  loading: r.bool,
  /**
   * Locale for components texts.
   * Allows overriding texts coming from `LocalizationProvider` and `theme`.
   */
  localeText: r.object,
  /**
   * Maximal selectable date.
   */
  maxDate: r.any,
  /**
   * Minimal selectable date.
   */
  minDate: r.any,
  /**
   * Months rendered per row.
   * @default 3
   */
  monthsPerRow: r.oneOf([3, 4]),
  /**
   * Name attribute used by the `input` element in the Field.
   */
  name: r.string,
  /**
   * Callback fired when the value is accepted.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @param {TValue} value The value that was just accepted.
   */
  onAccept: r.func,
  /**
   * Callback fired when the value changes.
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TError The validation error type. Will be either `string` or a `null`. Can be in `[start, end]` format in case of range value.
   * @param {TValue} value The new value.
   * @param {FieldChangeHandlerContext<TError>} context The context containing the validation result of the current value.
   */
  onChange: r.func,
  /**
   * Callback fired when the popup requests to be closed.
   * Use in controlled mode (see `open`).
   */
  onClose: r.func,
  /**
   * Callback fired when the error associated to the current value changes.
   * If the error has a non-null value, then the `TextField` will be rendered in `error` state.
   *
   * @template TValue The value type. Will be either the same type as `value` or `null`. Can be in `[start, end]` format in case of range value.
   * @template TError The validation error type. Will be either `string` or a `null`. Can be in `[start, end]` format in case of range value.
   * @param {TError} error The new error describing why the current value is not valid.
   * @param {TValue} value The value associated to the error.
   */
  onError: r.func,
  /**
   * Callback fired on month change.
   * @template TDate
   * @param {TDate} month The new month.
   */
  onMonthChange: r.func,
  /**
   * Callback fired when the popup requests to be opened.
   * Use in controlled mode (see `open`).
   */
  onOpen: r.func,
  /**
   * Callback fired when the selected sections change.
   * @param {FieldSelectedSections} newValue The new selected sections.
   */
  onSelectedSectionsChange: r.func,
  /**
   * Callback fired on view change.
   * @template TView
   * @param {TView} view The new view.
   */
  onViewChange: r.func,
  /**
   * Callback fired on year change.
   * @template TDate
   * @param {TDate} year The new year.
   */
  onYearChange: r.func,
  /**
   * Control the popup or dialog open state.
   * @default false
   */
  open: r.bool,
  /**
   * The default visible view.
   * Used when the component view is not controlled.
   * Must be a valid option from `views` list.
   */
  openTo: r.oneOf(["day", "month", "year"]),
  /**
   * Force rendering in particular orientation.
   */
  orientation: r.oneOf(["landscape", "portrait"]),
  readOnly: r.bool,
  /**
   * If `true`, disable heavy animations.
   * @default `@media(prefers-reduced-motion: reduce)` || `navigator.userAgent` matches Android <10 or iOS <13
   */
  reduceAnimations: r.bool,
  /**
   * The date used to generate the new value when both `value` and `defaultValue` are empty.
   * @default The closest valid date-time using the validation props, except callbacks like `shouldDisable<...>`.
   */
  referenceDate: r.any,
  /**
   * Component displaying when passed `loading` true.
   * @returns {React.ReactNode} The node to render when loading.
   * @default () => <span data-mui-test="loading-progress">...</span>
   */
  renderLoading: r.func,
  /**
   * The currently selected sections.
   * This prop accept four formats:
   * 1. If a number is provided, the section at this index will be selected.
   * 2. If an object with a `startIndex` and `endIndex` properties are provided, the sections between those two indexes will be selected.
   * 3. If a string of type `FieldSectionType` is provided, the first section with that name will be selected.
   * 4. If `null` is provided, no section will be selected
   * If not provided, the selected sections will be handled internally.
   */
  selectedSections: r.oneOfType([r.oneOf(["all", "day", "hours", "meridiem", "minutes", "month", "seconds", "weekDay", "year"]), r.number, r.shape({
    endIndex: r.number.isRequired,
    startIndex: r.number.isRequired
  })]),
  /**
   * Disable specific date.
   *
   * Warning: This function can be called multiple times (e.g. when rendering date calendar, checking if focus can be moved to a certain date, etc.). Expensive computations can impact performance.
   *
   * @template TDate
   * @param {TDate} day The date to test.
   * @returns {boolean} If `true` the date will be disabled.
   */
  shouldDisableDate: r.func,
  /**
   * Disable specific month.
   * @template TDate
   * @param {TDate} month The month to test.
   * @returns {boolean} If `true`, the month will be disabled.
   */
  shouldDisableMonth: r.func,
  /**
   * Disable specific year.
   * @template TDate
   * @param {TDate} year The year to test.
   * @returns {boolean} If `true`, the year will be disabled.
   */
  shouldDisableYear: r.func,
  /**
   * If `true`, days outside the current month are rendered:
   *
   * - if `fixedWeekNumber` is defined, renders days to have the weeks requested.
   *
   * - if `fixedWeekNumber` is not defined, renders day to fill the first and last week of the current month.
   *
   * - ignored if `calendars` equals more than `1` on range pickers.
   * @default false
   */
  showDaysOutsideCurrentMonth: r.bool,
  /**
   * The props used for each component slot.
   * @default {}
   */
  slotProps: r.object,
  /**
   * Overridable component slots.
   * @default {}
   */
  slots: r.object,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: r.oneOfType([r.arrayOf(r.oneOfType([r.func, r.object, r.bool])), r.func, r.object]),
  /**
   * Choose which timezone to use for the value.
   * Example: "default", "system", "UTC", "America/New_York".
   * If you pass values from other timezones to some props, they will be converted to this timezone before being used.
   * @see See the {@link https://mui.com/x/react-date-pickers/timezone/ timezones documention} for more details.
   * @default The timezone of the `value` or `defaultValue` prop is defined, 'default' otherwise.
   */
  timezone: r.string,
  /**
   * The selected value.
   * Used when the component is controlled.
   */
  value: r.any,
  /**
   * The visible view.
   * Used when the component view is controlled.
   * Must be a valid option from `views` list.
   */
  view: r.oneOf(["day", "month", "year"]),
  /**
   * Define custom view renderers for each section.
   * If `null`, the section will only have field editing.
   * If `undefined`, internally defined view will be the used.
   */
  viewRenderers: r.shape({
    day: r.func,
    month: r.func,
    year: r.func
  }),
  /**
   * Available views.
   */
  views: r.arrayOf(r.oneOf(["day", "month", "year"]).isRequired),
  /**
   * Years rendered per row.
   * @default 4 on desktop, 3 on mobile
   */
  yearsPerRow: r.oneOf([3, 4])
});
var zc = {}, QM = Ln;
Object.defineProperty(zc, "__esModule", {
  value: !0
});
var ob = zc.default = void 0, eI = QM(po()), tI = x;
ob = zc.default = (0, eI.default)(/* @__PURE__ */ (0, tI.jsx)("path", {
  d: "M9 16h6v-6h4l-7-7-7 7h4zm-4 2h14v2H5z"
}), "FileUpload");
const Cp = (e) => ({
  "& .MuiInputBase-root, MuiOutlinedInput-root": {
    height: "40px",
    "&: hover .MuiOutlinedInput-notchedOutline": {
      borderColor: e.palette.success.main
    },
    "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
      border: `1px solid ${e.palette.success.main}`
    }
  }
}), oI = ({ config: e, showSnackbar: t }) => {
  const [n, o] = gn([]), [a, s] = gn(), [i, l] = gn([]), [c, u] = gn(Rm), [d, f] = gn(!1), [p, m] = gn(!1), [v, h] = gn(!1), y = bn(null), w = Zt(), C = hr(), E = () => {
    s(void 0), f(!1);
  }, O = () => {
    s(void 0), m(!1);
  };
  Bt(
    () => {
      YO(e).then((R) => Hr(R, e.setLoginRequired)).then((R) => R.json()).then((R) => {
        o(R);
      }).catch((R) => {
        vn(R, e.setTechnicalError);
      });
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [v]
  );
  const T = (R) => {
    s(R), f(!0);
  }, P = () => {
    f(!0);
  }, S = (R) => {
    s(R), m(!0);
  }, j = nt((R) => {
    u((D) => ({
      ...D,
      [R.target.name]: R.target.value
    }));
  }, []), $ = nt((R, D) => {
    u((F) => ({
      ...F,
      [R]: D instanceof Date ? D.toISOString() : D
    }));
  }, []), V = nt((R) => {
    u((D) => ({
      ...D,
      [R]: null
    }));
  }, []);
  Bt(() => {
    let R = !1;
    l([]);
    const D = (n == null ? void 0 : n.map((z) => z.id)) || [];
    return (async () => {
      const z = [];
      for (const N of D)
        try {
          const q = await Dm(N, e);
          await Hr(q, e.setLoginRequired);
          const A = await q.json();
          z.push(A);
        } catch (q) {
          vn(q, e.setTechnicalError);
        }
      R || l(z);
    })(), () => {
      R = !0;
    };
  }, [e, e.setLoginRequired, e.setTechnicalError, n]);
  const _ = () => {
    Ch(i);
  }, L = () => {
    y.current && y.current.click();
  }, M = (R) => {
    const D = R.target.files[0], F = async (N) => {
      var A;
      const q = (A = N.target) == null ? void 0 : A.result;
      if (typeof q == "string")
        try {
          const H = JSON.parse(q);
          if (Array.isArray(H))
            t && t("JSON needs to contain an object, not an array.", "error");
          else {
            const te = (n == null ? void 0 : n.map((B) => B.id)) || [];
            delete H._id, delete H._rev;
            const re = te.includes(H.name) ? GO(H, e) : Qi(H, e);
            try {
              const B = await re;
              await Hr(B, e.setLoginRequired), await B.json(), t && t(`Uploaded ${te.includes(H.name) ? "an existing" : "a new"} form successfully.`, "success"), h((G) => !G);
            } catch (B) {
              t && t(`Error while uploading ${te.includes(H.name) ? "an existing" : "a new"} form: ${B}`, "error"), vn(B, e.setTechnicalError);
            }
          }
        } catch (H) {
          t && t(`Error parsing JSON: ${H.message}`, "error");
        }
    }, z = () => {
      t && t("Error reading file.", "error");
    };
    if (D) {
      const N = new FileReader();
      N.onload = F, N.onerror = z, N.readAsText(D);
    }
    y.current && (y.current.value = "");
  };
  return /* @__PURE__ */ x.jsx(tn, { pt: 6, children: n ? /* @__PURE__ */ x.jsxs(tn, { sx: { padding: "0 50px" }, children: [
    /* @__PURE__ */ x.jsxs(tn, { sx: { display: "flex", justifyContent: "space-between" }, children: [
      /* @__PURE__ */ x.jsx(Rt, { sx: { mb: 4 }, variant: "h2", children: /* @__PURE__ */ x.jsx(bt, { id: "adminUI.dialog.heading" }) }),
      /* @__PURE__ */ x.jsxs(tn, { children: [
        /* @__PURE__ */ x.jsx(Nn, { title: C.formatMessage({ id: "upload" }), placement: "top-end", arrow: !0, children: /* @__PURE__ */ x.jsx(UO, { onClick: L, children: /* @__PURE__ */ x.jsx(rr, { fontSize: "small", children: /* @__PURE__ */ x.jsx(ob, {}) }) }) }),
        /* @__PURE__ */ x.jsx(
          "input",
          {
            ref: y,
            type: "file",
            accept: ".json",
            hidden: !0,
            onChange: (R) => M(R)
          }
        )
      ] })
    ] }),
    /* @__PURE__ */ x.jsx(Cm, { children: /* @__PURE__ */ x.jsxs(Tm, { size: "small", sx: { borderCollapse: "separate", borderSpacing: "2px 2px" }, children: [
      /* @__PURE__ */ x.jsx(Om, { children: /* @__PURE__ */ x.jsxs(dc, { children: [
        /* @__PURE__ */ x.jsx(tt, { width: "3%", sx: { border: "none", textAlign: "center" }, children: /* @__PURE__ */ x.jsx(Nn, { title: C.formatMessage({ id: "adminUI.table.tooltip.add" }), placement: "top-end", arrow: !0, children: /* @__PURE__ */ x.jsx(
          Fr,
          {
            onClick: function(R) {
              R.preventDefault(), P();
            },
            children: /* @__PURE__ */ x.jsx(rr, { fontSize: "medium", children: /* @__PURE__ */ x.jsx(Sf, {}) })
          }
        ) }) }),
        /* @__PURE__ */ x.jsx(tt, { width: "3%", sx: { border: "none" } }),
        /* @__PURE__ */ x.jsx(tt, { width: "25%", sx: { border: "none" }, children: /* @__PURE__ */ x.jsx(bt, { id: "adminUI.formConfiguration.label" }) }),
        /* @__PURE__ */ x.jsx(tt, { width: "25%", sx: { border: "none" }, children: /* @__PURE__ */ x.jsx(bt, { id: "adminUI.formConfiguration.latestTagName" }) }),
        /* @__PURE__ */ x.jsx(tt, { width: "19%", sx: { border: "none" }, children: /* @__PURE__ */ x.jsx(bt, { id: "adminUI.formConfiguration.latestTagDate" }) }),
        /* @__PURE__ */ x.jsx(tt, { width: "19%", sx: { border: "none" }, children: /* @__PURE__ */ x.jsx(bt, { id: "adminUI.formConfiguration.lastSaved" }) }),
        /* @__PURE__ */ x.jsx(tt, { width: "3%", sx: { border: "none" } }),
        /* @__PURE__ */ x.jsx(tt, { width: "3%", sx: { border: "none", textAlign: "center" }, children: /* @__PURE__ */ x.jsx(Nn, { title: C.formatMessage({ id: "download.all" }), placement: "top-end", arrow: !0, children: /* @__PURE__ */ x.jsx(
          Fr,
          {
            onClick: function(R) {
              R.preventDefault(), _();
            },
            children: /* @__PURE__ */ x.jsx(rr, { fontSize: "small", children: /* @__PURE__ */ x.jsx(kc, {}) })
          }
        ) }) })
      ] }) }),
      /* @__PURE__ */ x.jsxs(wm, { children: [
        /* @__PURE__ */ x.jsxs(Sm, { children: [
          /* @__PURE__ */ x.jsx(tt, {}),
          /* @__PURE__ */ x.jsx(tt, {}),
          /* @__PURE__ */ x.jsx(tt, { children: /* @__PURE__ */ x.jsx(
            Pd,
            {
              name: "label",
              onChange: j,
              value: c.label
            }
          ) }),
          /* @__PURE__ */ x.jsx(tt, { children: /* @__PURE__ */ x.jsx(
            Pd,
            {
              name: "latestTagName",
              onChange: j,
              value: c.latestTagName
            }
          ) }),
          /* @__PURE__ */ x.jsx(tt, { children: /* @__PURE__ */ x.jsx(
            fl,
            {
              value: c.latestTagDate,
              onChange: (R) => $("latestTagDate", R),
              sx: Cp(w),
              slotProps: {
                field: {
                  clearable: !0,
                  onClear: () => V("latestTagDate")
                }
              }
            }
          ) }),
          /* @__PURE__ */ x.jsx(tt, { children: /* @__PURE__ */ x.jsx(
            fl,
            {
              value: c.lastSaved,
              onChange: (R) => $("lastSaved", R),
              sx: Cp(w),
              slotProps: {
                field: {
                  clearable: !0,
                  onClear: () => V("lastSaved")
                }
              }
            }
          ) }),
          /* @__PURE__ */ x.jsx(tt, {}),
          /* @__PURE__ */ x.jsx(tt, {})
        ] }),
        n.map(
          (R) => /* @__PURE__ */ x.jsx(
            M$,
            {
              filters: c,
              formConfiguration: R,
              deleteFormConfiguration: S,
              copyFormConfiguration: T,
              dialobForm: i.find((D) => D.name === R.id),
              config: e
            }
          )
        )
      ] })
    ] }) }),
    /* @__PURE__ */ x.jsx(
      g$,
      {
        createModalOpen: d,
        handleCreateModalClose: E,
        setFetchAgain: h,
        formConfiguration: a,
        config: e
      }
    ),
    /* @__PURE__ */ x.jsx(
      y$,
      {
        deleteModalOpen: p,
        handleDeleteModalClose: O,
        setFetchAgain: h,
        formConfiguration: a,
        config: e
      }
    )
  ] }) : /* @__PURE__ */ x.jsx(WO, {}) });
};
export {
  oI as DialobAdminView
};
