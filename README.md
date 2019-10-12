# food-of-tyria

This is a simple web-based lookup tool and tracker for food recipes from Guild Wars. On first run it will initialize itself with information from the GW2 API about what cooking recipes are available, which may take some time. It has no concept of ownership or different users, so it's best used as a local tool or on a small home network. The intent is to let you keep track of which recipes you've made *outside* the game, unlike other trackers that keep track of what recipes your *character* has crafted.

It will probably not be useful to anyone but me, but I've uploaded it as an example of a simple (if somewhat sloppy) Ring/Compojure/Hiccup/Codax application.

By editing the `cookable?` filter in `models/recipes.clj` it could easily be adapted to other sorts of recipes, although if you just want to keep track of which recipes your GW2 character has crafted there are existing websites that do that better by querying the GW2 character API directly.

## Running

    lein ring server

## License

Copyright © 2019 ToxicFrog, released under the Apache 2.0 license
