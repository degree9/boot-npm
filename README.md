<p align="center"><img src="/.github/d9boot-npm.png" width="445px"></p>

[![Clojars Project](https://img.shields.io/clojars/v/degree9/boot-npm.svg)](https://clojars.org/degree9/boot-npm)
[![Dependencies Status](https://jarkeeper.com/degree9/boot-npm/status.svg)](https://jarkeeper.com/degree9/boot-npm)
[![Downloads](https://jarkeeper.com/degree9/boot-npm/downloads.svg)](https://jarkeeper.com/degree9/boot-npm)
[![Slack][slack]][d9-slack]
<!--- [![CircleCI](https://circleci.com/gh/degree9/boot-npm.svg?style=svg)](https://circleci.com/gh/degree9/boot-npm) --->

Node Package Manager (NPM) wrapper for [boot-clj][boot-clj].

* Provides `npm` task for installing node modules.
* Provides `exec` task for executing node modules. (auto-installs local module)

> The following outlines basic usage of the task, extensive testing has not been done.
> Please submit issues and pull requests!

## Usage

Add `boot-npm` to your `build.boot` dependencies and `require` the namespace:

```clj
(set-env! :dependencies '[[degree9/boot-npm "X.Y.Z" :scope "test"]])
(require '[degree9.boot-npm :as npm])
```

Install a Node Module:

```clojure
(boot/deftask bower
  "Install bower to node_modules."
  []
  (npm/npm :install ["bower@latest"])))
```

## Notes

- Starting with version `1.9` the `:install` option no longer accepts keywords, use a vector of strings instead.
  ex. `["bower@latest"]`
- Starting with version `1.8` the `:install` option no longer accepts keywords, use a map of strings instead.
  ex. `{"bower" "latest"}`

## Task Options

The `npm` task exposes a few options when using npm as part of a build process.

```clojure
[p package     VAL     str   "An edn file containing a package.json map."
 i install     FOO=BAR [str] "Dependency map."
 d develop             bool  "Include development dependencies with packages."
 r dry-run             bool  "Report what changes npm would have made. (usefull with boot -vv)"
 g global              bool  "Opperates in global mode. Packages are installed to prefix."
 c cache-key   VAL     kw    "Optional cache key for when npm is used with multiple dependency sets."
 _ include             bool  "Include package.json in fileset output."
 _ pretty              bool  "Pretty print generated package.json file"]
```

The `:install` option is provided for installing node modules, takes a map containing a dependency/version pair. This will install the module to a temporary `node_modules` folder and include this folder in the fileset output.

```clojure
(boot/deftask bower
  "Install bower to node_modules."
  []
  (npm/npm :install ["bower@latest"]))
```

The `:cache-key` option is provided to avoid downloading node modules each time boot is restarted. This will cache the `node_modules` folder and include this folder in the fileset output.

```clojure
(boot/deftask bower
  "Install bower to node_modules."
  []
  (npm/npm :install   ["bower@latest"]
           :cache-key ::cache))
```

---

<p align="center">
  <a href="https://www.patreon.com/degree9" align="center">
    <img src="https://c5.patreon.com/external/logo/become_a_patron_button@2x.png" width="160" alt="Patreon">
  </a>
  <br>
  <b>Support this and other open- patrons.</b>
</p>

---

[boot-clj]: https://github.com/boot-clj/boot
[slack]: https://img.shields.io/badge/clojurians-degree9-%23e01563.svg?logo=slack
[d9-slack]: https://clojurians.slack.com/channels/degree9/
