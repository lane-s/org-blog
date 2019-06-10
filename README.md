# org-blog

org-blog is intended for emacs users that want to blog directly from org-mode.

org-blog currently does not provide a complete blogging solution right out of the box. It is meant for developers or others who are willing to do the (fairly small) amount of work required to deploy the servers. After this work is done, you'll be able to write and manage posts in emacs. This is great if, like me, you prefer to use emacs for just about everything. For anyone else, it's probably not worth the trouble.

For now, the system consists of three parts:
- The backend (this repo): stores, parses, and serves blog posts.
- [org-blog-mode](https://github.com/lane-s/org-blog-mode): an emacs package that provides an interface for managing posts.
- The frontend: Gets parsed posts from the servers and displays them. An example frontend is on the way.

## Deploying org-blog 

## Development Setup

Requires [leiningen](https://leiningen.org/) and Clojure

## Running Tests

    $ lein test

## Running development server

    $ lein ring server headless

## API

See [REST Api examples](./api_examples.http) to see how the API can be used

## License

Copyright © 2019 Lane Spangler

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
