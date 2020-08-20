# repoman

Tiny tool to query repology.org.

## Installation

Download the uberjar.  In the future I want to publish this as an executable.

## Usage

Currently needs java.

```
$ java -jar repoman-0.1.0-standalone.jar -s starship --with-repo chocolatey --with-repo scoop
"rust:starship-module-config-derive" is in 0/4 repositories
"starship" is in 3/20 repositories

$ java -jar repoman-0.1.0-standalone.jar -p starship --with-repo chocolatey --with-repo scoop
|      :repo | :subrepo |             :name | :version |  :status |
|------------+----------+-------------------+----------+----------|
| chocolatey |          |          starship |   0.44.0 |   newest |
| chocolatey |          | starship.portable |   0.41.0 | outdated |
|      scoop |     main |          starship |   0.44.0 |   newest |

```

Create a config file at `$(... --config)`.  Here is an example file.

```clojure
[arch
 gnuguix
 nix_stable
 linuxbrew]
```

## Options

```
rp: A tiny tool for querying repology

  -s, --search TERM         Search for a package
  -p, --project PRJ         Show repo info for a project
  -a, --show-all            Show all repos with -p
  -r, --with-repo REPO  []  Include a specific repo with -s or -p
  -j, --json                Convert -s or -p output to json
  -c, --config              Displays the config file location
  -h, --help                Display this help string

```

## Additional

I wrote this in an afternoon.  I had a vm with 4 non-language-specific package managers and wanted an easier way to query what was available, where.

After writing it, I realized it's small enough (< 150 loc) to be an excellent project for playing around with lumo, babashka, graal native image, etc.  I plan to do that at some point.

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
