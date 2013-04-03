# clostar

**This is really nothing you care about**. I'm playing with clojure and thought interacting with Mongo and the GitHub API would be fun.

Given a GitHub username, this app pulls in all received events related to you or someone you follow starring a repo. It also pulls in any of your repos that get starred.

Eventually, I think this could be really cool. Right now it has no pagination, so it is probably useless.

You can see what it looks like for me here:
http://shrouded-island-2540.herokuapp.com/

## Install

```
# get the repo first
git clone https://github.com/jnunemaker/clostar.git

# hop into the repo, the water is nice...
cd clostar

# create an app on heroku
heroku create

# setup which username we should slurp stars from (ie: jnunemaker)
heroku config:add CLOSTAR_USERNAME=your-github-username

# create mongo database with one of the hosted providers and set url here
heroku config:add MONGODB_URL=mongodb://...

# if you've never created the database before, add a quick unique index
MONGODB_URL=mongodb://... lein add-indexes

# setup the url where your app is hosted, either the heroku url or a custom one
heroku config:add APP_URL=http://heroku-app-name.herokuapp.com

# deploy the app to heroku
git push heroku master
```

## Thanks

Thanks to [@derekgr](https://github.com/derekgr) for pairing with me on this.

## License

Copyright © 2013 John Nunemaker

Distributed under the Eclipse Public License, the same as Clojure.
