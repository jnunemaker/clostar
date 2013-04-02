# clostar

Clojure app that pulls in star events from your timeline so you can see what your friends are starring.

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

# setup the url where your app is hosted, either the heroku url or a custom one
heroku config:add APP_URL=http://heroku-app-name.herokuapp.com

# deploy the app to heroku
git push heroku master
```

## License

Copyright © 2013 John Nunemaker

Distributed under the Eclipse Public License, the same as Clojure.
