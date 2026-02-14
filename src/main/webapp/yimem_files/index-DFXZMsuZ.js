(function () {
    const t = document.createElement("link").relList;
    if (t && t.supports && t.supports("modulepreload")) return;
    for (const r of document.querySelectorAll('link[rel="modulepreload"]')) n(r);
    new MutationObserver(r => {
        for (const o of r) if (o.type === "childList") for (const i of o.addedNodes) i.tagName === "LINK" && i.rel === "modulepreload" && n(i)
    }).observe(document, {childList: !0, subtree: !0});

    function s(r) {
        const o = {};
        return r.integrity && (o.integrity = r.integrity), r.referrerPolicy && (o.referrerPolicy = r.referrerPolicy), r.crossOrigin === "use-credentials" ? o.credentials = "include" : r.crossOrigin === "anonymous" ? o.credentials = "omit" : o.credentials = "same-origin", o
    }

    function n(r) {
        if (r.ep) return;
        r.ep = !0;
        const o = s(r);
        fetch(r.href, o)
    }
})();

/**
 * @vue/shared v3.5.21
 * (c) 2018-present Yuxi (Evan) You and Vue contributors
 * @license MIT
 **/function gn(e) {
    const t = Object.create(null);
    for (const s of e.split(",")) t[s] = 1;
    return s => s in t
}

const Y = {}, Ot = [], Be = () => {
    }, Or = () => !1,
    As = e => e.charCodeAt(0) === 111 && e.charCodeAt(1) === 110 && (e.charCodeAt(2) > 122 || e.charCodeAt(2) < 97),
    mn = e => e.startsWith("onUpdate:"), ue = Object.assign, xn = (e, t) => {
        const s = e.indexOf(t);
        s > -1 && e.splice(s, 1)
    }, Zo = Object.prototype.hasOwnProperty, z = (e, t) => Zo.call(e, t), H = Array.isArray,
    Tt = e => Os(e) === "[object Map]", Tr = e => Os(e) === "[object Set]", D = e => typeof e == "function",
    ne = e => typeof e == "string", dt = e => typeof e == "symbol", ee = e => e !== null && typeof e == "object",
    Mr = e => (ee(e) || D(e)) && D(e.then) && D(e.catch), jr = Object.prototype.toString, Os = e => jr.call(e),
    ei = e => Os(e).slice(8, -1), $r = e => Os(e) === "[object Object]",
    vn = e => ne(e) && e !== "NaN" && e[0] !== "-" && "" + parseInt(e, 10) === e,
    Wt = gn(",key,ref,ref_for,ref_key,onVnodeBeforeMount,onVnodeMounted,onVnodeBeforeUpdate,onVnodeUpdated,onVnodeBeforeUnmount,onVnodeUnmounted"),
    Ts = e => {
        const t = Object.create(null);
        return s => t[s] || (t[s] = e(s))
    }, ti = /-\w/g, Ae = Ts(e => e.replace(ti, t => t.slice(1).toUpperCase())), si = /\B([A-Z])/g,
    wt = Ts(e => e.replace(si, "-$1").toLowerCase()), Ms = Ts(e => e.charAt(0).toUpperCase() + e.slice(1)),
    Vs = Ts(e => e ? `on${Ms(e)}` : ""), ct = (e, t) => !Object.is(e, t), Ks = (e, ...t) => {
        for (let s = 0; s < e.length; s++) e[s](...t)
    }, Ir = (e, t, s, n = !1) => {
        Object.defineProperty(e, t, {configurable: !0, enumerable: !1, writable: n, value: s})
    }, ni = e => {
        const t = parseFloat(e);
        return isNaN(t) ? e : t
    };
let Nn;
const js = () => Nn || (Nn = typeof globalThis < "u" ? globalThis : typeof self < "u" ? self : typeof window < "u" ? window : typeof global < "u" ? global : {});

function bn(e) {
    if (H(e)) {
        const t = {};
        for (let s = 0; s < e.length; s++) {
            const n = e[s], r = ne(n) ? li(n) : bn(n);
            if (r) for (const o in r) t[o] = r[o]
        }
        return t
    } else if (ne(e) || ee(e)) return e
}

const ri = /;(?![^(]*\))/g, oi = /:([^]+)/, ii = /\/\*[^]*?\*\//g;

function li(e) {
    const t = {};
    return e.replace(ii, "").split(ri).forEach(s => {
        if (s) {
            const n = s.split(oi);
            n.length > 1 && (t[n[0].trim()] = n[1].trim())
        }
    }), t
}

function _t(e) {
    let t = "";
    if (ne(e)) t = e; else if (H(e)) for (let s = 0; s < e.length; s++) {
        const n = _t(e[s]);
        n && (t += n + " ")
    } else if (ee(e)) for (const s in e) e[s] && (t += s + " ");
    return t.trim()
}

const ci = "itemscope,allowfullscreen,formnovalidate,ismap,nomodule,novalidate,readonly", ai = gn(ci);

function kr(e) {
    return !!e || e === ""
}

const Lr = e => !!(e && e.__v_isRef === !0),
    we = e => ne(e) ? e : e == null ? "" : H(e) || ee(e) && (e.toString === jr || !D(e.toString)) ? Lr(e) ? we(e.value) : JSON.stringify(e, Fr, 2) : String(e),
    Fr = (e, t) => Lr(t) ? Fr(e, t.value) : Tt(t) ? {[`Map(${t.size})`]: [...t.entries()].reduce((s, [n, r], o) => (s[Us(n, o) + " =>"] = r, s), {})} : Tr(t) ? {[`Set(${t.size})`]: [...t.values()].map(s => Us(s))} : dt(t) ? Us(t) : ee(t) && !H(t) && !$r(t) ? String(t) : t,
    Us = (e, t = "") => {
        var s;
        return dt(e) ? `Symbol(${(s = e.description) != null ? s : t})` : e
    };
/**
 * @vue/reactivity v3.5.21
 * (c) 2018-present Yuxi (Evan) You and Vue contributors
 * @license MIT
 **/let ve;

class Nr {
    constructor(t = !1) {
        this.detached = t, this._active = !0, this._on = 0, this.effects = [], this.cleanups = [], this._isPaused = !1, this.parent = ve, !t && ve && (this.index = (ve.scopes || (ve.scopes = [])).push(this) - 1)
    }

    get active() {
        return this._active
    }

    pause() {
        if (this._active) {
            this._isPaused = !0;
            let t, s;
            if (this.scopes) for (t = 0, s = this.scopes.length; t < s; t++) this.scopes[t].pause();
            for (t = 0, s = this.effects.length; t < s; t++) this.effects[t].pause()
        }
    }

    resume() {
        if (this._active && this._isPaused) {
            this._isPaused = !1;
            let t, s;
            if (this.scopes) for (t = 0, s = this.scopes.length; t < s; t++) this.scopes[t].resume();
            for (t = 0, s = this.effects.length; t < s; t++) this.effects[t].resume()
        }
    }

    run(t) {
        if (this._active) {
            const s = ve;
            try {
                return ve = this, t()
            } finally {
                ve = s
            }
        }
    }

    on() {
        ++this._on === 1 && (this.prevScope = ve, ve = this)
    }

    off() {
        this._on > 0 && --this._on === 0 && (ve = this.prevScope, this.prevScope = void 0)
    }

    stop(t) {
        if (this._active) {
            this._active = !1;
            let s, n;
            for (s = 0, n = this.effects.length; s < n; s++) this.effects[s].stop();
            for (this.effects.length = 0, s = 0, n = this.cleanups.length; s < n; s++) this.cleanups[s]();
            if (this.cleanups.length = 0, this.scopes) {
                for (s = 0, n = this.scopes.length; s < n; s++) this.scopes[s].stop(!0);
                this.scopes.length = 0
            }
            if (!this.detached && this.parent && !t) {
                const r = this.parent.scopes.pop();
                r && r !== this && (this.parent.scopes[this.index] = r, r.index = this.index)
            }
            this.parent = void 0
        }
    }
}

function fi(e) {
    return new Nr(e)
}

function ui() {
    return ve
}

let Z;
const Ws = new WeakSet;

class Hr {
    constructor(t) {
        this.fn = t, this.deps = void 0, this.depsTail = void 0, this.flags = 5, this.next = void 0, this.cleanup = void 0, this.scheduler = void 0, ve && ve.active && ve.effects.push(this)
    }

    pause() {
        this.flags |= 64
    }

    resume() {
        this.flags & 64 && (this.flags &= -65, Ws.has(this) && (Ws.delete(this), this.trigger()))
    }

    notify() {
        this.flags & 2 && !(this.flags & 32) || this.flags & 8 || Br(this)
    }

    run() {
        if (!(this.flags & 1)) return this.fn();
        this.flags |= 2, Hn(this), Vr(this);
        const t = Z, s = Oe;
        Z = this, Oe = !0;
        try {
            return this.fn()
        } finally {
            Kr(this), Z = t, Oe = s, this.flags &= -3
        }
    }

    stop() {
        if (this.flags & 1) {
            for (let t = this.deps; t; t = t.nextDep) wn(t);
            this.deps = this.depsTail = void 0, Hn(this), this.onStop && this.onStop(), this.flags &= -2
        }
    }

    trigger() {
        this.flags & 64 ? Ws.add(this) : this.scheduler ? this.scheduler() : this.runIfDirty()
    }

    runIfDirty() {
        tn(this) && this.run()
    }

    get dirty() {
        return tn(this)
    }
}

let Dr = 0, zt, qt;

function Br(e, t = !1) {
    if (e.flags |= 8, t) {
        e.next = qt, qt = e;
        return
    }
    e.next = zt, zt = e
}

function yn() {
    Dr++
}

function _n() {
    if (--Dr > 0) return;
    if (qt) {
        let t = qt;
        for (qt = void 0; t;) {
            const s = t.next;
            t.next = void 0, t.flags &= -9, t = s
        }
    }
    let e;
    for (; zt;) {
        let t = zt;
        for (zt = void 0; t;) {
            const s = t.next;
            if (t.next = void 0, t.flags &= -9, t.flags & 1) try {
                t.trigger()
            } catch (n) {
                e || (e = n)
            }
            t = s
        }
    }
    if (e) throw e
}

function Vr(e) {
    for (let t = e.deps; t; t = t.nextDep) t.version = -1, t.prevActiveLink = t.dep.activeLink, t.dep.activeLink = t
}

function Kr(e) {
    let t, s = e.depsTail, n = s;
    for (; n;) {
        const r = n.prevDep;
        n.version === -1 ? (n === s && (s = r), wn(n), di(n)) : t = n, n.dep.activeLink = n.prevActiveLink, n.prevActiveLink = void 0, n = r
    }
    e.deps = t, e.depsTail = s
}

function tn(e) {
    for (let t = e.deps; t; t = t.nextDep) if (t.dep.version !== t.version || t.dep.computed && (Ur(t.dep.computed) || t.dep.version !== t.version)) return !0;
    return !!e._dirty
}

function Ur(e) {
    if (e.flags & 4 && !(e.flags & 16) || (e.flags &= -17, e.globalVersion === es) || (e.globalVersion = es, !e.isSSR && e.flags & 128 && (!e.deps && !e._dirty || !tn(e)))) return;
    e.flags |= 2;
    const t = e.dep, s = Z, n = Oe;
    Z = e, Oe = !0;
    try {
        Vr(e);
        const r = e.fn(e._value);
        (t.version === 0 || ct(r, e._value)) && (e.flags |= 128, e._value = r, t.version++)
    } catch (r) {
        throw t.version++, r
    } finally {
        Z = s, Oe = n, Kr(e), e.flags &= -3
    }
}

function wn(e, t = !1) {
    const {dep: s, prevSub: n, nextSub: r} = e;
    if (n && (n.nextSub = r, e.prevSub = void 0), r && (r.prevSub = n, e.nextSub = void 0), s.subs === e && (s.subs = n, !n && s.computed)) {
        s.computed.flags &= -5;
        for (let o = s.computed.deps; o; o = o.nextDep) wn(o, !0)
    }
    !t && !--s.sc && s.map && s.map.delete(s.key)
}

function di(e) {
    const {prevDep: t, nextDep: s} = e;
    t && (t.nextDep = s, e.prevDep = void 0), s && (s.prevDep = t, e.nextDep = void 0)
}

let Oe = !0;
const Wr = [];

function Qe() {
    Wr.push(Oe), Oe = !1
}

function Ye() {
    const e = Wr.pop();
    Oe = e === void 0 ? !0 : e
}

function Hn(e) {
    const {cleanup: t} = e;
    if (e.cleanup = void 0, t) {
        const s = Z;
        Z = void 0;
        try {
            t()
        } finally {
            Z = s
        }
    }
}

let es = 0;

class pi {
    constructor(t, s) {
        this.sub = t, this.dep = s, this.version = s.version, this.nextDep = this.prevDep = this.nextSub = this.prevSub = this.prevActiveLink = void 0
    }
}

class Sn {
    constructor(t) {
        this.computed = t, this.version = 0, this.activeLink = void 0, this.subs = void 0, this.map = void 0, this.key = void 0, this.sc = 0, this.__v_skip = !0
    }

    track(t) {
        if (!Z || !Oe || Z === this.computed) return;
        let s = this.activeLink;
        if (s === void 0 || s.sub !== Z) s = this.activeLink = new pi(Z, this), Z.deps ? (s.prevDep = Z.depsTail, Z.depsTail.nextDep = s, Z.depsTail = s) : Z.deps = Z.depsTail = s, zr(s); else if (s.version === -1 && (s.version = this.version, s.nextDep)) {
            const n = s.nextDep;
            n.prevDep = s.prevDep, s.prevDep && (s.prevDep.nextDep = n), s.prevDep = Z.depsTail, s.nextDep = void 0, Z.depsTail.nextDep = s, Z.depsTail = s, Z.deps === s && (Z.deps = n)
        }
        return s
    }

    trigger(t) {
        this.version++, es++, this.notify(t)
    }

    notify(t) {
        yn();
        try {
            for (let s = this.subs; s; s = s.prevSub) s.sub.notify() && s.sub.dep.notify()
        } finally {
            _n()
        }
    }
}

function zr(e) {
    if (e.dep.sc++, e.sub.flags & 4) {
        const t = e.dep.computed;
        if (t && !e.dep.subs) {
            t.flags |= 20;
            for (let n = t.deps; n; n = n.nextDep) zr(n)
        }
        const s = e.dep.subs;
        s !== e && (e.prevSub = s, s && (s.nextSub = e)), e.dep.subs = e
    }
}

const sn = new WeakMap, vt = Symbol(""), nn = Symbol(""), ts = Symbol("");

function le(e, t, s) {
    if (Oe && Z) {
        let n = sn.get(e);
        n || sn.set(e, n = new Map);
        let r = n.get(s);
        r || (n.set(s, r = new Sn), r.map = n, r.key = s), r.track()
    }
}

function qe(e, t, s, n, r, o) {
    const i = sn.get(e);
    if (!i) {
        es++;
        return
    }
    const l = c => {
        c && c.trigger()
    };
    if (yn(), t === "clear") i.forEach(l); else {
        const c = H(e), d = c && vn(s);
        if (c && s === "length") {
            const f = Number(n);
            i.forEach((p, m) => {
                (m === "length" || m === ts || !dt(m) && m >= f) && l(p)
            })
        } else switch ((s !== void 0 || i.has(void 0)) && l(i.get(s)), d && l(i.get(ts)), t) {
            case"add":
                c ? d && l(i.get("length")) : (l(i.get(vt)), Tt(e) && l(i.get(nn)));
                break;
            case"delete":
                c || (l(i.get(vt)), Tt(e) && l(i.get(nn)));
                break;
            case"set":
                Tt(e) && l(i.get(vt));
                break
        }
    }
    _n()
}

function Rt(e) {
    const t = W(e);
    return t === e ? t : (le(t, "iterate", ts), Pe(e) ? t : t.map(ie))
}

function $s(e) {
    return le(e = W(e), "iterate", ts), e
}

const hi = {
    __proto__: null, [Symbol.iterator]() {
        return zs(this, Symbol.iterator, ie)
    }, concat(...e) {
        return Rt(this).concat(...e.map(t => H(t) ? Rt(t) : t))
    }, entries() {
        return zs(this, "entries", e => (e[1] = ie(e[1]), e))
    }, every(e, t) {
        return Ue(this, "every", e, t, void 0, arguments)
    }, filter(e, t) {
        return Ue(this, "filter", e, t, s => s.map(ie), arguments)
    }, find(e, t) {
        return Ue(this, "find", e, t, ie, arguments)
    }, findIndex(e, t) {
        return Ue(this, "findIndex", e, t, void 0, arguments)
    }, findLast(e, t) {
        return Ue(this, "findLast", e, t, ie, arguments)
    }, findLastIndex(e, t) {
        return Ue(this, "findLastIndex", e, t, void 0, arguments)
    }, forEach(e, t) {
        return Ue(this, "forEach", e, t, void 0, arguments)
    }, includes(...e) {
        return qs(this, "includes", e)
    }, indexOf(...e) {
        return qs(this, "indexOf", e)
    }, join(e) {
        return Rt(this).join(e)
    }, lastIndexOf(...e) {
        return qs(this, "lastIndexOf", e)
    }, map(e, t) {
        return Ue(this, "map", e, t, void 0, arguments)
    }, pop() {
        return Dt(this, "pop")
    }, push(...e) {
        return Dt(this, "push", e)
    }, reduce(e, ...t) {
        return Dn(this, "reduce", e, t)
    }, reduceRight(e, ...t) {
        return Dn(this, "reduceRight", e, t)
    }, shift() {
        return Dt(this, "shift")
    }, some(e, t) {
        return Ue(this, "some", e, t, void 0, arguments)
    }, splice(...e) {
        return Dt(this, "splice", e)
    }, toReversed() {
        return Rt(this).toReversed()
    }, toSorted(e) {
        return Rt(this).toSorted(e)
    }, toSpliced(...e) {
        return Rt(this).toSpliced(...e)
    }, unshift(...e) {
        return Dt(this, "unshift", e)
    }, values() {
        return zs(this, "values", ie)
    }
};

function zs(e, t, s) {
    const n = $s(e), r = n[t]();
    return n !== e && !Pe(e) && (r._next = r.next, r.next = () => {
        const o = r._next();
        return o.value && (o.value = s(o.value)), o
    }), r
}

const gi = Array.prototype;

function Ue(e, t, s, n, r, o) {
    const i = $s(e), l = i !== e && !Pe(e), c = i[t];
    if (c !== gi[t]) {
        const p = c.apply(e, o);
        return l ? ie(p) : p
    }
    let d = s;
    i !== e && (l ? d = function (p, m) {
        return s.call(this, ie(p), m, e)
    } : s.length > 2 && (d = function (p, m) {
        return s.call(this, p, m, e)
    }));
    const f = c.call(i, d, n);
    return l && r ? r(f) : f
}

function Dn(e, t, s, n) {
    const r = $s(e);
    let o = s;
    return r !== e && (Pe(e) ? s.length > 3 && (o = function (i, l, c) {
        return s.call(this, i, l, c, e)
    }) : o = function (i, l, c) {
        return s.call(this, i, ie(l), c, e)
    }), r[t](o, ...n)
}

function qs(e, t, s) {
    const n = W(e);
    le(n, "iterate", ts);
    const r = n[t](...s);
    return (r === -1 || r === !1) && Rn(s[0]) ? (s[0] = W(s[0]), n[t](...s)) : r
}

function Dt(e, t, s = []) {
    Qe(), yn();
    const n = W(e)[t].apply(e, s);
    return _n(), Ye(), n
}

const mi = gn("__proto__,__v_isRef,__isVue"),
    qr = new Set(Object.getOwnPropertyNames(Symbol).filter(e => e !== "arguments" && e !== "caller").map(e => Symbol[e]).filter(dt));

function xi(e) {
    dt(e) || (e = String(e));
    const t = W(this);
    return le(t, "has", e), t.hasOwnProperty(e)
}

class Gr {
    constructor(t = !1, s = !1) {
        this._isReadonly = t, this._isShallow = s
    }

    get(t, s, n) {
        if (s === "__v_skip") return t.__v_skip;
        const r = this._isReadonly, o = this._isShallow;
        if (s === "__v_isReactive") return !r;
        if (s === "__v_isReadonly") return r;
        if (s === "__v_isShallow") return o;
        if (s === "__v_raw") return n === (r ? o ? Pi : Xr : o ? Yr : Qr).get(t) || Object.getPrototypeOf(t) === Object.getPrototypeOf(n) ? t : void 0;
        const i = H(t);
        if (!r) {
            let c;
            if (i && (c = hi[s])) return c;
            if (s === "hasOwnProperty") return xi
        }
        const l = Reflect.get(t, s, fe(t) ? t : n);
        return (dt(s) ? qr.has(s) : mi(s)) || (r || le(t, "get", s), o) ? l : fe(l) ? i && vn(s) ? l : l.value : ee(l) ? r ? eo(l) : Is(l) : l
    }
}

class Jr extends Gr {
    constructor(t = !1) {
        super(!1, t)
    }

    set(t, s, n, r) {
        let o = t[s];
        if (!this._isShallow) {
            const c = ft(o);
            if (!Pe(n) && !ft(n) && (o = W(o), n = W(n)), !H(t) && fe(o) && !fe(n)) return c || (o.value = n), !0
        }
        const i = H(t) && vn(s) ? Number(s) < t.length : z(t, s), l = Reflect.set(t, s, n, fe(t) ? t : r);
        return t === W(r) && (i ? ct(n, o) && qe(t, "set", s, n) : qe(t, "add", s, n)), l
    }

    deleteProperty(t, s) {
        const n = z(t, s);
        t[s];
        const r = Reflect.deleteProperty(t, s);
        return r && n && qe(t, "delete", s, void 0), r
    }

    has(t, s) {
        const n = Reflect.has(t, s);
        return (!dt(s) || !qr.has(s)) && le(t, "has", s), n
    }

    ownKeys(t) {
        return le(t, "iterate", H(t) ? "length" : vt), Reflect.ownKeys(t)
    }
}

class vi extends Gr {
    constructor(t = !1) {
        super(!0, t)
    }

    set(t, s) {
        return !0
    }

    deleteProperty(t, s) {
        return !0
    }
}

const bi = new Jr, yi = new vi, _i = new Jr(!0);
const rn = e => e, fs = e => Reflect.getPrototypeOf(e);

function wi(e, t, s) {
    return function (...n) {
        const r = this.__v_raw, o = W(r), i = Tt(o), l = e === "entries" || e === Symbol.iterator && i,
            c = e === "keys" && i, d = r[e](...n), f = s ? rn : t ? vs : ie;
        return !t && le(o, "iterate", c ? nn : vt), {
            next() {
                const {value: p, done: m} = d.next();
                return m ? {value: p, done: m} : {value: l ? [f(p[0]), f(p[1])] : f(p), done: m}
            }, [Symbol.iterator]() {
                return this
            }
        }
    }
}

function us(e) {
    return function (...t) {
        return e === "delete" ? !1 : e === "clear" ? void 0 : this
    }
}

function Si(e, t) {
    const s = {
        get(r) {
            const o = this.__v_raw, i = W(o), l = W(r);
            e || (ct(r, l) && le(i, "get", r), le(i, "get", l));
            const {has: c} = fs(i), d = t ? rn : e ? vs : ie;
            if (c.call(i, r)) return d(o.get(r));
            if (c.call(i, l)) return d(o.get(l));
            o !== i && o.get(r)
        }, get size() {
            const r = this.__v_raw;
            return !e && le(W(r), "iterate", vt), r.size
        }, has(r) {
            const o = this.__v_raw, i = W(o), l = W(r);
            return e || (ct(r, l) && le(i, "has", r), le(i, "has", l)), r === l ? o.has(r) : o.has(r) || o.has(l)
        }, forEach(r, o) {
            const i = this, l = i.__v_raw, c = W(l), d = t ? rn : e ? vs : ie;
            return !e && le(c, "iterate", vt), l.forEach((f, p) => r.call(o, d(f), d(p), i))
        }
    };
    return ue(s, e ? {add: us("add"), set: us("set"), delete: us("delete"), clear: us("clear")} : {
        add(r) {
            !t && !Pe(r) && !ft(r) && (r = W(r));
            const o = W(this);
            return fs(o).has.call(o, r) || (o.add(r), qe(o, "add", r, r)), this
        }, set(r, o) {
            !t && !Pe(o) && !ft(o) && (o = W(o));
            const i = W(this), {has: l, get: c} = fs(i);
            let d = l.call(i, r);
            d || (r = W(r), d = l.call(i, r));
            const f = c.call(i, r);
            return i.set(r, o), d ? ct(o, f) && qe(i, "set", r, o) : qe(i, "add", r, o), this
        }, delete(r) {
            const o = W(this), {has: i, get: l} = fs(o);
            let c = i.call(o, r);
            c || (r = W(r), c = i.call(o, r)), l && l.call(o, r);
            const d = o.delete(r);
            return c && qe(o, "delete", r, void 0), d
        }, clear() {
            const r = W(this), o = r.size !== 0, i = r.clear();
            return o && qe(r, "clear", void 0, void 0), i
        }
    }), ["keys", "values", "entries", Symbol.iterator].forEach(r => {
        s[r] = wi(r, e, t)
    }), s
}

function En(e, t) {
    const s = Si(e, t);
    return (n, r, o) => r === "__v_isReactive" ? !e : r === "__v_isReadonly" ? e : r === "__v_raw" ? n : Reflect.get(z(s, r) && r in n ? s : n, r, o)
}

const Ei = {get: En(!1, !1)}, Ci = {get: En(!1, !0)}, Ri = {get: En(!0, !1)};
const Qr = new WeakMap, Yr = new WeakMap, Xr = new WeakMap, Pi = new WeakMap;

function Ai(e) {
    switch (e) {
        case"Object":
        case"Array":
            return 1;
        case"Map":
        case"Set":
        case"WeakMap":
        case"WeakSet":
            return 2;
        default:
            return 0
    }
}

function Oi(e) {
    return e.__v_skip || !Object.isExtensible(e) ? 0 : Ai(ei(e))
}

function Is(e) {
    return ft(e) ? e : Cn(e, !1, bi, Ei, Qr)
}

function Zr(e) {
    return Cn(e, !1, _i, Ci, Yr)
}

function eo(e) {
    return Cn(e, !0, yi, Ri, Xr)
}

function Cn(e, t, s, n, r) {
    if (!ee(e) || e.__v_raw && !(t && e.__v_isReactive)) return e;
    const o = Oi(e);
    if (o === 0) return e;
    const i = r.get(e);
    if (i) return i;
    const l = new Proxy(e, o === 2 ? n : s);
    return r.set(e, l), l
}

function Mt(e) {
    return ft(e) ? Mt(e.__v_raw) : !!(e && e.__v_isReactive)
}

function ft(e) {
    return !!(e && e.__v_isReadonly)
}

function Pe(e) {
    return !!(e && e.__v_isShallow)
}

function Rn(e) {
    return e ? !!e.__v_raw : !1
}

function W(e) {
    const t = e && e.__v_raw;
    return t ? W(t) : e
}

function to(e) {
    return !z(e, "__v_skip") && Object.isExtensible(e) && Ir(e, "__v_skip", !0), e
}

const ie = e => ee(e) ? Is(e) : e, vs = e => ee(e) ? eo(e) : e;

function fe(e) {
    return e ? e.__v_isRef === !0 : !1
}

function it(e) {
    return so(e, !1)
}

function Ti(e) {
    return so(e, !0)
}

function so(e, t) {
    return fe(e) ? e : new Mi(e, t)
}

class Mi {
    constructor(t, s) {
        this.dep = new Sn, this.__v_isRef = !0, this.__v_isShallow = !1, this._rawValue = s ? t : W(t), this._value = s ? t : ie(t), this.__v_isShallow = s
    }

    get value() {
        return this.dep.track(), this._value
    }

    set value(t) {
        const s = this._rawValue, n = this.__v_isShallow || Pe(t) || ft(t);
        t = n ? t : W(t), ct(t, s) && (this._rawValue = t, this._value = n ? t : ie(t), this.dep.trigger())
    }
}

function jt(e) {
    return fe(e) ? e.value : e
}

const ji = {
    get: (e, t, s) => t === "__v_raw" ? e : jt(Reflect.get(e, t, s)), set: (e, t, s, n) => {
        const r = e[t];
        return fe(r) && !fe(s) ? (r.value = s, !0) : Reflect.set(e, t, s, n)
    }
};

function no(e) {
    return Mt(e) ? e : new Proxy(e, ji)
}

class $i {
    constructor(t, s, n) {
        this.fn = t, this.setter = s, this._value = void 0, this.dep = new Sn(this), this.__v_isRef = !0, this.deps = void 0, this.depsTail = void 0, this.flags = 16, this.globalVersion = es - 1, this.next = void 0, this.effect = this, this.__v_isReadonly = !s, this.isSSR = n
    }

    notify() {
        if (this.flags |= 16, !(this.flags & 8) && Z !== this) return Br(this, !0), !0
    }

    get value() {
        const t = this.dep.track();
        return Ur(this), t && (t.version = this.dep.version), this._value
    }

    set value(t) {
        this.setter && this.setter(t)
    }
}

function Ii(e, t, s = !1) {
    let n, r;
    return D(e) ? n = e : (n = e.get, r = e.set), new $i(n, r, s)
}

const ds = {}, bs = new WeakMap;
let xt;

function ki(e, t = !1, s = xt) {
    if (s) {
        let n = bs.get(s);
        n || bs.set(s, n = []), n.push(e)
    }
}

function Li(e, t, s = Y) {
    const {immediate: n, deep: r, once: o, scheduler: i, augmentJob: l, call: c} = s,
        d = j => r ? j : Pe(j) || r === !1 || r === 0 ? Ge(j, 1) : Ge(j);
    let f, p, m, h, E = !1, P = !1;
    if (fe(e) ? (p = () => e.value, E = Pe(e)) : Mt(e) ? (p = () => d(e), E = !0) : H(e) ? (P = !0, E = e.some(j => Mt(j) || Pe(j)), p = () => e.map(j => {
        if (fe(j)) return j.value;
        if (Mt(j)) return d(j);
        if (D(j)) return c ? c(j, 2) : j()
    })) : D(e) ? t ? p = c ? () => c(e, 2) : e : p = () => {
        if (m) {
            Qe();
            try {
                m()
            } finally {
                Ye()
            }
        }
        const j = xt;
        xt = f;
        try {
            return c ? c(e, 3, [h]) : e(h)
        } finally {
            xt = j
        }
    } : p = Be, t && r) {
        const j = p, J = r === !0 ? 1 / 0 : r;
        p = () => Ge(j(), J)
    }
    const B = ui(), I = () => {
        f.stop(), B && B.active && xn(B.effects, f)
    };
    if (o && t) {
        const j = t;
        t = (...J) => {
            j(...J), I()
        }
    }
    let M = P ? new Array(e.length).fill(ds) : ds;
    const L = j => {
        if (!(!(f.flags & 1) || !f.dirty && !j)) if (t) {
            const J = f.run();
            if (r || E || (P ? J.some((oe, te) => ct(oe, M[te])) : ct(J, M))) {
                m && m();
                const oe = xt;
                xt = f;
                try {
                    const te = [J, M === ds ? void 0 : P && M[0] === ds ? [] : M, h];
                    M = J, c ? c(t, 3, te) : t(...te)
                } finally {
                    xt = oe
                }
            }
        } else f.run()
    };
    return l && l(L), f = new Hr(p), f.scheduler = i ? () => i(L, !1) : L, h = j => ki(j, !1, f), m = f.onStop = () => {
        const j = bs.get(f);
        if (j) {
            if (c) c(j, 4); else for (const J of j) J();
            bs.delete(f)
        }
    }, t ? n ? L(!0) : M = f.run() : i ? i(L.bind(null, !0), !0) : f.run(), I.pause = f.pause.bind(f), I.resume = f.resume.bind(f), I.stop = I, I
}

function Ge(e, t = 1 / 0, s) {
    if (t <= 0 || !ee(e) || e.__v_skip || (s = s || new Map, (s.get(e) || 0) >= t)) return e;
    if (s.set(e, t), t--, fe(e)) Ge(e.value, t, s); else if (H(e)) for (let n = 0; n < e.length; n++) Ge(e[n], t, s); else if (Tr(e) || Tt(e)) e.forEach(n => {
        Ge(n, t, s)
    }); else if ($r(e)) {
        for (const n in e) Ge(e[n], t, s);
        for (const n of Object.getOwnPropertySymbols(e)) Object.prototype.propertyIsEnumerable.call(e, n) && Ge(e[n], t, s)
    }
    return e
}

/**
 * @vue/runtime-core v3.5.21
 * (c) 2018-present Yuxi (Evan) You and Vue contributors
 * @license MIT
 **/function ls(e, t, s, n) {
    try {
        return n ? e(...n) : e()
    } catch (r) {
        ks(r, t, s)
    }
}

function Ve(e, t, s, n) {
    if (D(e)) {
        const r = ls(e, t, s, n);
        return r && Mr(r) && r.catch(o => {
            ks(o, t, s)
        }), r
    }
    if (H(e)) {
        const r = [];
        for (let o = 0; o < e.length; o++) r.push(Ve(e[o], t, s, n));
        return r
    }
}

function ks(e, t, s, n = !0) {
    const r = t ? t.vnode : null, {errorHandler: o, throwUnhandledErrorInProduction: i} = t && t.appContext.config || Y;
    if (t) {
        let l = t.parent;
        const c = t.proxy, d = `https://vuejs.org/error-reference/#runtime-${s}`;
        for (; l;) {
            const f = l.ec;
            if (f) {
                for (let p = 0; p < f.length; p++) if (f[p](e, c, d) === !1) return
            }
            l = l.parent
        }
        if (o) {
            Qe(), ls(o, null, 10, [e, c, d]), Ye();
            return
        }
    }
    Fi(e, s, r, n, i)
}

function Fi(e, t, s, n = !0, r = !1) {
    if (r) throw e;
    console.error(e)
}

const pe = [];
let He = -1;
const $t = [];
let nt = null, Pt = 0;
const ro = Promise.resolve();
let ys = null;

function oo(e) {
    const t = ys || ro;
    return e ? t.then(this ? e.bind(this) : e) : t
}

function Ni(e) {
    let t = He + 1, s = pe.length;
    for (; t < s;) {
        const n = t + s >>> 1, r = pe[n], o = ss(r);
        o < e || o === e && r.flags & 2 ? t = n + 1 : s = n
    }
    return t
}

function Pn(e) {
    if (!(e.flags & 1)) {
        const t = ss(e), s = pe[pe.length - 1];
        !s || !(e.flags & 2) && t >= ss(s) ? pe.push(e) : pe.splice(Ni(t), 0, e), e.flags |= 1, io()
    }
}

function io() {
    ys || (ys = ro.then(co))
}

function Hi(e) {
    H(e) ? $t.push(...e) : nt && e.id === -1 ? nt.splice(Pt + 1, 0, e) : e.flags & 1 || ($t.push(e), e.flags |= 1), io()
}

function Bn(e, t, s = He + 1) {
    for (; s < pe.length; s++) {
        const n = pe[s];
        if (n && n.flags & 2) {
            if (e && n.id !== e.uid) continue;
            pe.splice(s, 1), s--, n.flags & 4 && (n.flags &= -2), n(), n.flags & 4 || (n.flags &= -2)
        }
    }
}

function lo(e) {
    if ($t.length) {
        const t = [...new Set($t)].sort((s, n) => ss(s) - ss(n));
        if ($t.length = 0, nt) {
            nt.push(...t);
            return
        }
        for (nt = t, Pt = 0; Pt < nt.length; Pt++) {
            const s = nt[Pt];
            s.flags & 4 && (s.flags &= -2), s.flags & 8 || s(), s.flags &= -2
        }
        nt = null, Pt = 0
    }
}

const ss = e => e.id == null ? e.flags & 2 ? -1 : 1 / 0 : e.id;

function co(e) {
    try {
        for (He = 0; He < pe.length; He++) {
            const t = pe[He];
            t && !(t.flags & 8) && (t.flags & 4 && (t.flags &= -2), ls(t, t.i, t.i ? 15 : 14), t.flags & 4 || (t.flags &= -2))
        }
    } finally {
        for (; He < pe.length; He++) {
            const t = pe[He];
            t && (t.flags &= -2)
        }
        He = -1, pe.length = 0, lo(), ys = null, (pe.length || $t.length) && co()
    }
}

let Se = null, ao = null;

function _s(e) {
    const t = Se;
    return Se = e, ao = e && e.type.__scopeId || null, t
}

function at(e, t = Se, s) {
    if (!t || e._n) return e;
    const n = (...r) => {
        n._d && Es(-1);
        const o = _s(t);
        let i;
        try {
            i = e(...r)
        } finally {
            _s(o), n._d && Es(1)
        }
        return i
    };
    return n._n = !0, n._c = !0, n._d = !0, n
}

function Di(e, t) {
    if (Se === null) return e;
    const s = Ds(Se), n = e.dirs || (e.dirs = []);
    for (let r = 0; r < t.length; r++) {
        let [o, i, l, c = Y] = t[r];
        o && (D(o) && (o = {mounted: o, updated: o}), o.deep && Ge(i), n.push({
            dir: o,
            instance: s,
            value: i,
            oldValue: void 0,
            arg: l,
            modifiers: c
        }))
    }
    return e
}

function gt(e, t, s, n) {
    const r = e.dirs, o = t && t.dirs;
    for (let i = 0; i < r.length; i++) {
        const l = r[i];
        o && (l.oldValue = o[i].value);
        let c = l.dir[n];
        c && (Qe(), Ve(c, s, 8, [e.el, l, e, t]), Ye())
    }
}

const Bi = Symbol("_vte"), Vi = e => e.__isTeleport, Ki = Symbol("_leaveCb");

function An(e, t) {
    e.shapeFlag & 6 && e.component ? (e.transition = t, An(e.component.subTree, t)) : e.shapeFlag & 128 ? (e.ssContent.transition = t.clone(e.ssContent), e.ssFallback.transition = t.clone(e.ssFallback)) : e.transition = t
}

function pt(e, t) {
    return D(e) ? ue({name: e.name}, t, {setup: e}) : e
}

function fo(e) {
    e.ids = [e.ids[0] + e.ids[2]++ + "-", 0, 0]
}

const ws = new WeakMap;

function Gt(e, t, s, n, r = !1) {
    if (H(e)) {
        e.forEach((E, P) => Gt(E, t && (H(t) ? t[P] : t), s, n, r));
        return
    }
    if (Jt(n) && !r) {
        n.shapeFlag & 512 && n.type.__asyncResolved && n.component.subTree.component && Gt(e, t, s, n.component.subTree);
        return
    }
    const o = n.shapeFlag & 4 ? Ds(n.component) : n.el, i = r ? null : o, {i: l, r: c} = e, d = t && t.r,
        f = l.refs === Y ? l.refs = {} : l.refs, p = l.setupState, m = W(p), h = p === Y ? Or : E => z(m, E);
    if (d != null && d !== c) {
        if (Vn(t), ne(d)) f[d] = null, h(d) && (p[d] = null); else if (fe(d)) {
            d.value = null;
            const E = t;
            E.k && (f[E.k] = null)
        }
    }
    if (D(c)) ls(c, l, 12, [i, f]); else {
        const E = ne(c), P = fe(c);
        if (E || P) {
            const B = () => {
                if (e.f) {
                    const I = E ? h(c) ? p[c] : f[c] : c.value;
                    if (r) H(I) && xn(I, o); else if (H(I)) I.includes(o) || I.push(o); else if (E) f[c] = [o], h(c) && (p[c] = f[c]); else {
                        const M = [o];
                        c.value = M, e.k && (f[e.k] = M)
                    }
                } else E ? (f[c] = i, h(c) && (p[c] = i)) : P && (c.value = i, e.k && (f[e.k] = i))
            };
            if (i) {
                const I = () => {
                    B(), ws.delete(e)
                };
                I.id = -1, ws.set(e, I), _e(I, s)
            } else Vn(e), B()
        }
    }
}

function Vn(e) {
    const t = ws.get(e);
    t && (t.flags |= 8, ws.delete(e))
}

js().requestIdleCallback;
js().cancelIdleCallback;
const Jt = e => !!e.type.__asyncLoader, uo = e => e.type.__isKeepAlive;

function Ui(e, t) {
    po(e, "a", t)
}

function Wi(e, t) {
    po(e, "da", t)
}

function po(e, t, s = ae) {
    const n = e.__wdc || (e.__wdc = () => {
        let r = s;
        for (; r;) {
            if (r.isDeactivated) return;
            r = r.parent
        }
        return e()
    });
    if (Ls(t, n, s), s) {
        let r = s.parent;
        for (; r && r.parent;) uo(r.parent.vnode) && zi(n, t, s, r), r = r.parent
    }
}

function zi(e, t, s, n) {
    const r = Ls(t, e, n, !0);
    ho(() => {
        xn(n[t], r)
    }, s)
}

function Ls(e, t, s = ae, n = !1) {
    if (s) {
        const r = s[e] || (s[e] = []), o = t.__weh || (t.__weh = (...i) => {
            Qe();
            const l = cs(s), c = Ve(t, s, e, i);
            return l(), Ye(), c
        });
        return n ? r.unshift(o) : r.push(o), o
    }
}

const Xe = e => (t, s = ae) => {
        (!rs || e === "sp") && Ls(e, (...n) => t(...n), s)
    }, qi = Xe("bm"), Gi = Xe("m"), Ji = Xe("bu"), Qi = Xe("u"), Yi = Xe("bum"), ho = Xe("um"), Xi = Xe("sp"),
    Zi = Xe("rtg"), el = Xe("rtc");

function tl(e, t = ae) {
    Ls("ec", e, t)
}

const sl = "components";

function Fs(e, t) {
    return rl(sl, e, !0, t) || e
}

const nl = Symbol.for("v-ndc");

function rl(e, t, s = !0, n = !1) {
    const r = Se || ae;
    if (r) {
        const o = r.type;
        {
            const l = Gl(o, !1);
            if (l && (l === t || l === Ae(t) || l === Ms(Ae(t)))) return o
        }
        const i = Kn(r[e] || o[e], t) || Kn(r.appContext[e], t);
        return !i && n ? o : i
    }
}

function Kn(e, t) {
    return e && (e[t] || e[Ae(t)] || e[Ms(Ae(t))])
}

function bt(e, t, s, n) {
    let r;
    const o = s, i = H(e);
    if (i || ne(e)) {
        const l = i && Mt(e);
        let c = !1, d = !1;
        l && (c = !Pe(e), d = ft(e), e = $s(e)), r = new Array(e.length);
        for (let f = 0, p = e.length; f < p; f++) r[f] = t(c ? d ? vs(ie(e[f])) : ie(e[f]) : e[f], f, void 0, o)
    } else if (typeof e == "number") {
        r = new Array(e);
        for (let l = 0; l < e; l++) r[l] = t(l + 1, l, void 0, o)
    } else if (ee(e)) if (e[Symbol.iterator]) r = Array.from(e, (l, c) => t(l, c, void 0, o)); else {
        const l = Object.keys(e);
        r = new Array(l.length);
        for (let c = 0, d = l.length; c < d; c++) {
            const f = l[c];
            r[c] = t(e[f], f, c, o)
        }
    } else r = [];
    return r
}

const on = e => e ? Io(e) ? Ds(e) : on(e.parent) : null, Qt = ue(Object.create(null), {
    $: e => e,
    $el: e => e.vnode.el,
    $data: e => e.data,
    $props: e => e.props,
    $attrs: e => e.attrs,
    $slots: e => e.slots,
    $refs: e => e.refs,
    $parent: e => on(e.parent),
    $root: e => on(e.root),
    $host: e => e.ce,
    $emit: e => e.emit,
    $options: e => mo(e),
    $forceUpdate: e => e.f || (e.f = () => {
        Pn(e.update)
    }),
    $nextTick: e => e.n || (e.n = oo.bind(e.proxy)),
    $watch: e => Cl.bind(e)
}), Gs = (e, t) => e !== Y && !e.__isScriptSetup && z(e, t), ol = {
    get({_: e}, t) {
        if (t === "__v_skip") return !0;
        const {ctx: s, setupState: n, data: r, props: o, accessCache: i, type: l, appContext: c} = e;
        let d;
        if (t[0] !== "$") {
            const h = i[t];
            if (h !== void 0) switch (h) {
                case 1:
                    return n[t];
                case 2:
                    return r[t];
                case 4:
                    return s[t];
                case 3:
                    return o[t]
            } else {
                if (Gs(n, t)) return i[t] = 1, n[t];
                if (r !== Y && z(r, t)) return i[t] = 2, r[t];
                if ((d = e.propsOptions[0]) && z(d, t)) return i[t] = 3, o[t];
                if (s !== Y && z(s, t)) return i[t] = 4, s[t];
                ln && (i[t] = 0)
            }
        }
        const f = Qt[t];
        let p, m;
        if (f) return t === "$attrs" && le(e.attrs, "get", ""), f(e);
        if ((p = l.__cssModules) && (p = p[t])) return p;
        if (s !== Y && z(s, t)) return i[t] = 4, s[t];
        if (m = c.config.globalProperties, z(m, t)) return m[t]
    }, set({_: e}, t, s) {
        const {data: n, setupState: r, ctx: o} = e;
        return Gs(r, t) ? (r[t] = s, !0) : n !== Y && z(n, t) ? (n[t] = s, !0) : z(e.props, t) || t[0] === "$" && t.slice(1) in e ? !1 : (o[t] = s, !0)
    }, has({_: {data: e, setupState: t, accessCache: s, ctx: n, appContext: r, propsOptions: o, type: i}}, l) {
        let c, d;
        return !!(s[l] || e !== Y && l[0] !== "$" && z(e, l) || Gs(t, l) || (c = o[0]) && z(c, l) || z(n, l) || z(Qt, l) || z(r.config.globalProperties, l) || (d = i.__cssModules) && d[l])
    }, defineProperty(e, t, s) {
        return s.get != null ? e._.accessCache[t] = 0 : z(s, "value") && this.set(e, t, s.value, null), Reflect.defineProperty(e, t, s)
    }
};

function Un(e) {
    return H(e) ? e.reduce((t, s) => (t[s] = null, t), {}) : e
}

let ln = !0;

function il(e) {
    const t = mo(e), s = e.proxy, n = e.ctx;
    ln = !1, t.beforeCreate && Wn(t.beforeCreate, e, "bc");
    const {data: r, computed: o, methods: i, watch: l, provide: c, inject: d, created: f, beforeMount: p, mounted: m, beforeUpdate: h, updated: E, activated: P, deactivated: B, beforeDestroy: I, beforeUnmount: M, destroyed: L, unmounted: j, render: J, renderTracked: oe, renderTriggered: te, errorCaptured: Me, serverPrefetch: Ze, expose: je, inheritAttrs: et, components: ht, directives: $e, filters: Nt} = t;
    if (d && ll(d, n, null), i) for (const G in i) {
        const K = i[G];
        D(K) && (n[G] = K.bind(s))
    }
    if (r) {
        const G = r.call(s, s);
        ee(G) && (e.data = Is(G))
    }
    if (ln = !0, o) for (const G in o) {
        const K = o[G], Ke = D(K) ? K.bind(s, s) : D(K.get) ? K.get.bind(s, s) : Be,
            tt = !D(K) && D(K.set) ? K.set.bind(s) : Be, Ie = Re({get: Ke, set: tt});
        Object.defineProperty(n, G, {enumerable: !0, configurable: !0, get: () => Ie.value, set: ge => Ie.value = ge})
    }
    if (l) for (const G in l) go(l[G], n, s, G);
    if (c) {
        const G = D(c) ? c.call(s) : c;
        Reflect.ownKeys(G).forEach(K => {
            ps(K, G[K])
        })
    }
    f && Wn(f, e, "c");

    function re(G, K) {
        H(K) ? K.forEach(Ke => G(Ke.bind(s))) : K && G(K.bind(s))
    }

    if (re(qi, p), re(Gi, m), re(Ji, h), re(Qi, E), re(Ui, P), re(Wi, B), re(tl, Me), re(el, oe), re(Zi, te), re(Yi, M), re(ho, j), re(Xi, Ze), H(je)) if (je.length) {
        const G = e.exposed || (e.exposed = {});
        je.forEach(K => {
            Object.defineProperty(G, K, {get: () => s[K], set: Ke => s[K] = Ke, enumerable: !0})
        })
    } else e.exposed || (e.exposed = {});
    J && e.render === Be && (e.render = J), et != null && (e.inheritAttrs = et), ht && (e.components = ht), $e && (e.directives = $e), Ze && fo(e)
}

function ll(e, t, s = Be) {
    H(e) && (e = cn(e));
    for (const n in e) {
        const r = e[n];
        let o;
        ee(r) ? "default" in r ? o = Je(r.from || n, r.default, !0) : o = Je(r.from || n) : o = Je(r), fe(o) ? Object.defineProperty(t, n, {
            enumerable: !0,
            configurable: !0,
            get: () => o.value,
            set: i => o.value = i
        }) : t[n] = o
    }
}

function Wn(e, t, s) {
    Ve(H(e) ? e.map(n => n.bind(t.proxy)) : e.bind(t.proxy), t, s)
}

function go(e, t, s, n) {
    let r = n.includes(".") ? Oo(s, n) : () => s[n];
    if (ne(e)) {
        const o = t[e];
        D(o) && hs(r, o)
    } else if (D(e)) hs(r, e.bind(s)); else if (ee(e)) if (H(e)) e.forEach(o => go(o, t, s, n)); else {
        const o = D(e.handler) ? e.handler.bind(s) : t[e.handler];
        D(o) && hs(r, o, e)
    }
}

function mo(e) {
    const t = e.type, {mixins: s, extends: n} = t, {mixins: r, optionsCache: o, config: {optionMergeStrategies: i}} = e.appContext,
        l = o.get(t);
    let c;
    return l ? c = l : !r.length && !s && !n ? c = t : (c = {}, r.length && r.forEach(d => Ss(c, d, i, !0)), Ss(c, t, i)), ee(t) && o.set(t, c), c
}

function Ss(e, t, s, n = !1) {
    const {mixins: r, extends: o} = t;
    o && Ss(e, o, s, !0), r && r.forEach(i => Ss(e, i, s, !0));
    for (const i in t) if (!(n && i === "expose")) {
        const l = cl[i] || s && s[i];
        e[i] = l ? l(e[i], t[i]) : t[i]
    }
    return e
}

const cl = {
    data: zn,
    props: qn,
    emits: qn,
    methods: Ut,
    computed: Ut,
    beforeCreate: de,
    created: de,
    beforeMount: de,
    mounted: de,
    beforeUpdate: de,
    updated: de,
    beforeDestroy: de,
    beforeUnmount: de,
    destroyed: de,
    unmounted: de,
    activated: de,
    deactivated: de,
    errorCaptured: de,
    serverPrefetch: de,
    components: Ut,
    directives: Ut,
    watch: fl,
    provide: zn,
    inject: al
};

function zn(e, t) {
    return t ? e ? function () {
        return ue(D(e) ? e.call(this, this) : e, D(t) ? t.call(this, this) : t)
    } : t : e
}

function al(e, t) {
    return Ut(cn(e), cn(t))
}

function cn(e) {
    if (H(e)) {
        const t = {};
        for (let s = 0; s < e.length; s++) t[e[s]] = e[s];
        return t
    }
    return e
}

function de(e, t) {
    return e ? [...new Set([].concat(e, t))] : t
}

function Ut(e, t) {
    return e ? ue(Object.create(null), e, t) : t
}

function qn(e, t) {
    return e ? H(e) && H(t) ? [...new Set([...e, ...t])] : ue(Object.create(null), Un(e), Un(t ?? {})) : t
}

function fl(e, t) {
    if (!e) return t;
    if (!t) return e;
    const s = ue(Object.create(null), e);
    for (const n in t) s[n] = de(e[n], t[n]);
    return s
}

function xo() {
    return {
        app: null,
        config: {
            isNativeTag: Or,
            performance: !1,
            globalProperties: {},
            optionMergeStrategies: {},
            errorHandler: void 0,
            warnHandler: void 0,
            compilerOptions: {}
        },
        mixins: [],
        components: {},
        directives: {},
        provides: Object.create(null),
        optionsCache: new WeakMap,
        propsCache: new WeakMap,
        emitsCache: new WeakMap
    }
}

let ul = 0;

function dl(e, t) {
    return function (n, r = null) {
        D(n) || (n = ue({}, n)), r != null && !ee(r) && (r = null);
        const o = xo(), i = new WeakSet, l = [];
        let c = !1;
        const d = o.app = {
            _uid: ul++,
            _component: n,
            _props: r,
            _container: null,
            _context: o,
            _instance: null,
            version: Ql,
            get config() {
                return o.config
            },
            set config(f) {
            },
            use(f, ...p) {
                return i.has(f) || (f && D(f.install) ? (i.add(f), f.install(d, ...p)) : D(f) && (i.add(f), f(d, ...p))), d
            },
            mixin(f) {
                return o.mixins.includes(f) || o.mixins.push(f), d
            },
            component(f, p) {
                return p ? (o.components[f] = p, d) : o.components[f]
            },
            directive(f, p) {
                return p ? (o.directives[f] = p, d) : o.directives[f]
            },
            mount(f, p, m) {
                if (!c) {
                    const h = d._ceVNode || se(n, r);
                    return h.appContext = o, m === !0 ? m = "svg" : m === !1 && (m = void 0), e(h, f, m), c = !0, d._container = f, f.__vue_app__ = d, Ds(h.component)
                }
            },
            onUnmount(f) {
                l.push(f)
            },
            unmount() {
                c && (Ve(l, d._instance, 16), e(null, d._container), delete d._container.__vue_app__)
            },
            provide(f, p) {
                return o.provides[f] = p, d
            },
            runWithContext(f) {
                const p = It;
                It = d;
                try {
                    return f()
                } finally {
                    It = p
                }
            }
        };
        return d
    }
}

let It = null;

function ps(e, t) {
    if (ae) {
        let s = ae.provides;
        const n = ae.parent && ae.parent.provides;
        n === s && (s = ae.provides = Object.create(n)), s[e] = t
    }
}

function Je(e, t, s = !1) {
    const n = Kl();
    if (n || It) {
        let r = It ? It._context.provides : n ? n.parent == null || n.ce ? n.vnode.appContext && n.vnode.appContext.provides : n.parent.provides : void 0;
        if (r && e in r) return r[e];
        if (arguments.length > 1) return s && D(t) ? t.call(n && n.proxy) : t
    }
}

const vo = {}, bo = () => Object.create(vo), yo = e => Object.getPrototypeOf(e) === vo;

function pl(e, t, s, n = !1) {
    const r = {}, o = bo();
    e.propsDefaults = Object.create(null), _o(e, t, r, o);
    for (const i in e.propsOptions[0]) i in r || (r[i] = void 0);
    s ? e.props = n ? r : Zr(r) : e.type.props ? e.props = r : e.props = o, e.attrs = o
}

function hl(e, t, s, n) {
    const {props: r, attrs: o, vnode: {patchFlag: i}} = e, l = W(r), [c] = e.propsOptions;
    let d = !1;
    if ((n || i > 0) && !(i & 16)) {
        if (i & 8) {
            const f = e.vnode.dynamicProps;
            for (let p = 0; p < f.length; p++) {
                let m = f[p];
                if (Ns(e.emitsOptions, m)) continue;
                const h = t[m];
                if (c) if (z(o, m)) h !== o[m] && (o[m] = h, d = !0); else {
                    const E = Ae(m);
                    r[E] = an(c, l, E, h, e, !1)
                } else h !== o[m] && (o[m] = h, d = !0)
            }
        }
    } else {
        _o(e, t, r, o) && (d = !0);
        let f;
        for (const p in l) (!t || !z(t, p) && ((f = wt(p)) === p || !z(t, f))) && (c ? s && (s[p] !== void 0 || s[f] !== void 0) && (r[p] = an(c, l, p, void 0, e, !0)) : delete r[p]);
        if (o !== l) for (const p in o) (!t || !z(t, p)) && (delete o[p], d = !0)
    }
    d && qe(e.attrs, "set", "")
}

function _o(e, t, s, n) {
    const [r, o] = e.propsOptions;
    let i = !1, l;
    if (t) for (let c in t) {
        if (Wt(c)) continue;
        const d = t[c];
        let f;
        r && z(r, f = Ae(c)) ? !o || !o.includes(f) ? s[f] = d : (l || (l = {}))[f] = d : Ns(e.emitsOptions, c) || (!(c in n) || d !== n[c]) && (n[c] = d, i = !0)
    }
    if (o) {
        const c = W(s), d = l || Y;
        for (let f = 0; f < o.length; f++) {
            const p = o[f];
            s[p] = an(r, c, p, d[p], e, !z(d, p))
        }
    }
    return i
}

function an(e, t, s, n, r, o) {
    const i = e[s];
    if (i != null) {
        const l = z(i, "default");
        if (l && n === void 0) {
            const c = i.default;
            if (i.type !== Function && !i.skipFactory && D(c)) {
                const {propsDefaults: d} = r;
                if (s in d) n = d[s]; else {
                    const f = cs(r);
                    n = d[s] = c.call(null, t), f()
                }
            } else n = c;
            r.ce && r.ce._setProp(s, n)
        }
        i[0] && (o && !l ? n = !1 : i[1] && (n === "" || n === wt(s)) && (n = !0))
    }
    return n
}

const gl = new WeakMap;

function wo(e, t, s = !1) {
    const n = s ? gl : t.propsCache, r = n.get(e);
    if (r) return r;
    const o = e.props, i = {}, l = [];
    let c = !1;
    if (!D(e)) {
        const f = p => {
            c = !0;
            const [m, h] = wo(p, t, !0);
            ue(i, m), h && l.push(...h)
        };
        !s && t.mixins.length && t.mixins.forEach(f), e.extends && f(e.extends), e.mixins && e.mixins.forEach(f)
    }
    if (!o && !c) return ee(e) && n.set(e, Ot), Ot;
    if (H(o)) for (let f = 0; f < o.length; f++) {
        const p = Ae(o[f]);
        Gn(p) && (i[p] = Y)
    } else if (o) for (const f in o) {
        const p = Ae(f);
        if (Gn(p)) {
            const m = o[f], h = i[p] = H(m) || D(m) ? {type: m} : ue({}, m), E = h.type;
            let P = !1, B = !0;
            if (H(E)) for (let I = 0; I < E.length; ++I) {
                const M = E[I], L = D(M) && M.name;
                if (L === "Boolean") {
                    P = !0;
                    break
                } else L === "String" && (B = !1)
            } else P = D(E) && E.name === "Boolean";
            h[0] = P, h[1] = B, (P || z(h, "default")) && l.push(p)
        }
    }
    const d = [i, l];
    return ee(e) && n.set(e, d), d
}

function Gn(e) {
    return e[0] !== "$" && !Wt(e)
}

const On = e => e === "_" || e === "_ctx" || e === "$stable", Tn = e => H(e) ? e.map(De) : [De(e)], ml = (e, t, s) => {
    if (t._n) return t;
    const n = at((...r) => Tn(t(...r)), s);
    return n._c = !1, n
}, So = (e, t, s) => {
    const n = e._ctx;
    for (const r in e) {
        if (On(r)) continue;
        const o = e[r];
        if (D(o)) t[r] = ml(r, o, n); else if (o != null) {
            const i = Tn(o);
            t[r] = () => i
        }
    }
}, Eo = (e, t) => {
    const s = Tn(t);
    e.slots.default = () => s
}, Co = (e, t, s) => {
    for (const n in t) (s || !On(n)) && (e[n] = t[n])
}, xl = (e, t, s) => {
    const n = e.slots = bo();
    if (e.vnode.shapeFlag & 32) {
        const r = t._;
        r ? (Co(n, t, s), s && Ir(n, "_", r, !0)) : So(t, n)
    } else t && Eo(e, t)
}, vl = (e, t, s) => {
    const {vnode: n, slots: r} = e;
    let o = !0, i = Y;
    if (n.shapeFlag & 32) {
        const l = t._;
        l ? s && l === 1 ? o = !1 : Co(r, t, s) : (o = !t.$stable, So(t, r)), i = t
    } else t && (Eo(e, t), i = {default: 1});
    if (o) for (const l in r) !On(l) && i[l] == null && delete r[l]
}, _e = $l;

function bl(e) {
    return yl(e)
}

function yl(e, t) {
    const s = js();
    s.__VUE__ = !0;
    const {insert: n, remove: r, patchProp: o, createElement: i, createText: l, createComment: c, setText: d, setElementText: f, parentNode: p, nextSibling: m, setScopeId: h = Be, insertStaticContent: E} = e,
        P = (a, u, g, b = null, _ = null, v = null, R = void 0, C = null, S = !!u.dynamicChildren) => {
            if (a === u) return;
            a && !Bt(a, u) && (b = y(a), ge(a, _, v, !0), a = null), u.patchFlag === -2 && (S = !1, u.dynamicChildren = null);
            const {type: w, ref: F, shapeFlag: O} = u;
            switch (w) {
                case Hs:
                    B(a, u, g, b);
                    break;
                case ut:
                    I(a, u, g, b);
                    break;
                case gs:
                    a == null && M(u, g, b, R);
                    break;
                case ce:
                    ht(a, u, g, b, _, v, R, C, S);
                    break;
                default:
                    O & 1 ? J(a, u, g, b, _, v, R, C, S) : O & 6 ? $e(a, u, g, b, _, v, R, C, S) : (O & 64 || O & 128) && w.process(a, u, g, b, _, v, R, C, S, $)
            }
            F != null && _ ? Gt(F, a && a.ref, v, u || a, !u) : F == null && a && a.ref != null && Gt(a.ref, null, v, a, !0)
        }, B = (a, u, g, b) => {
            if (a == null) n(u.el = l(u.children), g, b); else {
                const _ = u.el = a.el;
                u.children !== a.children && d(_, u.children)
            }
        }, I = (a, u, g, b) => {
            a == null ? n(u.el = c(u.children || ""), g, b) : u.el = a.el
        }, M = (a, u, g, b) => {
            [a.el, a.anchor] = E(a.children, u, g, b, a.el, a.anchor)
        }, L = ({el: a, anchor: u}, g, b) => {
            let _;
            for (; a && a !== u;) _ = m(a), n(a, g, b), a = _;
            n(u, g, b)
        }, j = ({el: a, anchor: u}) => {
            let g;
            for (; a && a !== u;) g = m(a), r(a), a = g;
            r(u)
        }, J = (a, u, g, b, _, v, R, C, S) => {
            u.type === "svg" ? R = "svg" : u.type === "math" && (R = "mathml"), a == null ? oe(u, g, b, _, v, R, C, S) : Ze(a, u, _, v, R, C, S)
        }, oe = (a, u, g, b, _, v, R, C) => {
            let S, w;
            const {props: F, shapeFlag: O, transition: k, dirs: N} = a;
            if (S = a.el = i(a.type, v, F && F.is, F), O & 8 ? f(S, a.children) : O & 16 && Me(a.children, S, null, b, _, Js(a, v), R, C), N && gt(a, null, b, "created"), te(S, a, a.scopeId, R, b), F) {
                for (const X in F) X !== "value" && !Wt(X) && o(S, X, null, F[X], v, b);
                "value" in F && o(S, "value", null, F.value, v), (w = F.onVnodeBeforeMount) && Ne(w, b, a)
            }
            N && gt(a, null, b, "beforeMount");
            const V = _l(_, k);
            V && k.beforeEnter(S), n(S, u, g), ((w = F && F.onVnodeMounted) || V || N) && _e(() => {
                w && Ne(w, b, a), V && k.enter(S), N && gt(a, null, b, "mounted")
            }, _)
        }, te = (a, u, g, b, _) => {
            if (g && h(a, g), b) for (let v = 0; v < b.length; v++) h(a, b[v]);
            if (_) {
                let v = _.subTree;
                if (u === v || Mo(v.type) && (v.ssContent === u || v.ssFallback === u)) {
                    const R = _.vnode;
                    te(a, R, R.scopeId, R.slotScopeIds, _.parent)
                }
            }
        }, Me = (a, u, g, b, _, v, R, C, S = 0) => {
            for (let w = S; w < a.length; w++) {
                const F = a[w] = C ? rt(a[w]) : De(a[w]);
                P(null, F, u, g, b, _, v, R, C)
            }
        }, Ze = (a, u, g, b, _, v, R) => {
            const C = u.el = a.el;
            let {patchFlag: S, dynamicChildren: w, dirs: F} = u;
            S |= a.patchFlag & 16;
            const O = a.props || Y, k = u.props || Y;
            let N;
            if (g && mt(g, !1), (N = k.onVnodeBeforeUpdate) && Ne(N, g, u, a), F && gt(u, a, g, "beforeUpdate"), g && mt(g, !0), (O.innerHTML && k.innerHTML == null || O.textContent && k.textContent == null) && f(C, ""), w ? je(a.dynamicChildren, w, C, g, b, Js(u, _), v) : R || K(a, u, C, null, g, b, Js(u, _), v, !1), S > 0) {
                if (S & 16) et(C, O, k, g, _); else if (S & 2 && O.class !== k.class && o(C, "class", null, k.class, _), S & 4 && o(C, "style", O.style, k.style, _), S & 8) {
                    const V = u.dynamicProps;
                    for (let X = 0; X < V.length; X++) {
                        const q = V[X], me = O[q], xe = k[q];
                        (xe !== me || q === "value") && o(C, q, me, xe, _, g)
                    }
                }
                S & 1 && a.children !== u.children && f(C, u.children)
            } else !R && w == null && et(C, O, k, g, _);
            ((N = k.onVnodeUpdated) || F) && _e(() => {
                N && Ne(N, g, u, a), F && gt(u, a, g, "updated")
            }, b)
        }, je = (a, u, g, b, _, v, R) => {
            for (let C = 0; C < u.length; C++) {
                const S = a[C], w = u[C], F = S.el && (S.type === ce || !Bt(S, w) || S.shapeFlag & 198) ? p(S.el) : g;
                P(S, w, F, null, b, _, v, R, !0)
            }
        }, et = (a, u, g, b, _) => {
            if (u !== g) {
                if (u !== Y) for (const v in u) !Wt(v) && !(v in g) && o(a, v, u[v], null, _, b);
                for (const v in g) {
                    if (Wt(v)) continue;
                    const R = g[v], C = u[v];
                    R !== C && v !== "value" && o(a, v, C, R, _, b)
                }
                "value" in g && o(a, "value", u.value, g.value, _)
            }
        }, ht = (a, u, g, b, _, v, R, C, S) => {
            const w = u.el = a ? a.el : l(""), F = u.anchor = a ? a.anchor : l("");
            let {patchFlag: O, dynamicChildren: k, slotScopeIds: N} = u;
            N && (C = C ? C.concat(N) : N), a == null ? (n(w, g, b), n(F, g, b), Me(u.children || [], g, F, _, v, R, C, S)) : O > 0 && O & 64 && k && a.dynamicChildren ? (je(a.dynamicChildren, k, g, _, v, R, C), (u.key != null || _ && u === _.subTree) && Ro(a, u, !0)) : K(a, u, g, F, _, v, R, C, S)
        }, $e = (a, u, g, b, _, v, R, C, S) => {
            u.slotScopeIds = C, a == null ? u.shapeFlag & 512 ? _.ctx.activate(u, g, b, R, S) : Nt(u, g, b, _, v, R, S) : St(a, u, S)
        }, Nt = (a, u, g, b, _, v, R) => {
            const C = a.component = Vl(a, b, _);
            if (uo(a) && (C.ctx.renderer = $), Ul(C, !1, R), C.asyncDep) {
                if (_ && _.registerDep(C, re, R), !a.el) {
                    const S = C.subTree = se(ut);
                    I(null, S, u, g), a.placeholder = S.el
                }
            } else re(C, a, u, g, _, v, R)
        }, St = (a, u, g) => {
            const b = u.component = a.component;
            if (Ml(a, u, g)) if (b.asyncDep && !b.asyncResolved) {
                G(b, u, g);
                return
            } else b.next = u, b.update(); else u.el = a.el, b.vnode = u
        }, re = (a, u, g, b, _, v, R) => {
            const C = () => {
                if (a.isMounted) {
                    let {next: O, bu: k, u: N, parent: V, vnode: X} = a;
                    {
                        const Le = Po(a);
                        if (Le) {
                            O && (O.el = X.el, G(a, O, R)), Le.asyncDep.then(() => {
                                a.isUnmounted || C()
                            });
                            return
                        }
                    }
                    let q = O, me;
                    mt(a, !1), O ? (O.el = X.el, G(a, O, R)) : O = X, k && Ks(k), (me = O.props && O.props.onVnodeBeforeUpdate) && Ne(me, V, O, X), mt(a, !0);
                    const xe = Qn(a), ke = a.subTree;
                    a.subTree = xe, P(ke, xe, p(ke.el), y(ke), a, _, v), O.el = xe.el, q === null && jl(a, xe.el), N && _e(N, _), (me = O.props && O.props.onVnodeUpdated) && _e(() => Ne(me, V, O, X), _)
                } else {
                    let O;
                    const {el: k, props: N} = u, {bm: V, m: X, parent: q, root: me, type: xe} = a, ke = Jt(u);
                    mt(a, !1), V && Ks(V), !ke && (O = N && N.onVnodeBeforeMount) && Ne(O, q, u), mt(a, !0);
                    {
                        me.ce && me.ce._def.shadowRoot !== !1 && me.ce._injectChildStyle(xe);
                        const Le = a.subTree = Qn(a);
                        P(null, Le, g, b, a, _, v), u.el = Le.el
                    }
                    if (X && _e(X, _), !ke && (O = N && N.onVnodeMounted)) {
                        const Le = u;
                        _e(() => Ne(O, q, Le), _)
                    }
                    (u.shapeFlag & 256 || q && Jt(q.vnode) && q.vnode.shapeFlag & 256) && a.a && _e(a.a, _), a.isMounted = !0, u = g = b = null
                }
            };
            a.scope.on();
            const S = a.effect = new Hr(C);
            a.scope.off();
            const w = a.update = S.run.bind(S), F = a.job = S.runIfDirty.bind(S);
            F.i = a, F.id = a.uid, S.scheduler = () => Pn(F), mt(a, !0), w()
        }, G = (a, u, g) => {
            u.component = a;
            const b = a.vnode.props;
            a.vnode = u, a.next = null, hl(a, u.props, b, g), vl(a, u.children, g), Qe(), Bn(a), Ye()
        }, K = (a, u, g, b, _, v, R, C, S = !1) => {
            const w = a && a.children, F = a ? a.shapeFlag : 0, O = u.children, {patchFlag: k, shapeFlag: N} = u;
            if (k > 0) {
                if (k & 128) {
                    tt(w, O, g, b, _, v, R, C, S);
                    return
                } else if (k & 256) {
                    Ke(w, O, g, b, _, v, R, C, S);
                    return
                }
            }
            N & 8 ? (F & 16 && Ce(w, _, v), O !== w && f(g, O)) : F & 16 ? N & 16 ? tt(w, O, g, b, _, v, R, C, S) : Ce(w, _, v, !0) : (F & 8 && f(g, ""), N & 16 && Me(O, g, b, _, v, R, C, S))
        }, Ke = (a, u, g, b, _, v, R, C, S) => {
            a = a || Ot, u = u || Ot;
            const w = a.length, F = u.length, O = Math.min(w, F);
            let k;
            for (k = 0; k < O; k++) {
                const N = u[k] = S ? rt(u[k]) : De(u[k]);
                P(a[k], N, g, null, _, v, R, C, S)
            }
            w > F ? Ce(a, _, v, !0, !1, O) : Me(u, g, b, _, v, R, C, S, O)
        }, tt = (a, u, g, b, _, v, R, C, S) => {
            let w = 0;
            const F = u.length;
            let O = a.length - 1, k = F - 1;
            for (; w <= O && w <= k;) {
                const N = a[w], V = u[w] = S ? rt(u[w]) : De(u[w]);
                if (Bt(N, V)) P(N, V, g, null, _, v, R, C, S); else break;
                w++
            }
            for (; w <= O && w <= k;) {
                const N = a[O], V = u[k] = S ? rt(u[k]) : De(u[k]);
                if (Bt(N, V)) P(N, V, g, null, _, v, R, C, S); else break;
                O--, k--
            }
            if (w > O) {
                if (w <= k) {
                    const N = k + 1, V = N < F ? u[N].el : b;
                    for (; w <= k;) P(null, u[w] = S ? rt(u[w]) : De(u[w]), g, V, _, v, R, C, S), w++
                }
            } else if (w > k) for (; w <= O;) ge(a[w], _, v, !0), w++; else {
                const N = w, V = w, X = new Map;
                for (w = V; w <= k; w++) {
                    const ye = u[w] = S ? rt(u[w]) : De(u[w]);
                    ye.key != null && X.set(ye.key, w)
                }
                let q, me = 0;
                const xe = k - V + 1;
                let ke = !1, Le = 0;
                const Ht = new Array(xe);
                for (w = 0; w < xe; w++) Ht[w] = 0;
                for (w = N; w <= O; w++) {
                    const ye = a[w];
                    if (me >= xe) {
                        ge(ye, _, v, !0);
                        continue
                    }
                    let Fe;
                    if (ye.key != null) Fe = X.get(ye.key); else for (q = V; q <= k; q++) if (Ht[q - V] === 0 && Bt(ye, u[q])) {
                        Fe = q;
                        break
                    }
                    Fe === void 0 ? ge(ye, _, v, !0) : (Ht[Fe - V] = w + 1, Fe >= Le ? Le = Fe : ke = !0, P(ye, u[Fe], g, null, _, v, R, C, S), me++)
                }
                const kn = ke ? wl(Ht) : Ot;
                for (q = kn.length - 1, w = xe - 1; w >= 0; w--) {
                    const ye = V + w, Fe = u[ye], Ln = u[ye + 1], Fn = ye + 1 < F ? Ln.el || Ln.placeholder : b;
                    Ht[w] === 0 ? P(null, Fe, g, Fn, _, v, R, C, S) : ke && (q < 0 || w !== kn[q] ? Ie(Fe, g, Fn, 2) : q--)
                }
            }
        }, Ie = (a, u, g, b, _ = null) => {
            const {el: v, type: R, transition: C, children: S, shapeFlag: w} = a;
            if (w & 6) {
                Ie(a.component.subTree, u, g, b);
                return
            }
            if (w & 128) {
                a.suspense.move(u, g, b);
                return
            }
            if (w & 64) {
                R.move(a, u, g, $);
                return
            }
            if (R === ce) {
                n(v, u, g);
                for (let O = 0; O < S.length; O++) Ie(S[O], u, g, b);
                n(a.anchor, u, g);
                return
            }
            if (R === gs) {
                L(a, u, g);
                return
            }
            if (b !== 2 && w & 1 && C) if (b === 0) C.beforeEnter(v), n(v, u, g), _e(() => C.enter(v), _); else {
                const {leave: O, delayLeave: k, afterLeave: N} = C, V = () => {
                    a.ctx.isUnmounted ? r(v) : n(v, u, g)
                }, X = () => {
                    v._isLeaving && v[Ki](!0), O(v, () => {
                        V(), N && N()
                    })
                };
                k ? k(v, V, X) : X()
            } else n(v, u, g)
        }, ge = (a, u, g, b = !1, _ = !1) => {
            const {type: v, props: R, ref: C, children: S, dynamicChildren: w, shapeFlag: F, patchFlag: O, dirs: k, cacheIndex: N} = a;
            if (O === -2 && (_ = !1), C != null && (Qe(), Gt(C, null, g, a, !0), Ye()), N != null && (u.renderCache[N] = void 0), F & 256) {
                u.ctx.deactivate(a);
                return
            }
            const V = F & 1 && k, X = !Jt(a);
            let q;
            if (X && (q = R && R.onVnodeBeforeUnmount) && Ne(q, u, a), F & 6) as(a.component, g, b); else {
                if (F & 128) {
                    a.suspense.unmount(g, b);
                    return
                }
                V && gt(a, null, u, "beforeUnmount"), F & 64 ? a.type.remove(a, u, g, $, b) : w && !w.hasOnce && (v !== ce || O > 0 && O & 64) ? Ce(w, u, g, !1, !0) : (v === ce && O & 384 || !_ && F & 16) && Ce(S, u, g), b && Et(a)
            }
            (X && (q = R && R.onVnodeUnmounted) || V) && _e(() => {
                q && Ne(q, u, a), V && gt(a, null, u, "unmounted")
            }, g)
        }, Et = a => {
            const {type: u, el: g, anchor: b, transition: _} = a;
            if (u === ce) {
                Ct(g, b);
                return
            }
            if (u === gs) {
                j(a);
                return
            }
            const v = () => {
                r(g), _ && !_.persisted && _.afterLeave && _.afterLeave()
            };
            if (a.shapeFlag & 1 && _ && !_.persisted) {
                const {leave: R, delayLeave: C} = _, S = () => R(g, v);
                C ? C(a.el, v, S) : S()
            } else v()
        }, Ct = (a, u) => {
            let g;
            for (; a !== u;) g = m(a), r(a), a = g;
            r(u)
        }, as = (a, u, g) => {
            const {bum: b, scope: _, job: v, subTree: R, um: C, m: S, a: w} = a;
            Jn(S), Jn(w), b && Ks(b), _.stop(), v && (v.flags |= 8, ge(R, a, u, g)), C && _e(C, u), _e(() => {
                a.isUnmounted = !0
            }, u)
        }, Ce = (a, u, g, b = !1, _ = !1, v = 0) => {
            for (let R = v; R < a.length; R++) ge(a[R], u, g, b, _)
        }, y = a => {
            if (a.shapeFlag & 6) return y(a.component.subTree);
            if (a.shapeFlag & 128) return a.suspense.next();
            const u = m(a.anchor || a.el), g = u && u[Bi];
            return g ? m(g) : u
        };
    let T = !1;
    const A = (a, u, g) => {
        a == null ? u._vnode && ge(u._vnode, null, null, !0) : P(u._vnode || null, a, u, null, null, null, g), u._vnode = a, T || (T = !0, Bn(), lo(), T = !1)
    }, $ = {p: P, um: ge, m: Ie, r: Et, mt: Nt, mc: Me, pc: K, pbc: je, n: y, o: e};
    return {render: A, hydrate: void 0, createApp: dl(A)}
}

function Js({type: e, props: t}, s) {
    return s === "svg" && e === "foreignObject" || s === "mathml" && e === "annotation-xml" && t && t.encoding && t.encoding.includes("html") ? void 0 : s
}

function mt({effect: e, job: t}, s) {
    s ? (e.flags |= 32, t.flags |= 4) : (e.flags &= -33, t.flags &= -5)
}

function _l(e, t) {
    return (!e || e && !e.pendingBranch) && t && !t.persisted
}

function Ro(e, t, s = !1) {
    const n = e.children, r = t.children;
    if (H(n) && H(r)) for (let o = 0; o < n.length; o++) {
        const i = n[o];
        let l = r[o];
        l.shapeFlag & 1 && !l.dynamicChildren && ((l.patchFlag <= 0 || l.patchFlag === 32) && (l = r[o] = rt(r[o]), l.el = i.el), !s && l.patchFlag !== -2 && Ro(i, l)), l.type === Hs && l.patchFlag !== -1 && (l.el = i.el), l.type === ut && !l.el && (l.el = i.el)
    }
}

function wl(e) {
    const t = e.slice(), s = [0];
    let n, r, o, i, l;
    const c = e.length;
    for (n = 0; n < c; n++) {
        const d = e[n];
        if (d !== 0) {
            if (r = s[s.length - 1], e[r] < d) {
                t[n] = r, s.push(n);
                continue
            }
            for (o = 0, i = s.length - 1; o < i;) l = o + i >> 1, e[s[l]] < d ? o = l + 1 : i = l;
            d < e[s[o]] && (o > 0 && (t[n] = s[o - 1]), s[o] = n)
        }
    }
    for (o = s.length, i = s[o - 1]; o-- > 0;) s[o] = i, i = t[i];
    return s
}

function Po(e) {
    const t = e.subTree.component;
    if (t) return t.asyncDep && !t.asyncResolved ? t : Po(t)
}

function Jn(e) {
    if (e) for (let t = 0; t < e.length; t++) e[t].flags |= 8
}

const Sl = Symbol.for("v-scx"), El = () => Je(Sl);

function hs(e, t, s) {
    return Ao(e, t, s)
}

function Ao(e, t, s = Y) {
    const {immediate: n, deep: r, flush: o, once: i} = s, l = ue({}, s), c = t && n || !t && o !== "post";
    let d;
    if (rs) {
        if (o === "sync") {
            const h = El();
            d = h.__watcherHandles || (h.__watcherHandles = [])
        } else if (!c) {
            const h = () => {
            };
            return h.stop = Be, h.resume = Be, h.pause = Be, h
        }
    }
    const f = ae;
    l.call = (h, E, P) => Ve(h, f, E, P);
    let p = !1;
    o === "post" ? l.scheduler = h => {
        _e(h, f && f.suspense)
    } : o !== "sync" && (p = !0, l.scheduler = (h, E) => {
        E ? h() : Pn(h)
    }), l.augmentJob = h => {
        t && (h.flags |= 4), p && (h.flags |= 2, f && (h.id = f.uid, h.i = f))
    };
    const m = Li(e, t, l);
    return rs && (d ? d.push(m) : c && m()), m
}

function Cl(e, t, s) {
    const n = this.proxy, r = ne(e) ? e.includes(".") ? Oo(n, e) : () => n[e] : e.bind(n, n);
    let o;
    D(t) ? o = t : (o = t.handler, s = t);
    const i = cs(this), l = Ao(r, o.bind(n), s);
    return i(), l
}

function Oo(e, t) {
    const s = t.split(".");
    return () => {
        let n = e;
        for (let r = 0; r < s.length && n; r++) n = n[s[r]];
        return n
    }
}

const Rl = (e, t) => t === "modelValue" || t === "model-value" ? e.modelModifiers : e[`${t}Modifiers`] || e[`${Ae(t)}Modifiers`] || e[`${wt(t)}Modifiers`];

function Pl(e, t, ...s) {
    if (e.isUnmounted) return;
    const n = e.vnode.props || Y;
    let r = s;
    const o = t.startsWith("update:"), i = o && Rl(n, t.slice(7));
    i && (i.trim && (r = s.map(f => ne(f) ? f.trim() : f)), i.number && (r = s.map(ni)));
    let l, c = n[l = Vs(t)] || n[l = Vs(Ae(t))];
    !c && o && (c = n[l = Vs(wt(t))]), c && Ve(c, e, 6, r);
    const d = n[l + "Once"];
    if (d) {
        if (!e.emitted) e.emitted = {}; else if (e.emitted[l]) return;
        e.emitted[l] = !0, Ve(d, e, 6, r)
    }
}

const Al = new WeakMap;

function To(e, t, s = !1) {
    const n = s ? Al : t.emitsCache, r = n.get(e);
    if (r !== void 0) return r;
    const o = e.emits;
    let i = {}, l = !1;
    if (!D(e)) {
        const c = d => {
            const f = To(d, t, !0);
            f && (l = !0, ue(i, f))
        };
        !s && t.mixins.length && t.mixins.forEach(c), e.extends && c(e.extends), e.mixins && e.mixins.forEach(c)
    }
    return !o && !l ? (ee(e) && n.set(e, null), null) : (H(o) ? o.forEach(c => i[c] = null) : ue(i, o), ee(e) && n.set(e, i), i)
}

function Ns(e, t) {
    return !e || !As(t) ? !1 : (t = t.slice(2).replace(/Once$/, ""), z(e, t[0].toLowerCase() + t.slice(1)) || z(e, wt(t)) || z(e, t))
}

function Qn(e) {
    const {type: t, vnode: s, proxy: n, withProxy: r, propsOptions: [o], slots: i, attrs: l, emit: c, render: d, renderCache: f, props: p, data: m, setupState: h, ctx: E, inheritAttrs: P} = e,
        B = _s(e);
    let I, M;
    try {
        if (s.shapeFlag & 4) {
            const j = r || n, J = j;
            I = De(d.call(J, j, f, p, h, m, E)), M = l
        } else {
            const j = t;
            I = De(j.length > 1 ? j(p, {attrs: l, slots: i, emit: c}) : j(p, null)), M = t.props ? l : Ol(l)
        }
    } catch (j) {
        Yt.length = 0, ks(j, e, 1), I = se(ut)
    }
    let L = I;
    if (M && P !== !1) {
        const j = Object.keys(M), {shapeFlag: J} = L;
        j.length && J & 7 && (o && j.some(mn) && (M = Tl(M, o)), L = kt(L, M, !1, !0))
    }
    return s.dirs && (L = kt(L, null, !1, !0), L.dirs = L.dirs ? L.dirs.concat(s.dirs) : s.dirs), s.transition && An(L, s.transition), I = L, _s(B), I
}

const Ol = e => {
    let t;
    for (const s in e) (s === "class" || s === "style" || As(s)) && ((t || (t = {}))[s] = e[s]);
    return t
}, Tl = (e, t) => {
    const s = {};
    for (const n in e) (!mn(n) || !(n.slice(9) in t)) && (s[n] = e[n]);
    return s
};

function Ml(e, t, s) {
    const {props: n, children: r, component: o} = e, {props: i, children: l, patchFlag: c} = t, d = o.emitsOptions;
    if (t.dirs || t.transition) return !0;
    if (s && c >= 0) {
        if (c & 1024) return !0;
        if (c & 16) return n ? Yn(n, i, d) : !!i;
        if (c & 8) {
            const f = t.dynamicProps;
            for (let p = 0; p < f.length; p++) {
                const m = f[p];
                if (i[m] !== n[m] && !Ns(d, m)) return !0
            }
        }
    } else return (r || l) && (!l || !l.$stable) ? !0 : n === i ? !1 : n ? i ? Yn(n, i, d) : !0 : !!i;
    return !1
}

function Yn(e, t, s) {
    const n = Object.keys(t);
    if (n.length !== Object.keys(e).length) return !0;
    for (let r = 0; r < n.length; r++) {
        const o = n[r];
        if (t[o] !== e[o] && !Ns(s, o)) return !0
    }
    return !1
}

function jl({vnode: e, parent: t}, s) {
    for (; t;) {
        const n = t.subTree;
        if (n.suspense && n.suspense.activeBranch === e && (n.el = e.el), n === e) (e = t.vnode).el = s, t = t.parent; else break
    }
}

const Mo = e => e.__isSuspense;

function $l(e, t) {
    t && t.pendingBranch ? H(e) ? t.effects.push(...e) : t.effects.push(e) : Hi(e)
}

const ce = Symbol.for("v-fgt"), Hs = Symbol.for("v-txt"), ut = Symbol.for("v-cmt"), gs = Symbol.for("v-stc"), Yt = [];
let Ee = null;

function he(e = !1) {
    Yt.push(Ee = e ? null : [])
}

function Il() {
    Yt.pop(), Ee = Yt[Yt.length - 1] || null
}

let ns = 1;

function Es(e, t = !1) {
    ns += e, e < 0 && Ee && t && (Ee.hasOnce = !0)
}

function jo(e) {
    return e.dynamicChildren = ns > 0 ? Ee || Ot : null, Il(), ns > 0 && Ee && Ee.push(e), e
}

function be(e, t, s, n, r, o) {
    return jo(x(e, t, s, n, r, o, !0))
}

function kl(e, t, s, n, r) {
    return jo(se(e, t, s, n, r, !0))
}

function Cs(e) {
    return e ? e.__v_isVNode === !0 : !1
}

function Bt(e, t) {
    return e.type === t.type && e.key === t.key
}

const $o = ({key: e}) => e ?? null,
    ms = ({ref: e, ref_key: t, ref_for: s}) => (typeof e == "number" && (e = "" + e), e != null ? ne(e) || fe(e) || D(e) ? {
        i: Se,
        r: e,
        k: t,
        f: !!s
    } : e : null);

function x(e, t = null, s = null, n = 0, r = null, o = e === ce ? 0 : 1, i = !1, l = !1) {
    const c = {
        __v_isVNode: !0,
        __v_skip: !0,
        type: e,
        props: t,
        key: t && $o(t),
        ref: t && ms(t),
        scopeId: ao,
        slotScopeIds: null,
        children: s,
        component: null,
        suspense: null,
        ssContent: null,
        ssFallback: null,
        dirs: null,
        transition: null,
        el: null,
        anchor: null,
        target: null,
        targetStart: null,
        targetAnchor: null,
        staticCount: 0,
        shapeFlag: o,
        patchFlag: n,
        dynamicProps: r,
        dynamicChildren: null,
        appContext: null,
        ctx: Se
    };
    return l ? (Mn(c, s), o & 128 && e.normalize(c)) : s && (c.shapeFlag |= ne(s) ? 8 : 16), ns > 0 && !i && Ee && (c.patchFlag > 0 || o & 6) && c.patchFlag !== 32 && Ee.push(c), c
}

const se = Ll;

function Ll(e, t = null, s = null, n = 0, r = null, o = !1) {
    if ((!e || e === nl) && (e = ut), Cs(e)) {
        const l = kt(e, t, !0);
        return s && Mn(l, s), ns > 0 && !o && Ee && (l.shapeFlag & 6 ? Ee[Ee.indexOf(e)] = l : Ee.push(l)), l.patchFlag = -2, l
    }
    if (Jl(e) && (e = e.__vccOpts), t) {
        t = Fl(t);
        let {class: l, style: c} = t;
        l && !ne(l) && (t.class = _t(l)), ee(c) && (Rn(c) && !H(c) && (c = ue({}, c)), t.style = bn(c))
    }
    const i = ne(e) ? 1 : Mo(e) ? 128 : Vi(e) ? 64 : ee(e) ? 4 : D(e) ? 2 : 0;
    return x(e, t, s, n, r, i, o, !0)
}

function Fl(e) {
    return e ? Rn(e) || yo(e) ? ue({}, e) : e : null
}

function kt(e, t, s = !1, n = !1) {
    const {props: r, ref: o, patchFlag: i, children: l, transition: c} = e, d = t ? Hl(r || {}, t) : r, f = {
        __v_isVNode: !0,
        __v_skip: !0,
        type: e.type,
        props: d,
        key: d && $o(d),
        ref: t && t.ref ? s && o ? H(o) ? o.concat(ms(t)) : [o, ms(t)] : ms(t) : o,
        scopeId: e.scopeId,
        slotScopeIds: e.slotScopeIds,
        children: l,
        target: e.target,
        targetStart: e.targetStart,
        targetAnchor: e.targetAnchor,
        staticCount: e.staticCount,
        shapeFlag: e.shapeFlag,
        patchFlag: t && e.type !== ce ? i === -1 ? 16 : i | 16 : i,
        dynamicProps: e.dynamicProps,
        dynamicChildren: e.dynamicChildren,
        appContext: e.appContext,
        dirs: e.dirs,
        transition: c,
        component: e.component,
        suspense: e.suspense,
        ssContent: e.ssContent && kt(e.ssContent),
        ssFallback: e.ssFallback && kt(e.ssFallback),
        placeholder: e.placeholder,
        el: e.el,
        anchor: e.anchor,
        ctx: e.ctx,
        ce: e.ce
    };
    return c && n && An(f, c.clone(f)), f
}

function yt(e = " ", t = 0) {
    return se(Hs, null, e, t)
}

function lt(e, t) {
    const s = se(gs, null, e);
    return s.staticCount = t, s
}

function Nl(e = "", t = !1) {
    return t ? (he(), kl(ut, null, e)) : se(ut, null, e)
}

function De(e) {
    return e == null || typeof e == "boolean" ? se(ut) : H(e) ? se(ce, null, e.slice()) : Cs(e) ? rt(e) : se(Hs, null, String(e))
}

function rt(e) {
    return e.el === null && e.patchFlag !== -1 || e.memo ? e : kt(e)
}

function Mn(e, t) {
    let s = 0;
    const {shapeFlag: n} = e;
    if (t == null) t = null; else if (H(t)) s = 16; else if (typeof t == "object") if (n & 65) {
        const r = t.default;
        r && (r._c && (r._d = !1), Mn(e, r()), r._c && (r._d = !0));
        return
    } else {
        s = 32;
        const r = t._;
        !r && !yo(t) ? t._ctx = Se : r === 3 && Se && (Se.slots._ === 1 ? t._ = 1 : (t._ = 2, e.patchFlag |= 1024))
    } else D(t) ? (t = {default: t, _ctx: Se}, s = 32) : (t = String(t), n & 64 ? (s = 16, t = [yt(t)]) : s = 8);
    e.children = t, e.shapeFlag |= s
}

function Hl(...e) {
    const t = {};
    for (let s = 0; s < e.length; s++) {
        const n = e[s];
        for (const r in n) if (r === "class") t.class !== n.class && (t.class = _t([t.class, n.class])); else if (r === "style") t.style = bn([t.style, n.style]); else if (As(r)) {
            const o = t[r], i = n[r];
            i && o !== i && !(H(o) && o.includes(i)) && (t[r] = o ? [].concat(o, i) : i)
        } else r !== "" && (t[r] = n[r])
    }
    return t
}

function Ne(e, t, s, n = null) {
    Ve(e, t, 7, [s, n])
}

const Dl = xo();
let Bl = 0;

function Vl(e, t, s) {
    const n = e.type, r = (t ? t.appContext : e.appContext) || Dl, o = {
        uid: Bl++,
        vnode: e,
        type: n,
        parent: t,
        appContext: r,
        root: null,
        next: null,
        subTree: null,
        effect: null,
        update: null,
        job: null,
        scope: new Nr(!0),
        render: null,
        proxy: null,
        exposed: null,
        exposeProxy: null,
        withProxy: null,
        provides: t ? t.provides : Object.create(r.provides),
        ids: t ? t.ids : ["", 0, 0],
        accessCache: null,
        renderCache: [],
        components: null,
        directives: null,
        propsOptions: wo(n, r),
        emitsOptions: To(n, r),
        emit: null,
        emitted: null,
        propsDefaults: Y,
        inheritAttrs: n.inheritAttrs,
        ctx: Y,
        data: Y,
        props: Y,
        attrs: Y,
        slots: Y,
        refs: Y,
        setupState: Y,
        setupContext: null,
        suspense: s,
        suspenseId: s ? s.pendingId : 0,
        asyncDep: null,
        asyncResolved: !1,
        isMounted: !1,
        isUnmounted: !1,
        isDeactivated: !1,
        bc: null,
        c: null,
        bm: null,
        m: null,
        bu: null,
        u: null,
        um: null,
        bum: null,
        da: null,
        a: null,
        rtg: null,
        rtc: null,
        ec: null,
        sp: null
    };
    return o.ctx = {_: o}, o.root = t ? t.root : o, o.emit = Pl.bind(null, o), e.ce && e.ce(o), o
}

let ae = null;
const Kl = () => ae || Se;
let Rs, fn;
{
    const e = js(), t = (s, n) => {
        let r;
        return (r = e[s]) || (r = e[s] = []), r.push(n), o => {
            r.length > 1 ? r.forEach(i => i(o)) : r[0](o)
        }
    };
    Rs = t("__VUE_INSTANCE_SETTERS__", s => ae = s), fn = t("__VUE_SSR_SETTERS__", s => rs = s)
}
const cs = e => {
    const t = ae;
    return Rs(e), e.scope.on(), () => {
        e.scope.off(), Rs(t)
    }
}, Xn = () => {
    ae && ae.scope.off(), Rs(null)
};

function Io(e) {
    return e.vnode.shapeFlag & 4
}

let rs = !1;

function Ul(e, t = !1, s = !1) {
    t && fn(t);
    const {props: n, children: r} = e.vnode, o = Io(e);
    pl(e, n, o, t), xl(e, r, s || t);
    const i = o ? Wl(e, t) : void 0;
    return t && fn(!1), i
}

function Wl(e, t) {
    const s = e.type;
    e.accessCache = Object.create(null), e.proxy = new Proxy(e.ctx, ol);
    const {setup: n} = s;
    if (n) {
        Qe();
        const r = e.setupContext = n.length > 1 ? ql(e) : null, o = cs(e), i = ls(n, e, 0, [e.props, r]), l = Mr(i);
        if (Ye(), o(), (l || e.sp) && !Jt(e) && fo(e), l) {
            if (i.then(Xn, Xn), t) return i.then(c => {
                Zn(e, c)
            }).catch(c => {
                ks(c, e, 0)
            });
            e.asyncDep = i
        } else Zn(e, i)
    } else ko(e)
}

function Zn(e, t, s) {
    D(t) ? e.type.__ssrInlineRender ? e.ssrRender = t : e.render = t : ee(t) && (e.setupState = no(t)), ko(e)
}

function ko(e, t, s) {
    const n = e.type;
    e.render || (e.render = n.render || Be);
    {
        const r = cs(e);
        Qe();
        try {
            il(e)
        } finally {
            Ye(), r()
        }
    }
}

const zl = {
    get(e, t) {
        return le(e, "get", ""), e[t]
    }
};

function ql(e) {
    const t = s => {
        e.exposed = s || {}
    };
    return {attrs: new Proxy(e.attrs, zl), slots: e.slots, emit: e.emit, expose: t}
}

function Ds(e) {
    return e.exposed ? e.exposeProxy || (e.exposeProxy = new Proxy(no(to(e.exposed)), {
        get(t, s) {
            if (s in t) return t[s];
            if (s in Qt) return Qt[s](e)
        }, has(t, s) {
            return s in t || s in Qt
        }
    })) : e.proxy
}

function Gl(e, t = !0) {
    return D(e) ? e.displayName || e.name : e.name || t && e.__name
}

function Jl(e) {
    return D(e) && "__vccOpts" in e
}

const Re = (e, t) => Ii(e, t, rs);

function Lo(e, t, s) {
    const n = (o, i, l) => {
        Es(-1);
        try {
            return se(o, i, l)
        } finally {
            Es(1)
        }
    }, r = arguments.length;
    return r === 2 ? ee(t) && !H(t) ? Cs(t) ? n(e, null, [t]) : n(e, t) : n(e, null, t) : (r > 3 ? s = Array.prototype.slice.call(arguments, 2) : r === 3 && Cs(s) && (s = [s]), n(e, t, s))
}

const Ql = "3.5.21";
/**
 * @vue/runtime-dom v3.5.21
 * (c) 2018-present Yuxi (Evan) You and Vue contributors
 * @license MIT
 **/let un;
const er = typeof window < "u" && window.trustedTypes;
if (er) try {
    un = er.createPolicy("vue", {createHTML: e => e})
} catch {
}
const Fo = un ? e => un.createHTML(e) : e => e, Yl = "http://www.w3.org/2000/svg",
    Xl = "http://www.w3.org/1998/Math/MathML", ze = typeof document < "u" ? document : null,
    tr = ze && ze.createElement("template"), Zl = {
        insert: (e, t, s) => {
            t.insertBefore(e, s || null)
        },
        remove: e => {
            const t = e.parentNode;
            t && t.removeChild(e)
        },
        createElement: (e, t, s, n) => {
            const r = t === "svg" ? ze.createElementNS(Yl, e) : t === "mathml" ? ze.createElementNS(Xl, e) : s ? ze.createElement(e, {is: s}) : ze.createElement(e);
            return e === "select" && n && n.multiple != null && r.setAttribute("multiple", n.multiple), r
        },
        createText: e => ze.createTextNode(e),
        createComment: e => ze.createComment(e),
        setText: (e, t) => {
            e.nodeValue = t
        },
        setElementText: (e, t) => {
            e.textContent = t
        },
        parentNode: e => e.parentNode,
        nextSibling: e => e.nextSibling,
        querySelector: e => ze.querySelector(e),
        setScopeId(e, t) {
            e.setAttribute(t, "")
        },
        insertStaticContent(e, t, s, n, r, o) {
            const i = s ? s.previousSibling : t.lastChild;
            if (r && (r === o || r.nextSibling)) for (; t.insertBefore(r.cloneNode(!0), s), !(r === o || !(r = r.nextSibling));) ; else {
                tr.innerHTML = Fo(n === "svg" ? `<svg>${e}</svg>` : n === "mathml" ? `<math>${e}</math>` : e);
                const l = tr.content;
                if (n === "svg" || n === "mathml") {
                    const c = l.firstChild;
                    for (; c.firstChild;) l.appendChild(c.firstChild);
                    l.removeChild(c)
                }
                t.insertBefore(l, s)
            }
            return [i ? i.nextSibling : t.firstChild, s ? s.previousSibling : t.lastChild]
        }
    }, ec = Symbol("_vtc");

function tc(e, t, s) {
    const n = e[ec];
    n && (t = (t ? [t, ...n] : [...n]).join(" ")), t == null ? e.removeAttribute("class") : s ? e.setAttribute("class", t) : e.className = t
}

const Ps = Symbol("_vod"), No = Symbol("_vsh"), sc = {
    name: "show", beforeMount(e, {value: t}, {transition: s}) {
        e[Ps] = e.style.display === "none" ? "" : e.style.display, s && t ? s.beforeEnter(e) : Vt(e, t)
    }, mounted(e, {value: t}, {transition: s}) {
        s && t && s.enter(e)
    }, updated(e, {value: t, oldValue: s}, {transition: n}) {
        !t != !s && (n ? t ? (n.beforeEnter(e), Vt(e, !0), n.enter(e)) : n.leave(e, () => {
            Vt(e, !1)
        }) : Vt(e, t))
    }, beforeUnmount(e, {value: t}) {
        Vt(e, t)
    }
};

function Vt(e, t) {
    e.style.display = t ? e[Ps] : "none", e[No] = !t
}

const nc = Symbol(""), rc = /(?:^|;)\s*display\s*:/;

function oc(e, t, s) {
    const n = e.style, r = ne(s);
    let o = !1;
    if (s && !r) {
        if (t) if (ne(t)) for (const i of t.split(";")) {
            const l = i.slice(0, i.indexOf(":")).trim();
            s[l] == null && xs(n, l, "")
        } else for (const i in t) s[i] == null && xs(n, i, "");
        for (const i in s) i === "display" && (o = !0), xs(n, i, s[i])
    } else if (r) {
        if (t !== s) {
            const i = n[nc];
            i && (s += ";" + i), n.cssText = s, o = rc.test(s)
        }
    } else t && e.removeAttribute("style");
    Ps in e && (e[Ps] = o ? n.display : "", e[No] && (n.display = "none"))
}

const sr = /\s*!important$/;

function xs(e, t, s) {
    if (H(s)) s.forEach(n => xs(e, t, n)); else if (s == null && (s = ""), t.startsWith("--")) e.setProperty(t, s); else {
        const n = ic(e, t);
        sr.test(s) ? e.setProperty(wt(n), s.replace(sr, ""), "important") : e[n] = s
    }
}

const nr = ["Webkit", "Moz", "ms"], Qs = {};

function ic(e, t) {
    const s = Qs[t];
    if (s) return s;
    let n = Ae(t);
    if (n !== "filter" && n in e) return Qs[t] = n;
    n = Ms(n);
    for (let r = 0; r < nr.length; r++) {
        const o = nr[r] + n;
        if (o in e) return Qs[t] = o
    }
    return t
}

const rr = "http://www.w3.org/1999/xlink";

function or(e, t, s, n, r, o = ai(t)) {
    n && t.startsWith("xlink:") ? s == null ? e.removeAttributeNS(rr, t.slice(6, t.length)) : e.setAttributeNS(rr, t, s) : s == null || o && !kr(s) ? e.removeAttribute(t) : e.setAttribute(t, o ? "" : dt(s) ? String(s) : s)
}

function ir(e, t, s, n, r) {
    if (t === "innerHTML" || t === "textContent") {
        s != null && (e[t] = t === "innerHTML" ? Fo(s) : s);
        return
    }
    const o = e.tagName;
    if (t === "value" && o !== "PROGRESS" && !o.includes("-")) {
        const l = o === "OPTION" ? e.getAttribute("value") || "" : e.value,
            c = s == null ? e.type === "checkbox" ? "on" : "" : String(s);
        (l !== c || !("_value" in e)) && (e.value = c), s == null && e.removeAttribute(t), e._value = s;
        return
    }
    let i = !1;
    if (s === "" || s == null) {
        const l = typeof e[t];
        l === "boolean" ? s = kr(s) : s == null && l === "string" ? (s = "", i = !0) : l === "number" && (s = 0, i = !0)
    }
    try {
        e[t] = s
    } catch {
    }
    i && e.removeAttribute(r || t)
}

function lc(e, t, s, n) {
    e.addEventListener(t, s, n)
}

function cc(e, t, s, n) {
    e.removeEventListener(t, s, n)
}

const lr = Symbol("_vei");

function ac(e, t, s, n, r = null) {
    const o = e[lr] || (e[lr] = {}), i = o[t];
    if (n && i) i.value = n; else {
        const [l, c] = fc(t);
        if (n) {
            const d = o[t] = pc(n, r);
            lc(e, l, d, c)
        } else i && (cc(e, l, i, c), o[t] = void 0)
    }
}

const cr = /(?:Once|Passive|Capture)$/;

function fc(e) {
    let t;
    if (cr.test(e)) {
        t = {};
        let n;
        for (; n = e.match(cr);) e = e.slice(0, e.length - n[0].length), t[n[0].toLowerCase()] = !0
    }
    return [e[2] === ":" ? e.slice(3) : wt(e.slice(2)), t]
}

let Ys = 0;
const uc = Promise.resolve(), dc = () => Ys || (uc.then(() => Ys = 0), Ys = Date.now());

function pc(e, t) {
    const s = n => {
        if (!n._vts) n._vts = Date.now(); else if (n._vts <= s.attached) return;
        Ve(hc(n, s.value), t, 5, [n])
    };
    return s.value = e, s.attached = dc(), s
}

function hc(e, t) {
    if (H(t)) {
        const s = e.stopImmediatePropagation;
        return e.stopImmediatePropagation = () => {
            s.call(e), e._stopped = !0
        }, t.map(n => r => !r._stopped && n && n(r))
    } else return t
}

const ar = e => e.charCodeAt(0) === 111 && e.charCodeAt(1) === 110 && e.charCodeAt(2) > 96 && e.charCodeAt(2) < 123,
    gc = (e, t, s, n, r, o) => {
        const i = r === "svg";
        t === "class" ? tc(e, n, i) : t === "style" ? oc(e, s, n) : As(t) ? mn(t) || ac(e, t, s, n, o) : (t[0] === "." ? (t = t.slice(1), !0) : t[0] === "^" ? (t = t.slice(1), !1) : mc(e, t, n, i)) ? (ir(e, t, n), !e.tagName.includes("-") && (t === "value" || t === "checked" || t === "selected") && or(e, t, n, i, o, t !== "value")) : e._isVueCE && (/[A-Z]/.test(t) || !ne(n)) ? ir(e, Ae(t), n, o, t) : (t === "true-value" ? e._trueValue = n : t === "false-value" && (e._falseValue = n), or(e, t, n, i))
    };

function mc(e, t, s, n) {
    if (n) return !!(t === "innerHTML" || t === "textContent" || t in e && ar(t) && D(s));
    if (t === "spellcheck" || t === "draggable" || t === "translate" || t === "autocorrect" || t === "form" || t === "list" && e.tagName === "INPUT" || t === "type" && e.tagName === "TEXTAREA") return !1;
    if (t === "width" || t === "height") {
        const r = e.tagName;
        if (r === "IMG" || r === "VIDEO" || r === "CANVAS" || r === "SOURCE") return !1
    }
    return ar(t) && ne(s) ? !1 : t in e
}

const xc = ["ctrl", "shift", "alt", "meta"], vc = {
    stop: e => e.stopPropagation(),
    prevent: e => e.preventDefault(),
    self: e => e.target !== e.currentTarget,
    ctrl: e => !e.ctrlKey,
    shift: e => !e.shiftKey,
    alt: e => !e.altKey,
    meta: e => !e.metaKey,
    left: e => "button" in e && e.button !== 0,
    middle: e => "button" in e && e.button !== 1,
    right: e => "button" in e && e.button !== 2,
    exact: (e, t) => xc.some(s => e[`${s}Key`] && !t.includes(s))
}, bc = (e, t) => {
    const s = e._withMods || (e._withMods = {}), n = t.join(".");
    return s[n] || (s[n] = (r, ...o) => {
        for (let i = 0; i < t.length; i++) {
            const l = vc[t[i]];
            if (l && l(r, t)) return
        }
        return e(r, ...o)
    })
}, yc = ue({patchProp: gc}, Zl);
let fr;

function _c() {
    return fr || (fr = bl(yc))
}

const wc = (...e) => {
    const t = _c().createApp(...e), {mount: s} = t;
    return t.mount = n => {
        const r = Ec(n);
        if (!r) return;
        const o = t._component;
        !D(o) && !o.render && !o.template && (o.template = r.innerHTML), r.nodeType === 1 && (r.textContent = "");
        const i = s(r, !1, Sc(r));
        return r instanceof Element && (r.removeAttribute("v-cloak"), r.setAttribute("data-v-app", "")), i
    }, t
};

function Sc(e) {
    if (e instanceof SVGElement) return "svg";
    if (typeof MathMLElement == "function" && e instanceof MathMLElement) return "mathml"
}

function Ec(e) {
    return ne(e) ? document.querySelector(e) : e
}/*!
 * pinia v2.3.1
 * (c) 2025 Eduardo San Martin Morote
 * @license MIT
 */
const Cc = Symbol();
var ur;
(function (e) {
    e.direct = "direct", e.patchObject = "patch object", e.patchFunction = "patch function"
})(ur || (ur = {}));

function Rc() {
    const e = fi(!0), t = e.run(() => it({}));
    let s = [], n = [];
    const r = to({
        install(o) {
            r._a = o, o.provide(Cc, r), o.config.globalProperties.$pinia = r, n.forEach(i => s.push(i)), n = []
        }, use(o) {
            return this._a ? s.push(o) : n.push(o), this
        }, _p: s, _a: null, _e: e, _s: new Map, state: t
    });
    return r
}/*!
  * vue-router v4.5.1
  * (c) 2025 Eduardo San Martin Morote
  * @license MIT
  */
const At = typeof document < "u";

function Ho(e) {
    return typeof e == "object" || "displayName" in e || "props" in e || "__vccOpts" in e
}

function Pc(e) {
    return e.__esModule || e[Symbol.toStringTag] === "Module" || e.default && Ho(e.default)
}

const U = Object.assign;

function Xs(e, t) {
    const s = {};
    for (const n in t) {
        const r = t[n];
        s[n] = Te(r) ? r.map(e) : e(r)
    }
    return s
}

const Xt = () => {
    }, Te = Array.isArray, Do = /#/g, Ac = /&/g, Oc = /\//g, Tc = /=/g, Mc = /\?/g, Bo = /\+/g, jc = /%5B/g, $c = /%5D/g,
    Vo = /%5E/g, Ic = /%60/g, Ko = /%7B/g, kc = /%7C/g, Uo = /%7D/g, Lc = /%20/g;

function jn(e) {
    return encodeURI("" + e).replace(kc, "|").replace(jc, "[").replace($c, "]")
}

function Fc(e) {
    return jn(e).replace(Ko, "{").replace(Uo, "}").replace(Vo, "^")
}

function dn(e) {
    return jn(e).replace(Bo, "%2B").replace(Lc, "+").replace(Do, "%23").replace(Ac, "%26").replace(Ic, "`").replace(Ko, "{").replace(Uo, "}").replace(Vo, "^")
}

function Nc(e) {
    return dn(e).replace(Tc, "%3D")
}

function Hc(e) {
    return jn(e).replace(Do, "%23").replace(Mc, "%3F")
}

function Dc(e) {
    return e == null ? "" : Hc(e).replace(Oc, "%2F")
}

function os(e) {
    try {
        return decodeURIComponent("" + e)
    } catch {
    }
    return "" + e
}

const Bc = /\/$/, Vc = e => e.replace(Bc, "");

function Zs(e, t, s = "/") {
    let n, r = {}, o = "", i = "";
    const l = t.indexOf("#");
    let c = t.indexOf("?");
    return l < c && l >= 0 && (c = -1), c > -1 && (n = t.slice(0, c), o = t.slice(c + 1, l > -1 ? l : t.length), r = e(o)), l > -1 && (n = n || t.slice(0, l), i = t.slice(l, t.length)), n = zc(n ?? t, s), {
        fullPath: n + (o && "?") + o + i,
        path: n,
        query: r,
        hash: os(i)
    }
}

function Kc(e, t) {
    const s = t.query ? e(t.query) : "";
    return t.path + (s && "?") + s + (t.hash || "")
}

function dr(e, t) {
    return !t || !e.toLowerCase().startsWith(t.toLowerCase()) ? e : e.slice(t.length) || "/"
}

function Uc(e, t, s) {
    const n = t.matched.length - 1, r = s.matched.length - 1;
    return n > -1 && n === r && Lt(t.matched[n], s.matched[r]) && Wo(t.params, s.params) && e(t.query) === e(s.query) && t.hash === s.hash
}

function Lt(e, t) {
    return (e.aliasOf || e) === (t.aliasOf || t)
}

function Wo(e, t) {
    if (Object.keys(e).length !== Object.keys(t).length) return !1;
    for (const s in e) if (!Wc(e[s], t[s])) return !1;
    return !0
}

function Wc(e, t) {
    return Te(e) ? pr(e, t) : Te(t) ? pr(t, e) : e === t
}

function pr(e, t) {
    return Te(t) ? e.length === t.length && e.every((s, n) => s === t[n]) : e.length === 1 && e[0] === t
}

function zc(e, t) {
    if (e.startsWith("/")) return e;
    if (!e) return t;
    const s = t.split("/"), n = e.split("/"), r = n[n.length - 1];
    (r === ".." || r === ".") && n.push("");
    let o = s.length - 1, i, l;
    for (i = 0; i < n.length; i++) if (l = n[i], l !== ".") if (l === "..") o > 1 && o--; else break;
    return s.slice(0, o).join("/") + "/" + n.slice(i).join("/")
}

const st = {
    path: "/",
    name: void 0,
    params: {},
    query: {},
    hash: "",
    fullPath: "/",
    matched: [],
    meta: {},
    redirectedFrom: void 0
};
var is;
(function (e) {
    e.pop = "pop", e.push = "push"
})(is || (is = {}));
var Zt;
(function (e) {
    e.back = "back", e.forward = "forward", e.unknown = ""
})(Zt || (Zt = {}));

function qc(e) {
    if (!e) if (At) {
        const t = document.querySelector("base");
        e = t && t.getAttribute("href") || "/", e = e.replace(/^\w+:\/\/[^\/]+/, "")
    } else e = "/";
    return e[0] !== "/" && e[0] !== "#" && (e = "/" + e), Vc(e)
}

const Gc = /^[^#]+#/;

function Jc(e, t) {
    return e.replace(Gc, "#") + t
}

function Qc(e, t) {
    const s = document.documentElement.getBoundingClientRect(), n = e.getBoundingClientRect();
    return {behavior: t.behavior, left: n.left - s.left - (t.left || 0), top: n.top - s.top - (t.top || 0)}
}

const Bs = () => ({left: window.scrollX, top: window.scrollY});

function Yc(e) {
    let t;
    if ("el" in e) {
        const s = e.el, n = typeof s == "string" && s.startsWith("#"),
            r = typeof s == "string" ? n ? document.getElementById(s.slice(1)) : document.querySelector(s) : s;
        if (!r) return;
        t = Qc(r, e)
    } else t = e;
    "scrollBehavior" in document.documentElement.style ? window.scrollTo(t) : window.scrollTo(t.left != null ? t.left : window.scrollX, t.top != null ? t.top : window.scrollY)
}

function hr(e, t) {
    return (history.state ? history.state.position - t : -1) + e
}

const pn = new Map;

function Xc(e, t) {
    pn.set(e, t)
}

function Zc(e) {
    const t = pn.get(e);
    return pn.delete(e), t
}

let ea = () => location.protocol + "//" + location.host;

function zo(e, t) {
    const {pathname: s, search: n, hash: r} = t, o = e.indexOf("#");
    if (o > -1) {
        let l = r.includes(e.slice(o)) ? e.slice(o).length : 1, c = r.slice(l);
        return c[0] !== "/" && (c = "/" + c), dr(c, "")
    }
    return dr(s, e) + n + r
}

function ta(e, t, s, n) {
    let r = [], o = [], i = null;
    const l = ({state: m}) => {
        const h = zo(e, location), E = s.value, P = t.value;
        let B = 0;
        if (m) {
            if (s.value = h, t.value = m, i && i === E) {
                i = null;
                return
            }
            B = P ? m.position - P.position : 0
        } else n(h);
        r.forEach(I => {
            I(s.value, E, {delta: B, type: is.pop, direction: B ? B > 0 ? Zt.forward : Zt.back : Zt.unknown})
        })
    };

    function c() {
        i = s.value
    }

    function d(m) {
        r.push(m);
        const h = () => {
            const E = r.indexOf(m);
            E > -1 && r.splice(E, 1)
        };
        return o.push(h), h
    }

    function f() {
        const {history: m} = window;
        m.state && m.replaceState(U({}, m.state, {scroll: Bs()}), "")
    }

    function p() {
        for (const m of o) m();
        o = [], window.removeEventListener("popstate", l), window.removeEventListener("beforeunload", f)
    }

    return window.addEventListener("popstate", l), window.addEventListener("beforeunload", f, {passive: !0}), {
        pauseListeners: c,
        listen: d,
        destroy: p
    }
}

function gr(e, t, s, n = !1, r = !1) {
    return {back: e, current: t, forward: s, replaced: n, position: window.history.length, scroll: r ? Bs() : null}
}

function sa(e) {
    const {history: t, location: s} = window, n = {value: zo(e, s)}, r = {value: t.state};
    r.value || o(n.value, {
        back: null,
        current: n.value,
        forward: null,
        position: t.length - 1,
        replaced: !0,
        scroll: null
    }, !0);

    function o(c, d, f) {
        const p = e.indexOf("#"),
            m = p > -1 ? (s.host && document.querySelector("base") ? e : e.slice(p)) + c : ea() + e + c;
        try {
            t[f ? "replaceState" : "pushState"](d, "", m), r.value = d
        } catch (h) {
            console.error(h), s[f ? "replace" : "assign"](m)
        }
    }

    function i(c, d) {
        const f = U({}, t.state, gr(r.value.back, c, r.value.forward, !0), d, {position: r.value.position});
        o(c, f, !0), n.value = c
    }

    function l(c, d) {
        const f = U({}, r.value, t.state, {forward: c, scroll: Bs()});
        o(f.current, f, !0);
        const p = U({}, gr(n.value, c, null), {position: f.position + 1}, d);
        o(c, p, !1), n.value = c
    }

    return {location: n, state: r, push: l, replace: i}
}

function na(e) {
    e = qc(e);
    const t = sa(e), s = ta(e, t.state, t.location, t.replace);

    function n(o, i = !0) {
        i || s.pauseListeners(), history.go(o)
    }

    const r = U({location: "", base: e, go: n, createHref: Jc.bind(null, e)}, t, s);
    return Object.defineProperty(r, "location", {
        enumerable: !0,
        get: () => t.location.value
    }), Object.defineProperty(r, "state", {enumerable: !0, get: () => t.state.value}), r
}

function ra(e) {
    return e = location.host ? e || location.pathname + location.search : "", e.includes("#") || (e += "#"), na(e)
}

function oa(e) {
    return typeof e == "string" || e && typeof e == "object"
}

function qo(e) {
    return typeof e == "string" || typeof e == "symbol"
}

const Go = Symbol("");
var mr;
(function (e) {
    e[e.aborted = 4] = "aborted", e[e.cancelled = 8] = "cancelled", e[e.duplicated = 16] = "duplicated"
})(mr || (mr = {}));

function Ft(e, t) {
    return U(new Error, {type: e, [Go]: !0}, t)
}

function We(e, t) {
    return e instanceof Error && Go in e && (t == null || !!(e.type & t))
}

const xr = "[^/]+?", ia = {sensitive: !1, strict: !1, start: !0, end: !0}, la = /[.+*?^${}()[\]/\\]/g;

function ca(e, t) {
    const s = U({}, ia, t), n = [];
    let r = s.start ? "^" : "";
    const o = [];
    for (const d of e) {
        const f = d.length ? [] : [90];
        s.strict && !d.length && (r += "/");
        for (let p = 0; p < d.length; p++) {
            const m = d[p];
            let h = 40 + (s.sensitive ? .25 : 0);
            if (m.type === 0) p || (r += "/"), r += m.value.replace(la, "\\$&"), h += 40; else if (m.type === 1) {
                const {value: E, repeatable: P, optional: B, regexp: I} = m;
                o.push({name: E, repeatable: P, optional: B});
                const M = I || xr;
                if (M !== xr) {
                    h += 10;
                    try {
                        new RegExp(`(${M})`)
                    } catch (j) {
                        throw new Error(`Invalid custom RegExp for param "${E}" (${M}): ` + j.message)
                    }
                }
                let L = P ? `((?:${M})(?:/(?:${M}))*)` : `(${M})`;
                p || (L = B && d.length < 2 ? `(?:/${L})` : "/" + L), B && (L += "?"), r += L, h += 20, B && (h += -8), P && (h += -20), M === ".*" && (h += -50)
            }
            f.push(h)
        }
        n.push(f)
    }
    if (s.strict && s.end) {
        const d = n.length - 1;
        n[d][n[d].length - 1] += .7000000000000001
    }
    s.strict || (r += "/?"), s.end ? r += "$" : s.strict && !r.endsWith("/") && (r += "(?:/|$)");
    const i = new RegExp(r, s.sensitive ? "" : "i");

    function l(d) {
        const f = d.match(i), p = {};
        if (!f) return null;
        for (let m = 1; m < f.length; m++) {
            const h = f[m] || "", E = o[m - 1];
            p[E.name] = h && E.repeatable ? h.split("/") : h
        }
        return p
    }

    function c(d) {
        let f = "", p = !1;
        for (const m of e) {
            (!p || !f.endsWith("/")) && (f += "/"), p = !1;
            for (const h of m) if (h.type === 0) f += h.value; else if (h.type === 1) {
                const {value: E, repeatable: P, optional: B} = h, I = E in d ? d[E] : "";
                if (Te(I) && !P) throw new Error(`Provided param "${E}" is an array but it is not repeatable (* or + modifiers)`);
                const M = Te(I) ? I.join("/") : I;
                if (!M) if (B) m.length < 2 && (f.endsWith("/") ? f = f.slice(0, -1) : p = !0); else throw new Error(`Missing required param "${E}"`);
                f += M
            }
        }
        return f || "/"
    }

    return {re: i, score: n, keys: o, parse: l, stringify: c}
}

function aa(e, t) {
    let s = 0;
    for (; s < e.length && s < t.length;) {
        const n = t[s] - e[s];
        if (n) return n;
        s++
    }
    return e.length < t.length ? e.length === 1 && e[0] === 80 ? -1 : 1 : e.length > t.length ? t.length === 1 && t[0] === 80 ? 1 : -1 : 0
}

function Jo(e, t) {
    let s = 0;
    const n = e.score, r = t.score;
    for (; s < n.length && s < r.length;) {
        const o = aa(n[s], r[s]);
        if (o) return o;
        s++
    }
    if (Math.abs(r.length - n.length) === 1) {
        if (vr(n)) return 1;
        if (vr(r)) return -1
    }
    return r.length - n.length
}

function vr(e) {
    const t = e[e.length - 1];
    return e.length > 0 && t[t.length - 1] < 0
}

const fa = {type: 0, value: ""}, ua = /[a-zA-Z0-9_]/;

function da(e) {
    if (!e) return [[]];
    if (e === "/") return [[fa]];
    if (!e.startsWith("/")) throw new Error(`Invalid path "${e}"`);

    function t(h) {
        throw new Error(`ERR (${s})/"${d}": ${h}`)
    }

    let s = 0, n = s;
    const r = [];
    let o;

    function i() {
        o && r.push(o), o = []
    }

    let l = 0, c, d = "", f = "";

    function p() {
        d && (s === 0 ? o.push({
            type: 0,
            value: d
        }) : s === 1 || s === 2 || s === 3 ? (o.length > 1 && (c === "*" || c === "+") && t(`A repeatable param (${d}) must be alone in its segment. eg: '/:ids+.`), o.push({
            type: 1,
            value: d,
            regexp: f,
            repeatable: c === "*" || c === "+",
            optional: c === "*" || c === "?"
        })) : t("Invalid state to consume buffer"), d = "")
    }

    function m() {
        d += c
    }

    for (; l < e.length;) {
        if (c = e[l++], c === "\\" && s !== 2) {
            n = s, s = 4;
            continue
        }
        switch (s) {
            case 0:
                c === "/" ? (d && p(), i()) : c === ":" ? (p(), s = 1) : m();
                break;
            case 4:
                m(), s = n;
                break;
            case 1:
                c === "(" ? s = 2 : ua.test(c) ? m() : (p(), s = 0, c !== "*" && c !== "?" && c !== "+" && l--);
                break;
            case 2:
                c === ")" ? f[f.length - 1] == "\\" ? f = f.slice(0, -1) + c : s = 3 : f += c;
                break;
            case 3:
                p(), s = 0, c !== "*" && c !== "?" && c !== "+" && l--, f = "";
                break;
            default:
                t("Unknown state");
                break
        }
    }
    return s === 2 && t(`Unfinished custom RegExp for param "${d}"`), p(), i(), r
}

function pa(e, t, s) {
    const n = ca(da(e.path), s), r = U(n, {record: e, parent: t, children: [], alias: []});
    return t && !r.record.aliasOf == !t.record.aliasOf && t.children.push(r), r
}

function ha(e, t) {
    const s = [], n = new Map;
    t = wr({strict: !1, end: !0, sensitive: !1}, t);

    function r(p) {
        return n.get(p)
    }

    function o(p, m, h) {
        const E = !h, P = yr(p);
        P.aliasOf = h && h.record;
        const B = wr(t, p), I = [P];
        if ("alias" in p) {
            const j = typeof p.alias == "string" ? [p.alias] : p.alias;
            for (const J of j) I.push(yr(U({}, P, {
                components: h ? h.record.components : P.components,
                path: J,
                aliasOf: h ? h.record : P
            })))
        }
        let M, L;
        for (const j of I) {
            const {path: J} = j;
            if (m && J[0] !== "/") {
                const oe = m.record.path, te = oe[oe.length - 1] === "/" ? "" : "/";
                j.path = m.record.path + (J && te + J)
            }
            if (M = pa(j, m, B), h ? h.alias.push(M) : (L = L || M, L !== M && L.alias.push(M), E && p.name && !_r(M) && i(p.name)), Qo(M) && c(M), P.children) {
                const oe = P.children;
                for (let te = 0; te < oe.length; te++) o(oe[te], M, h && h.children[te])
            }
            h = h || M
        }
        return L ? () => {
            i(L)
        } : Xt
    }

    function i(p) {
        if (qo(p)) {
            const m = n.get(p);
            m && (n.delete(p), s.splice(s.indexOf(m), 1), m.children.forEach(i), m.alias.forEach(i))
        } else {
            const m = s.indexOf(p);
            m > -1 && (s.splice(m, 1), p.record.name && n.delete(p.record.name), p.children.forEach(i), p.alias.forEach(i))
        }
    }

    function l() {
        return s
    }

    function c(p) {
        const m = xa(p, s);
        s.splice(m, 0, p), p.record.name && !_r(p) && n.set(p.record.name, p)
    }

    function d(p, m) {
        let h, E = {}, P, B;
        if ("name" in p && p.name) {
            if (h = n.get(p.name), !h) throw Ft(1, {location: p});
            B = h.record.name, E = U(br(m.params, h.keys.filter(L => !L.optional).concat(h.parent ? h.parent.keys.filter(L => L.optional) : []).map(L => L.name)), p.params && br(p.params, h.keys.map(L => L.name))), P = h.stringify(E)
        } else if (p.path != null) P = p.path, h = s.find(L => L.re.test(P)), h && (E = h.parse(P), B = h.record.name); else {
            if (h = m.name ? n.get(m.name) : s.find(L => L.re.test(m.path)), !h) throw Ft(1, {
                location: p,
                currentLocation: m
            });
            B = h.record.name, E = U({}, m.params, p.params), P = h.stringify(E)
        }
        const I = [];
        let M = h;
        for (; M;) I.unshift(M.record), M = M.parent;
        return {name: B, path: P, params: E, matched: I, meta: ma(I)}
    }

    e.forEach(p => o(p));

    function f() {
        s.length = 0, n.clear()
    }

    return {addRoute: o, resolve: d, removeRoute: i, clearRoutes: f, getRoutes: l, getRecordMatcher: r}
}

function br(e, t) {
    const s = {};
    for (const n of t) n in e && (s[n] = e[n]);
    return s
}

function yr(e) {
    const t = {
        path: e.path,
        redirect: e.redirect,
        name: e.name,
        meta: e.meta || {},
        aliasOf: e.aliasOf,
        beforeEnter: e.beforeEnter,
        props: ga(e),
        children: e.children || [],
        instances: {},
        leaveGuards: new Set,
        updateGuards: new Set,
        enterCallbacks: {},
        components: "components" in e ? e.components || null : e.component && {default: e.component}
    };
    return Object.defineProperty(t, "mods", {value: {}}), t
}

function ga(e) {
    const t = {}, s = e.props || !1;
    if ("component" in e) t.default = s; else for (const n in e.components) t[n] = typeof s == "object" ? s[n] : s;
    return t
}

function _r(e) {
    for (; e;) {
        if (e.record.aliasOf) return !0;
        e = e.parent
    }
    return !1
}

function ma(e) {
    return e.reduce((t, s) => U(t, s.meta), {})
}

function wr(e, t) {
    const s = {};
    for (const n in e) s[n] = n in t ? t[n] : e[n];
    return s
}

function xa(e, t) {
    let s = 0, n = t.length;
    for (; s !== n;) {
        const o = s + n >> 1;
        Jo(e, t[o]) < 0 ? n = o : s = o + 1
    }
    const r = va(e);
    return r && (n = t.lastIndexOf(r, n - 1)), n
}

function va(e) {
    let t = e;
    for (; t = t.parent;) if (Qo(t) && Jo(e, t) === 0) return t
}

function Qo({record: e}) {
    return !!(e.name || e.components && Object.keys(e.components).length || e.redirect)
}

function ba(e) {
    const t = {};
    if (e === "" || e === "?") return t;
    const n = (e[0] === "?" ? e.slice(1) : e).split("&");
    for (let r = 0; r < n.length; ++r) {
        const o = n[r].replace(Bo, " "), i = o.indexOf("="), l = os(i < 0 ? o : o.slice(0, i)),
            c = i < 0 ? null : os(o.slice(i + 1));
        if (l in t) {
            let d = t[l];
            Te(d) || (d = t[l] = [d]), d.push(c)
        } else t[l] = c
    }
    return t
}

function Sr(e) {
    let t = "";
    for (let s in e) {
        const n = e[s];
        if (s = Nc(s), n == null) {
            n !== void 0 && (t += (t.length ? "&" : "") + s);
            continue
        }
        (Te(n) ? n.map(o => o && dn(o)) : [n && dn(n)]).forEach(o => {
            o !== void 0 && (t += (t.length ? "&" : "") + s, o != null && (t += "=" + o))
        })
    }
    return t
}

function ya(e) {
    const t = {};
    for (const s in e) {
        const n = e[s];
        n !== void 0 && (t[s] = Te(n) ? n.map(r => r == null ? null : "" + r) : n == null ? n : "" + n)
    }
    return t
}

const _a = Symbol(""), Er = Symbol(""), $n = Symbol(""), Yo = Symbol(""), hn = Symbol("");

function Kt() {
    let e = [];

    function t(n) {
        return e.push(n), () => {
            const r = e.indexOf(n);
            r > -1 && e.splice(r, 1)
        }
    }

    function s() {
        e = []
    }

    return {add: t, list: () => e.slice(), reset: s}
}

function ot(e, t, s, n, r, o = i => i()) {
    const i = n && (n.enterCallbacks[r] = n.enterCallbacks[r] || []);
    return () => new Promise((l, c) => {
        const d = m => {
            m === !1 ? c(Ft(4, {from: s, to: t})) : m instanceof Error ? c(m) : oa(m) ? c(Ft(2, {
                from: t,
                to: m
            })) : (i && n.enterCallbacks[r] === i && typeof m == "function" && i.push(m), l())
        }, f = o(() => e.call(n && n.instances[r], t, s, d));
        let p = Promise.resolve(f);
        e.length < 3 && (p = p.then(d)), p.catch(m => c(m))
    })
}

function en(e, t, s, n, r = o => o()) {
    const o = [];
    for (const i of e) for (const l in i.components) {
        let c = i.components[l];
        if (!(t !== "beforeRouteEnter" && !i.instances[l])) if (Ho(c)) {
            const f = (c.__vccOpts || c)[t];
            f && o.push(ot(f, s, n, i, l, r))
        } else {
            let d = c();
            o.push(() => d.then(f => {
                if (!f) throw new Error(`Couldn't resolve component "${l}" at "${i.path}"`);
                const p = Pc(f) ? f.default : f;
                i.mods[l] = f, i.components[l] = p;
                const h = (p.__vccOpts || p)[t];
                return h && ot(h, s, n, i, l, r)()
            }))
        }
    }
    return o
}

function Cr(e) {
    const t = Je($n), s = Je(Yo), n = Re(() => {
            const c = jt(e.to);
            return t.resolve(c)
        }), r = Re(() => {
            const {matched: c} = n.value, {length: d} = c, f = c[d - 1], p = s.matched;
            if (!f || !p.length) return -1;
            const m = p.findIndex(Lt.bind(null, f));
            if (m > -1) return m;
            const h = Rr(c[d - 2]);
            return d > 1 && Rr(f) === h && p[p.length - 1].path !== h ? p.findIndex(Lt.bind(null, c[d - 2])) : m
        }), o = Re(() => r.value > -1 && Ra(s.params, n.value.params)),
        i = Re(() => r.value > -1 && r.value === s.matched.length - 1 && Wo(s.params, n.value.params));

    function l(c = {}) {
        if (Ca(c)) {
            const d = t[jt(e.replace) ? "replace" : "push"](jt(e.to)).catch(Xt);
            return e.viewTransition && typeof document < "u" && "startViewTransition" in document && document.startViewTransition(() => d), d
        }
        return Promise.resolve()
    }

    return {route: n, href: Re(() => n.value.href), isActive: o, isExactActive: i, navigate: l}
}

function wa(e) {
    return e.length === 1 ? e[0] : e
}

const Sa = pt({
    name: "RouterLink",
    compatConfig: {MODE: 3},
    props: {
        to: {type: [String, Object], required: !0},
        replace: Boolean,
        activeClass: String,
        exactActiveClass: String,
        custom: Boolean,
        ariaCurrentValue: {type: String, default: "page"},
        viewTransition: Boolean
    },
    useLink: Cr,
    setup(e, {slots: t}) {
        const s = Is(Cr(e)), {options: n} = Je($n), r = Re(() => ({
            [Pr(e.activeClass, n.linkActiveClass, "router-link-active")]: s.isActive,
            [Pr(e.exactActiveClass, n.linkExactActiveClass, "router-link-exact-active")]: s.isExactActive
        }));
        return () => {
            const o = t.default && wa(t.default(s));
            return e.custom ? o : Lo("a", {
                "aria-current": s.isExactActive ? e.ariaCurrentValue : null,
                href: s.href,
                onClick: s.navigate,
                class: r.value
            }, o)
        }
    }
}), Ea = Sa;

function Ca(e) {
    if (!(e.metaKey || e.altKey || e.ctrlKey || e.shiftKey) && !e.defaultPrevented && !(e.button !== void 0 && e.button !== 0)) {
        if (e.currentTarget && e.currentTarget.getAttribute) {
            const t = e.currentTarget.getAttribute("target");
            if (/\b_blank\b/i.test(t)) return
        }
        return e.preventDefault && e.preventDefault(), !0
    }
}

function Ra(e, t) {
    for (const s in t) {
        const n = t[s], r = e[s];
        if (typeof n == "string") {
            if (n !== r) return !1
        } else if (!Te(r) || r.length !== n.length || n.some((o, i) => o !== r[i])) return !1
    }
    return !0
}

function Rr(e) {
    return e ? e.aliasOf ? e.aliasOf.path : e.path : ""
}

const Pr = (e, t, s) => e ?? t ?? s, Pa = pt({
    name: "RouterView",
    inheritAttrs: !1,
    props: {name: {type: String, default: "default"}, route: Object},
    compatConfig: {MODE: 3},
    setup(e, {attrs: t, slots: s}) {
        const n = Je(hn), r = Re(() => e.route || n.value), o = Je(Er, 0), i = Re(() => {
            let d = jt(o);
            const {matched: f} = r.value;
            let p;
            for (; (p = f[d]) && !p.components;) d++;
            return d
        }), l = Re(() => r.value.matched[i.value]);
        ps(Er, Re(() => i.value + 1)), ps(_a, l), ps(hn, r);
        const c = it();
        return hs(() => [c.value, l.value, e.name], ([d, f, p], [m, h, E]) => {
            f && (f.instances[p] = d, h && h !== f && d && d === m && (f.leaveGuards.size || (f.leaveGuards = h.leaveGuards), f.updateGuards.size || (f.updateGuards = h.updateGuards))), d && f && (!h || !Lt(f, h) || !m) && (f.enterCallbacks[p] || []).forEach(P => P(d))
        }, {flush: "post"}), () => {
            const d = r.value, f = e.name, p = l.value, m = p && p.components[f];
            if (!m) return Ar(s.default, {Component: m, route: d});
            const h = p.props[f], E = h ? h === !0 ? d.params : typeof h == "function" ? h(d) : h : null,
                B = Lo(m, U({}, E, t, {
                    onVnodeUnmounted: I => {
                        I.component.isUnmounted && (p.instances[f] = null)
                    }, ref: c
                }));
            return Ar(s.default, {Component: B, route: d}) || B
        }
    }
});

function Ar(e, t) {
    if (!e) return null;
    const s = e(t);
    return s.length === 1 ? s[0] : s
}

const Aa = Pa;

function Oa(e) {
    const t = ha(e.routes, e), s = e.parseQuery || ba, n = e.stringifyQuery || Sr, r = e.history, o = Kt(), i = Kt(),
        l = Kt(), c = Ti(st);
    let d = st;
    At && e.scrollBehavior && "scrollRestoration" in history && (history.scrollRestoration = "manual");
    const f = Xs.bind(null, y => "" + y), p = Xs.bind(null, Dc), m = Xs.bind(null, os);

    function h(y, T) {
        let A, $;
        return qo(y) ? (A = t.getRecordMatcher(y), $ = T) : $ = y, t.addRoute($, A)
    }

    function E(y) {
        const T = t.getRecordMatcher(y);
        T && t.removeRoute(T)
    }

    function P() {
        return t.getRoutes().map(y => y.record)
    }

    function B(y) {
        return !!t.getRecordMatcher(y)
    }

    function I(y, T) {
        if (T = U({}, T || c.value), typeof y == "string") {
            const g = Zs(s, y, T.path), b = t.resolve({path: g.path}, T), _ = r.createHref(g.fullPath);
            return U(g, b, {params: m(b.params), hash: os(g.hash), redirectedFrom: void 0, href: _})
        }
        let A;
        if (y.path != null) A = U({}, y, {path: Zs(s, y.path, T.path).path}); else {
            const g = U({}, y.params);
            for (const b in g) g[b] == null && delete g[b];
            A = U({}, y, {params: p(g)}), T.params = p(T.params)
        }
        const $ = t.resolve(A, T), Q = y.hash || "";
        $.params = f(m($.params));
        const a = Kc(n, U({}, y, {hash: Fc(Q), path: $.path})), u = r.createHref(a);
        return U({fullPath: a, hash: Q, query: n === Sr ? ya(y.query) : y.query || {}}, $, {
            redirectedFrom: void 0,
            href: u
        })
    }

    function M(y) {
        return typeof y == "string" ? Zs(s, y, c.value.path) : U({}, y)
    }

    function L(y, T) {
        if (d !== y) return Ft(8, {from: T, to: y})
    }

    function j(y) {
        return te(y)
    }

    function J(y) {
        return j(U(M(y), {replace: !0}))
    }

    function oe(y) {
        const T = y.matched[y.matched.length - 1];
        if (T && T.redirect) {
            const {redirect: A} = T;
            let $ = typeof A == "function" ? A(y) : A;
            return typeof $ == "string" && ($ = $.includes("?") || $.includes("#") ? $ = M($) : {path: $}, $.params = {}), U({
                query: y.query,
                hash: y.hash,
                params: $.path != null ? {} : y.params
            }, $)
        }
    }

    function te(y, T) {
        const A = d = I(y), $ = c.value, Q = y.state, a = y.force, u = y.replace === !0, g = oe(A);
        if (g) return te(U(M(g), {state: typeof g == "object" ? U({}, Q, g.state) : Q, force: a, replace: u}), T || A);
        const b = A;
        b.redirectedFrom = T;
        let _;
        return !a && Uc(n, $, A) && (_ = Ft(16, {
            to: b,
            from: $
        }), Ie($, $, !0, !1)), (_ ? Promise.resolve(_) : je(b, $)).catch(v => We(v) ? We(v, 2) ? v : tt(v) : K(v, b, $)).then(v => {
            if (v) {
                if (We(v, 2)) return te(U({replace: u}, M(v.to), {
                    state: typeof v.to == "object" ? U({}, Q, v.to.state) : Q,
                    force: a
                }), T || b)
            } else v = ht(b, $, !0, u, Q);
            return et(b, $, v), v
        })
    }

    function Me(y, T) {
        const A = L(y, T);
        return A ? Promise.reject(A) : Promise.resolve()
    }

    function Ze(y) {
        const T = Ct.values().next().value;
        return T && typeof T.runWithContext == "function" ? T.runWithContext(y) : y()
    }

    function je(y, T) {
        let A;
        const [$, Q, a] = Ta(y, T);
        A = en($.reverse(), "beforeRouteLeave", y, T);
        for (const g of $) g.leaveGuards.forEach(b => {
            A.push(ot(b, y, T))
        });
        const u = Me.bind(null, y, T);
        return A.push(u), Ce(A).then(() => {
            A = [];
            for (const g of o.list()) A.push(ot(g, y, T));
            return A.push(u), Ce(A)
        }).then(() => {
            A = en(Q, "beforeRouteUpdate", y, T);
            for (const g of Q) g.updateGuards.forEach(b => {
                A.push(ot(b, y, T))
            });
            return A.push(u), Ce(A)
        }).then(() => {
            A = [];
            for (const g of a) if (g.beforeEnter) if (Te(g.beforeEnter)) for (const b of g.beforeEnter) A.push(ot(b, y, T)); else A.push(ot(g.beforeEnter, y, T));
            return A.push(u), Ce(A)
        }).then(() => (y.matched.forEach(g => g.enterCallbacks = {}), A = en(a, "beforeRouteEnter", y, T, Ze), A.push(u), Ce(A))).then(() => {
            A = [];
            for (const g of i.list()) A.push(ot(g, y, T));
            return A.push(u), Ce(A)
        }).catch(g => We(g, 8) ? g : Promise.reject(g))
    }

    function et(y, T, A) {
        l.list().forEach($ => Ze(() => $(y, T, A)))
    }

    function ht(y, T, A, $, Q) {
        const a = L(y, T);
        if (a) return a;
        const u = T === st, g = At ? history.state : {};
        A && ($ || u ? r.replace(y.fullPath, U({scroll: u && g && g.scroll}, Q)) : r.push(y.fullPath, Q)), c.value = y, Ie(y, T, A, u), tt()
    }

    let $e;

    function Nt() {
        $e || ($e = r.listen((y, T, A) => {
            if (!as.listening) return;
            const $ = I(y), Q = oe($);
            if (Q) {
                te(U(Q, {replace: !0, force: !0}), $).catch(Xt);
                return
            }
            d = $;
            const a = c.value;
            At && Xc(hr(a.fullPath, A.delta), Bs()), je($, a).catch(u => We(u, 12) ? u : We(u, 2) ? (te(U(M(u.to), {force: !0}), $).then(g => {
                We(g, 20) && !A.delta && A.type === is.pop && r.go(-1, !1)
            }).catch(Xt), Promise.reject()) : (A.delta && r.go(-A.delta, !1), K(u, $, a))).then(u => {
                u = u || ht($, a, !1), u && (A.delta && !We(u, 8) ? r.go(-A.delta, !1) : A.type === is.pop && We(u, 20) && r.go(-1, !1)), et($, a, u)
            }).catch(Xt)
        }))
    }

    let St = Kt(), re = Kt(), G;

    function K(y, T, A) {
        tt(y);
        const $ = re.list();
        return $.length ? $.forEach(Q => Q(y, T, A)) : console.error(y), Promise.reject(y)
    }

    function Ke() {
        return G && c.value !== st ? Promise.resolve() : new Promise((y, T) => {
            St.add([y, T])
        })
    }

    function tt(y) {
        return G || (G = !y, Nt(), St.list().forEach(([T, A]) => y ? A(y) : T()), St.reset()), y
    }

    function Ie(y, T, A, $) {
        const {scrollBehavior: Q} = e;
        if (!At || !Q) return Promise.resolve();
        const a = !A && Zc(hr(y.fullPath, 0)) || ($ || !A) && history.state && history.state.scroll || null;
        return oo().then(() => Q(y, T, a)).then(u => u && Yc(u)).catch(u => K(u, y, T))
    }

    const ge = y => r.go(y);
    let Et;
    const Ct = new Set, as = {
        currentRoute: c,
        listening: !0,
        addRoute: h,
        removeRoute: E,
        clearRoutes: t.clearRoutes,
        hasRoute: B,
        getRoutes: P,
        resolve: I,
        options: e,
        push: j,
        replace: J,
        go: ge,
        back: () => ge(-1),
        forward: () => ge(1),
        beforeEach: o.add,
        beforeResolve: i.add,
        afterEach: l.add,
        onError: re.add,
        isReady: Ke,
        install(y) {
            const T = this;
            y.component("RouterLink", Ea), y.component("RouterView", Aa), y.config.globalProperties.$router = T, Object.defineProperty(y.config.globalProperties, "$route", {
                enumerable: !0,
                get: () => jt(c)
            }), At && !Et && c.value === st && (Et = !0, j(r.location).catch(Q => {
            }));
            const A = {};
            for (const Q in st) Object.defineProperty(A, Q, {get: () => c.value[Q], enumerable: !0});
            y.provide($n, T), y.provide(Yo, Zr(A)), y.provide(hn, c);
            const $ = y.unmount;
            Ct.add(y), y.unmount = function () {
                Ct.delete(y), Ct.size < 1 && (d = st, $e && $e(), $e = null, c.value = st, Et = !1, G = !1), $()
            }
        }
    };

    function Ce(y) {
        return y.reduce((T, A) => T.then(() => Ze(A)), Promise.resolve())
    }

    return as
}

function Ta(e, t) {
    const s = [], n = [], r = [], o = Math.max(t.matched.length, e.matched.length);
    for (let i = 0; i < o; i++) {
        const l = t.matched[i];
        l && (e.matched.find(d => Lt(d, l)) ? n.push(l) : s.push(l));
        const c = e.matched[i];
        c && (t.matched.find(d => Lt(d, c)) || r.push(c))
    }
    return [s, n, r]
}

const Ma = "/images/games/littleswordmaster/banner-1920x1080.png", ja = "/images/games/littleswordmaster/logo-512.png",
    $a = {class: "relative min-h-screen flex items-center overflow-hidden"},
    Ia = {class: "relative z-10 container-max page-spacing flex items-center min-h-screen"},
    ka = {class: "flex flex-col lg:flex-row lg:justify-between lg:items-center w-full gap-8 lg:gap-16"},
    La = {class: "space-y-6 max-w-md mx-auto lg:mx-0"},
    Fa = {class: "flex flex-col sm:flex-row gap-4 justify-center lg:justify-start"}, Na = pt({
        __name: "Home", setup(e) {
            return (t, s) => {
                const n = Fs("RouterLink");
                return he(), be("div", null, [x("section", $a, [s[4] || (s[4] = lt('<div class="absolute inset-0"><img src="' + Ma + '" alt="QQ神仙游戏背景" class="w-full h-full object-cover"><div class="absolute inset-0 bg-gradient-to-r from-black/90 via-black/70 to-black/50"></div></div><div class="absolute top-20 left-10 w-20 h-20 bg-jelly-500/20 rounded-full animate-float"></div><div class="absolute top-40 right-20 w-16 h-16 bg-jelly-400/30 rounded-full animate-float" style="animation-delay:2s;"></div><div class="absolute bottom-40 left-20 w-12 h-12 bg-jelly-600/25 rounded-full animate-float" style="animation-delay:4s;"></div>', 4)), x("div", Ia, [x("div", ka, [s[3] || (s[3] = lt('<div class="space-y-6 text-center lg:text-left"><h1 class="text-4xl sm:text-5xl lg:text-7xl font-display font-bold"><span class="gradient-text" style="text-shadow:0px 0px 15px rgba(221, 87, 255, 0.6);">依梦</span><br><span class="text-white" style="text-shadow:0px 0px 12px rgba(255,255,255,0.4);">工作室</span></h1><p class="text-lg sm:text-xl lg:text-2xl text-white max-w-lg mx-auto lg:mx-0 font-medium" style="text-shadow:2px 2px 4px rgba(0,0,0,0.8);"> 创造充满想象力的游戏世界，为玩家带来独特的冒险体验 </p></div>', 1)), x("div", La, [s[2] || (s[2] = lt('<div class="space-y-3 text-center lg:text-left"><div class="flex items-center justify-center lg:justify-start space-x-4"><div class="w-16 h-16 rounded-xl overflow-hidden"><img src="' + ja + '" alt="QQ神仙logo" class="w-full h-full object-cover"></div><div><h2 class="text-3xl lg:text-4xl font-display font-bold gradient-text">QQ神仙</h2><p class="text-lg lg:text-xl text-white font-medium" style="text-shadow:1px 1px 3px rgba(0,0,0,0.8);">QQ Master</p></div></div><div class="flex flex-col sm:flex-row sm:items-center gap-3"><div class="inline-block bg-jelly-500/20 backdrop-blur-sm px-3 py-1.5 rounded-full border border-jelly-500/30"></a></div></div></div><div class="bg-black/60 backdrop-blur-lg border border-white/20 rounded-xl p-6 shadow-2xl"><p class="text-base lg:text-lg text-white leading-relaxed font-medium" style="text-shadow:2px 2px 4px rgba(0,0,0,0.9);">以西游神话为背景融合卡牌收集养成、策略对战、探险闯关与社交互动机制，力求让每一局卡牌对决都充满新鲜感；玩家将置身于随机掉落与阵容搭配的双重变数中： 抽卡合成、阵营协同、竞技博弈，同时玩转休闲养成与好友 PK 的多元玩法。 </p></div><div class="flex flex-wrap gap-3 justify-center lg:justify-start"><span class="px-4 py-2 bg-purple-500/20 text-purple-300 rounded-full text-sm backdrop-blur-sm border border-purple-500/30">集换式卡牌</span><span class="px-4 py-2 bg-blue-500/20 text-blue-300 rounded-full text-sm backdrop-blur-sm border border-blue-500/30">阵营协同</span><span class="px-4 py-2 bg-green-500/20 text-green-300 rounded-full text-sm backdrop-blur-sm border border-green-500/30">竞技博弈</span><span class="px-4 py-2 bg-yellow-500/20 text-yellow-300 rounded-full text-sm backdrop-blur-sm border border-yellow-500/30">随机抽牌</span></div>', 3)), x("div", Fa, [se(n, {
                    to: "/products",
                    class: "btn-primary text-lg px-8 py-4 shadow-xl hover:shadow-2xl"
                }, {
                    default: at(() => [...s[0] || (s[0] = [yt(" 了解详情 ", -1)])]),
                    _: 1
                }), s[1] || (s[1] = x("a", {
                    href: "appUpload.html",
                    target: "_blank",
                    rel: "noopener noreferrer",
                    class: "btn-secondary text-lg px-8 py-4 shadow-xl hover:shadow-2xl"
                }, " 立即体验 ", -1))])])])])]), s[5] || (s[5] = lt('<section class="section-spacing bg-gray-900"><div class="container-max"><div class="text-center mb-16"><h3 class="text-3xl font-display font-bold gradient-text mb-4">独立游戏工作室</h3><p class="text-xl text-gray-300">专注创新，追求卓越</p></div><div class="grid grid-cols-1 md:grid-cols-3 gap-8"><div class="text-center space-y-4"><div class="w-20 h-20 bg-jelly-500/20 rounded-full flex items-center justify-center mx-auto"><span class="text-3xl">🎮</span></div><h4 class="text-xl font-semibold text-white">创新玩法</h4><p class="text-gray-300">融合多种游戏机制，创造独特体验</p></div><div class="text-center space-y-4"><div class="w-20 h-20 bg-purple-500/20 rounded-full flex items-center justify-center mx-auto"><span class="text-3xl">💎</span></div><h4 class="text-xl font-semibold text-white">精品制作</h4><p class="text-gray-300">专注质量，每一款游戏都精心打磨</p></div><div class="text-center space-y-4"><div class="w-20 h-20 bg-blue-500/20 rounded-full flex items-center justify-center mx-auto"><span class="text-3xl">🚀</span></div><h4 class="text-xl font-semibold text-white">持续更新</h4><p class="text-gray-300">不断优化，为玩家带来更好体验</p></div></div></div></section>', 1))])
            }
        }
    }), Ha = {class: "grid-background"}, Da = {class: "hero-section"}, Ba = {class: "container-max"},
    Va = {class: "grid grid-cols-1 lg:grid-cols-2 gap-12 items-start"}, Ka = {class: "relative order-2 lg:order-1"},
    Ua = {class: "space-y-6"}, Wa = {class: "relative"},
    za = {class: "aspect-[16/9] rounded-2xl overflow-hidden relative max-h-[600px] bg-gray-800/50"},
    qa = ["src", "alt"], Ga = {class: "absolute bottom-4 left-4 bg-black/40 backdrop-blur-sm px-4 py-2 rounded-lg"},
    Ja = {class: "text-white text-sm font-medium"}, Qa = {class: "absolute bottom-6 right-6"}, Ya = {class: "relative"},
    Xa = ["onClick"], Za = {class: "w-16 h-24 rounded-lg overflow-hidden bg-gray-800"}, ef = ["src", "alt"],
    tf = {class: "flex justify-center mt-4 space-x-2"}, sf = ["onClick"], nf = {class: "order-1 lg:order-2 space-y-8"},
    rf = {class: "flex flex-col sm:flex-row gap-4"}, of = {class: "section-spacing"}, lf = {class: "container-max"},
    cf = {class: "grid grid-cols-1 lg:grid-cols-2 gap-12 items-start"}, af = {class: "order-1 lg:order-1 space-y-8"},
    ff = {class: "space-y-4"}, uf = {class: "inline-block bg-jelly-500/20 px-4 py-2 rounded-full"},
    df = {class: "text-jelly-400 font-medium text-sm"},
    pf = {class: "text-5xl lg:text-6xl font-display font-bold text-white leading-tight"}, hf = {class: "gradient-text"},
    gf = {class: "text-xl text-gray-300 font-medium"}, mf = {class: "space-y-4"},
    xf = {class: "text-lg text-gray-300 leading-relaxed"}, vf = {class: "flex flex-col sm:flex-row gap-4"},
    bf = {class: "relative order-2 lg:order-2"}, yf = {class: "space-y-6"}, _f = {class: "relative"},
    wf = {class: "aspect-[16/9] rounded-2xl overflow-hidden relative max-h-[600px] bg-gray-800/50"},
    Sf = ["src", "alt"], Ef = {class: "absolute bottom-6 right-6"},
    Cf = {class: "aspect-video bg-black rounded-lg overflow-hidden"}, Rf = ["src"], Pf = pt({
        __name: "Products", setup(e) {
            const t = it(!1), s = it(""), n = it(), r = m => {
                    s.value = m, t.value = !0
                }, o = () => {
                    t.value = !1, n.value && n.value.pause()
                }, i = [{title: "游戏Banner", image: "/images/games/littleswordmaster/banner-1920x1080.png"}, {
                    title: "情怀主页",
                    image: "/images/games/littleswordmaster/screenshots/autumn_1080x1920.png"
                }, {
                    title: "Boss战斗",
                    image: "/images/games/littleswordmaster/screenshots/boss_fight_1080x1920.png"
                }, {
                    title: "火焰技能",
                    image: "/images/games/littleswordmaster/screenshots/lightning_1080x1920.png"
                }, {
                    title: "奇幻探险",
                    image: "/images/games/littleswordmaster/screenshots/summer_1080x1920.png"
                }, {
                    title: "高爆率海量卡池",
                    image: "/images/games/littleswordmaster/screenshots/swords-1080x1920.png"
                }, {title: "神秘五星", image: "/images/games/littleswordmaster/screenshots/winter_1080x1920.png"}], l = it(0),
                c = it(), d = Re(() => i[l.value]), f = m => {
                    l.value = m
                }, p = [{
                    title: "----",
                    englishTitle: "------",
                    description: "",
                    status: "开发中",
                    image: "/images/games/savethefarm/main.jpg",
                    video: "/savethefarm/savethefarm.mp4"
                }];
            return (m, h) => (he(), be("div", Ha, [x("section", Da, [x("div", Ba, [x("div", Va, [x("div", Ka, [x("div", Ua, [x("div", Wa, [x("div", za, [x("img", {
                src: d.value.image,
                alt: d.value.title,
                class: "w-full h-full object-contain transition-all duration-500"
            }, null, 8, qa), h[5] || (h[5] = x("div", {class: "absolute inset-0 bg-gradient-to-br from-black/20 to-transparent"}, null, -1)), h[6] || (h[6] = x("div", {class: "absolute top-4 right-4 w-8 h-8 bg-jelly-500/30 rounded-full animate-glow"}, null, -1)), h[7] || (h[7] = x("div", {
                class: "absolute bottom-4 left-4 w-6 h-6 bg-purple-500/30 rounded-full animate-glow",
                style: {"animation-delay": "1s"}
            }, null, -1)), x("div", Ga, [x("p", Ja, we(d.value.title), 1)])]), x("div", Qa, [x("button", {
                onClick: h[0] || (h[0] = E => r("/video/littleswordmaster.mp4")),
                class: "w-16 h-16 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center hover:bg-white/30 transition-all duration-300 group"
            }, [...h[8] || (h[8] = [x("svg", {
                class: "w-8 h-8 text-white group-hover:scale-110 transition-transform duration-300",
                fill: "currentColor",
                viewBox: "0 0 24 24"
            }, [x("path", {d: "M8 5v14l11-7z"})], -1)])])])]), x("div", Ya, [x("div", {
                class: "flex space-x-4 overflow-x-auto scrollbar-hide pb-2",
                ref_key: "thumbnailContainer",
                ref: c
            }, [(he(), be(ce, null, bt(i, (E, P) => x("div", {
                key: P,
                onClick: B => f(P),
                class: _t(["flex-shrink-0 cursor-pointer group", {"ring-2 ring-jelly-400": l.value === P}])
            }, [x("div", Za, [x("img", {
                src: E.image,
                alt: E.title,
                class: "w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            }, null, 8, ef)])], 10, Xa)), 64))], 512), x("div", tf, [(he(), be(ce, null, bt(i, (E, P) => x("button", {
                key: P,
                onClick: B => f(P),
                class: _t(["w-2 h-2 rounded-full transition-all duration-300", l.value === P ? "bg-jelly-400 w-6" : "bg-gray-600"])
            }, null, 10, sf)), 64))])])])]), x("div", nf, [h[10] || (h[10] = lt('<div class="space-y-4"><div class="flex flex-col sm:flex-row sm:items-center gap-4"><div class="inline-block bg-jelly-500/20 px-3 py-1.5 rounded-full"><span class="text-jelly-400 font-medium text-sm">最新发布</span></div><div class="flex items-center gap-2"><div class="flex gap-2 flex-wrap"><a href="appUpload.html" target="_blank" rel="noopener noreferrer" class="platform-mini-btn bg-blue-500"><span class="text-xs">网页</span></a><a href="appUpload.html" target="_blank" rel="noopener noreferrer" class="platform-mini-btn bg-green-500"><span class="text-xs">微信</span></a><a href="#" target="_blank" rel="noopener noreferrer" class="platform-mini-btn bg-red-500"><span class="text-xs">抖音</span></a><a href="#" target="_blank" rel="noopener noreferrer" class="platform-mini-btn bg-orange-500"><span class="text-xs">好游快爆</span></a><a href="#" target="_blank" rel="noopener noreferrer" class="platform-mini-btn bg-purple-500"><span class="text-xs">4399</span></a></div></div></div><h1 class="text-5xl lg:text-6xl font-display font-bold text-white leading-tight"><span class="gradient-text">QQ神仙</span></h1><p class="text-xl text-gray-300 font-medium">QQ Master</p></div><div class="space-y-4"><p class="text-lg text-gray-300 leading-relaxed"> 以西游神话为背景融合卡牌收集养成、策略对战、探险闯关与社交互动机制，力求让每一局卡牌对决都充满新鲜感； 玩家将置身于随机掉落与阵容搭配的双重变数中： 抽卡合成、阵营协同、竞技博弈，同时玩转休闲养成与好友 PK 的多元玩法。</p><div class="space-y-3"><div class="flex items-center space-x-3"><div class="w-2 h-2 bg-jelly-400 rounded-full"></div><span class="text-gray-300">集换式仙卡牌</span></div><div class="flex items-center space-x-3"><div class="w-2 h-2 bg-jelly-400 rounded-full"></div><span class="text-gray-300">阵营协同挑战</span></div><div class="flex items-center space-x-3"><div class="w-2 h-2 bg-jelly-400 rounded-full"></div><span class="text-gray-300">竞技博弈战斗</span></div><div class="flex items-center space-x-3"><div class="w-2 h-2 bg-jelly-400 rounded-full"></div><span class="text-gray-300">随机抽牌策略</span></div></div></div>', 2)), x("div", rf, [h[9] || (h[9] = x("a", {
                href: "appUpload.html",
                target: "_blank",
                rel: "noopener noreferrer",
                class: "btn-primary text-lg px-8 py-4"
            }, " 立即体验 ", -1)), x("button", {
                onClick: h[1] || (h[1] = E => r("/yimem_files/qqsx.mp4")),
                class: "btn-secondary text-lg px-8 py-4"
            }, " 观看宣传片 ")]), h[11] || (h[11] = x("div", {class: "flex items-center space-x-6"}, [x("a", {
                href: "#",
                target: "_blank",
                rel: "noopener noreferrer",
                class: "text-gray-400 hover:text-jelly-400 transition-colors duration-300 flex items-center space-x-2"
            }, [x("svg", {
                class: "w-6 h-6",
                fill: "currentColor",
                viewBox: "0 0 24 24"
            }, [x("path", {d: "M17.813 4.653c-.08-.1-.2-.15-.3-.15-.1 0-.2.05-.3.15L14.5 7.5c-.1.1-.1.2-.1.3v8c0 .1.05.2.15.3.1.1.2.15.3.15h6c.1 0 .2-.05.3-.15.1-.1.15-.2.15-.3V5c0-.1-.05-.2-.15-.3-.1-.1-.2-.15-.3-.15h-3.5zm-8.5 0c-.1 0-.2.05-.3.15L5.5 7.5c-.1.1-.1.2-.1.3v8c0 .1.05.2.15.3.1.1.2.15.3.15h6c.1 0 .2-.05.3-.15.1-.1.15-.2.15-.3V5c0-.1-.05-.2-.15-.3-.1-.1-.2-.15-.3-.15H9.313z"})]), x("span", null, "")])], -1))])])])]), x("section", of, [x("div", lf, [h[17] || (h[17] = x("div", {class: "text-center mb-16"}, [x("h2", {class: "text-4xl font-display font-bold gradient-text mb-4"}, "即将发布"), x("p", {class: "text-xl text-gray-300"}, "更多精彩游戏正在开发中")], -1)), (he(), be(ce, null, bt(p, E => x("div", {
                key: E.title,
                class: "mb-16"
            }, [x("div", cf, [x("div", af, [x("div", ff, [x("div", uf, [x("span", df, we(E.status), 1)]), x("h3", pf, [x("span", hf, we(E.title), 1)]), x("p", gf, we(E.englishTitle), 1)]), x("div", mf, [x("p", xf, we(E.description), 1)]), x("div", vf, [h[12] || (h[12] = x("a", {
                href: "",
                target: "_blank",
                rel: "noopener noreferrer",
                class: "btn-primary text-lg px-8 py-4 shadow-xl hover:shadow-2xl"
            }, " 关注开发进度 ", -1)), x("button", {
                onClick: h[2] || (h[2] = P => r("/video/savethefarm.mp4")),
                class: "btn-secondary text-lg px-8 py-4 shadow-xl hover:shadow-2xl"
            }, " 观看预告片 ")])]), x("div", bf, [x("div", yf, [x("div", _f, [x("div", wf, [x("img", {
                src: E.image,
                alt: E.title,
                class: "w-full h-full object-cover transition-all duration-500"
            }, null, 8, Sf), h[14] || (h[14] = x("div", {class: "absolute inset-0 bg-gradient-to-br from-black/20 to-transparent"}, null, -1)), h[15] || (h[15] = x("div", {class: "absolute top-4 right-4 w-8 h-8 bg-jelly-500/30 rounded-full animate-glow"}, null, -1)), h[16] || (h[16] = x("div", {
                class: "absolute bottom-4 left-4 w-6 h-6 bg-purple-500/30 rounded-full animate-glow",
                style: {"animation-delay": "1s"}
            }, null, -1)), x("div", Ef, [x("button", {
                onClick: h[3] || (h[3] = P => r("/video/savethefarm.mp4")),
                class: "w-16 h-16 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center hover:bg-white/30 transition-all duration-300 group"
            }, [...h[13] || (h[13] = [x("svg", {
                class: "w-8 h-8 text-white group-hover:scale-110 transition-transform duration-300",
                fill: "currentColor",
                viewBox: "0 0 24 24"
            }, [x("path", {d: "M8 5v14l11-7z"})], -1)])])])])])])])])])), 64))])]), h[19] || (h[19] = lt('<section class="section-spacing bg-gray-800/30"><div class="container-max text-center"><div class="max-w-3xl mx-auto"><h2 class="text-4xl font-display font-bold gradient-text mb-6">准备开始冒险了吗？</h2><p class="text-xl text-gray-300 mb-8"> 加入我们的游戏世界，体验独特的冒险之旅 </p><div class="flex justify-center"><a href="appUpload.html" target="_blank" rel="noopener noreferrer" class="btn-primary text-lg px-8 py-4">立即下载</a></div></div></div></section>', 1)), t.value ? (he(), be("div", {
                key: 0,
                class: "fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm",
                onClick: o
            }, [x("div", {
                class: "relative w-full max-w-4xl mx-4", onClick: h[4] || (h[4] = bc(() => {
                }, ["stop"]))
            }, [x("button", {
                onClick: o,
                class: "absolute -top-12 right-0 text-white hover:text-gray-300 transition-colors duration-300"
            }, [...h[18] || (h[18] = [x("svg", {
                class: "w-8 h-8",
                fill: "none",
                stroke: "currentColor",
                viewBox: "0 0 24 24"
            }, [x("path", {
                "stroke-linecap": "round",
                "stroke-linejoin": "round",
                "stroke-width": "2",
                d: "M6 18L18 6M6 6l12 12"
            })], -1)])]), x("div", Cf, [x("video", {
                ref_key: "videoPlayer",
                ref: n,
                src: s.value,
                controls: "",
                autoplay: "",
                class: "w-full h-full"
            }, " 您的浏览器不支持视频播放。 ", 8, Rf)])])])) : Nl("", !0)]))
        }
    }), Af = "/images/Avatar.jpg", Of = {class: "section-spacing bg-gray-800/30"}, Tf = {class: "container-max"},
    Mf = {class: "grid grid-cols-1 md:grid-cols-3 gap-8"},
    jf = {class: "w-16 h-16 bg-gradient-to-br from-jelly-400 to-jelly-600 rounded-xl flex items-center justify-center mx-auto mb-6"},
    $f = {class: "text-white font-bold text-xl"}, If = {class: "text-xl font-semibold text-white mb-4"},
    kf = {class: "text-gray-300"}, Lf = {class: "section-spacing bg-gray-800/30"}, Ff = {class: "container-max"},
    Nf = {class: "grid grid-cols-2 md:grid-cols-4 gap-8"},
    Hf = {class: "text-4xl md:text-5xl font-bold gradient-text mb-2"}, Df = {class: "text-gray-300"}, Bf = pt({
        __name: "About", setup(e) {
            const t = [{title: "创新", description: "始终追求创新，不断探索新的游戏机制和创意表达方式", icon: "💡"}, {
                    title: "品质",
                    description: "对每一个细节都精益求精，确保为玩家提供最高品质的游戏体验",
                    icon: "⭐"
                }, {title: "专注", description: "专注于独立游戏开发，用心打造每一款游戏，为玩家带来独特体验", icon: "🎯"}],
                s = [{value: "1+", label: "年经验"}, {value: "1", label: "游戏项目"}, {value: "1", label: "独立开发者"}, {
                    value: "∞",
                    label: "创作热情"
                }];
            return (n, r) => (he(), be("div", null, [r[2] || (r[2] = lt('<section class="relative py-20 bg-gradient-to-br from-gray-900 via-purple-900/20 to-gray-900"><div class="container-max"><div class="text-center mb-16"><h1 class="text-5xl md:text-6xl font-display font-bold gradient-text mb-6">关于我们</h1></div><div class="max-w-4xl mx-auto"><div class="glass-card p-8"><div class="text-center mb-8"><div class="w-32 h-32 rounded-full overflow-hidden mx-auto mb-6 border-4 border-jelly-500/30"><img src="' + Af + '" alt="YiMem 开发者头像" class="w-full h-full object-cover"></div><h3 class="text-3xl font-semibold text-white mb-2">YiMem</h3><p class="text-jelly-400 font-medium text-lg mb-6">独立游戏开发者</p></div><div class="space-y-6 text-gray-300 leading-relaxed"><p> 依梦工作室成立于2024年，是一个专注于独立游戏开发的个人工作室。我相信游戏不仅仅是娱乐， 更是一种艺术形式，能够传达情感、讲述故事、连接人心。 </p><p> 作为独立开发者，我倾注心血于游戏的每一个细节，从概念设计到程序开发，从美术创作到音效制作， 致力于为玩家创造独特而难忘的游戏体验。 </p><p> 正在开发的《QQ神仙》，我始终坚持创新和品质。每一款游戏都是我创作心血的结晶， 致力于探索不同的游戏类型和主题，希望通过它们为玩家带来快乐、启发、感动以及多样化的游戏体验。 </p></div></div></div></div></section>', 1)), x("section", Of, [x("div", Tf, [r[0] || (r[0] = x("div", {class: "text-center mb-16"}, [x("h2", {class: "text-4xl font-display font-bold gradient-text mb-4"}, "我们的价值观"), x("p", {class: "text-xl text-gray-300"}, "指导我们工作的核心原则")], -1)), x("div", Mf, [(he(), be(ce, null, bt(t, o => x("div", {
                key: o.title,
                class: "card text-center"
            }, [x("div", jf, [x("span", $f, we(o.icon), 1)]), x("h3", If, we(o.title), 1), x("p", kf, we(o.description), 1)])), 64))])])]), x("section", Lf, [x("div", Ff, [r[1] || (r[1] = x("div", {class: "text-center mb-16"}, [x("h2", {class: "text-4xl font-display font-bold gradient-text mb-4"}, "我们的成就"), x("p", {class: "text-xl text-gray-300"}, "数字见证我们的成长")], -1)), x("div", Nf, [(he(), be(ce, null, bt(s, o => x("div", {
                key: o.label,
                class: "text-center"
            }, [x("div", Hf, we(o.value), 1), x("div", Df, we(o.label), 1)])), 64))])])])]))
        }
    }), Vf = [{path: "/", name: "Home", component: Na}, {
        path: "/products",
        name: "Products",
        component: Pf
    }, {path: "/about", name: "About", component: Bf}], Kf = Oa({
        history: ra(), routes: Vf, scrollBehavior(e, t, s) {
            return s || {top: 0}
        }
    }), Xo = "/images/YiMem.png",
    Uf = {class: "fixed top-0 left-0 right-0 z-50 bg-gray-900/80 backdrop-blur-md border-b border-gray-800/50"},
    Wf = {class: "container-max"}, zf = {class: "flex items-center justify-between h-16"},
    qf = {class: "hidden md:flex items-center space-x-8"},
    Gf = {class: "md:hidden border-t border-gray-800/50 bg-gray-900/95 backdrop-blur-md"},
    Jf = {class: "py-4 space-y-2"}, Qf = pt({
        __name: "Header", setup(e) {
            const t = it(!1), s = [{name: "首页", path: "/"}, {name: "产品", path: "/products"}, {
                name: "关于我们",
                path: "/about"
            }, {name: "官方①群587452663", path: ""}], n = () => {
                t.value = !t.value
            }, r = () => {
                t.value = !1
            };
            return (o, i) => {
                const l = Fs("RouterLink");
                return he(), be("header", Uf, [x("nav", Wf, [x("div", zf, [se(l, {
                    to: "/",
                    class: "flex items-center space-x-3 group"
                }, {
                    default: at(() => [...i[0] || (i[0] = [x("div", {class: "w-10 h-10 rounded-lg overflow-hidden group-hover:scale-110 transition-transform duration-300"}, [x("img", {
                        src: Xo,
                        alt: "YiMem Logo",
                        class: "w-full h-full object-cover"
                    })], -1), x("div", {class: "hidden sm:block"}, [x("h1", {class: "text-xl font-display font-bold gradient-text"}, "YiMem"), x("p", {class: "text-xs text-gray-400"}, "依梦工作室")], -1)])]),
                    _: 1
                }), x("div", qf, [(he(), be(ce, null, bt(s, c => se(l, {
                    key: c.name,
                    to: c.path,
                    class: _t(["text-gray-300 hover:text-jelly-400 transition-colors duration-300 font-medium", {"text-jelly-400": o.$route.path === c.path}])
                }, {default: at(() => [yt(we(c.name), 1)]), _: 2}, 1032, ["to", "class"])), 64))]), x("button", {
                    onClick: n,
                    class: "md:hidden p-2 text-gray-300 hover:text-jelly-400 transition-colors duration-300"
                }, [...i[1] || (i[1] = [x("svg", {
                    class: "w-6 h-6",
                    fill: "none",
                    stroke: "currentColor",
                    viewBox: "0 0 24 24"
                }, [x("path", {
                    "stroke-linecap": "round",
                    "stroke-linejoin": "round",
                    "stroke-width": "2",
                    d: "M4 6h16M4 12h16M4 18h16"
                })], -1)])])]), Di(x("div", Gf, [x("div", Jf, [(he(), be(ce, null, bt(s, c => se(l, {
                    key: c.name,
                    to: c.path,
                    onClick: r,
                    class: _t(["block px-4 py-2 text-gray-300 hover:text-jelly-400 hover:bg-gray-800/50 rounded-lg transition-colors duration-300", {"text-jelly-400 bg-gray-800/50": o.$route.path === c.path}])
                }, {
                    default: at(() => [yt(we(c.name), 1)]),
                    _: 2
                }, 1032, ["to", "class"])), 64))])], 512), [[sc, t.value]])])])
            }
        }
    }), Yf = {class: "bg-gray-900 border-t border-gray-800/50"}, Xf = {class: "container-max section-spacing"},
    Zf = {class: "text-center"}, eu = {class: "mb-8"}, tu = {class: "flex justify-center space-x-8"}, su = pt({
        __name: "Footer", setup(e) {
            return (t, s) => {
                const n = Fs("RouterLink");
                return he(), be("footer", Yf, [x("div", Xf, [x("div", Zf, [s[4] || (s[4] = lt('<div class="mb-8"><div class="flex items-center justify-center space-x-3 mb-4"><div class="w-10 h-10 rounded-lg overflow-hidden"><img src="' + Xo + '" alt="YiMem Logo" class="w-full h-full object-cover"></div><div><h3 class="text-xl font-display font-bold gradient-text">YiMem</h3><p class="text-sm text-gray-400">依梦工作室</p></div></div><p class="text-gray-300 mb-6 max-w-2xl mx-auto"> 专注于创造独特游戏体验的个人独立游戏工作室，致力于为玩家带来充满想象力的游戏世界。 </p><div class="flex justify-center"><a href="#" target="_blank" rel="noopener noreferrer" class="text-gray-400 hover:text-jelly-400 transition-colors duration-300 flex items-center space-x-2"><svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24"><path d="M17.813 4.653c-.08-.1-.2-.15-.3-.15-.1 0-.2.05-.3.15L14.5 7.5c-.1.1-.1.2-.1.3v8c0 .1.05.2.15.3.1.1.2.15.3.15h6c.1 0 .2-.05.3-.15.1-.1.15-.2.15-.3V5c0-.1-.05-.2-.15-.3-.1-.1-.2-.15-.3-.15h-3.5zm-8.5 0c-.1 0-.2.05-.3.15L5.5 7.5c-.1.1-.1.2-.1.3v8c0 .1.05.2.15.3.1.1.2.15.3.15h6c.1 0 .2-.05.3-.15.1-.1.15-.2.15-.3V5c0-.1-.05-.2-.15-.3-.1-.1-.2-.15-.3-.15H9.313z"></path></svg><span></span></a></div></div>', 1)), x("div", eu, [s[3] || (s[3] = x("h4", {class: "text-lg font-semibold text-white mb-4"}, "", -1)), x("div", tu, [se(n, {
                    to: "/",
                    class: "text-gray-300 hover:text-jelly-400 transition-colors duration-300"
                }, {default: at(() => [...s[0] || (s[0] = [yt("首页", -1)])]), _: 1}), se(n, {
                    to: "/products",
                    class: "text-gray-300 hover:text-jelly-400 transition-colors duration-300"
                }, {default: at(() => [...s[1] || (s[1] = [yt("产品", -1)])]), _: 1}), se(n, {
                    to: "/about",
                    class: "text-gray-300 hover:text-jelly-400 transition-colors duration-300"
                }, {
                    default: at(() => [...s[2] || (s[2] = [yt("关于我们", -1)])]),
                    _: 1
                })])])]), s[5] || (s[5] = x("div", {class: "border-t border-gray-800/50 mt-8 pt-8 text-center text-gray-400"}, [x("p", null, "© 2024 依梦工作室 (YiMem). 保留所有权利.")], -1))])])
            }
        }
    }), nu = {id: "app", class: "min-h-screen bg-gray-900"}, ru = {class: "pt-16"}, ou = pt({
        __name: "App", setup(e) {
            return (t, s) => {
                const n = Fs("RouterView");
                return he(), be("div", nu, [se(Qf), x("main", ru, [se(n)]), se(su)])
            }
        }
    }), In = wc(ou);
In.use(Rc());
In.use(Kf);
In.mount("#app");
