import json
from google.appengine.ext import ndb
from google.appengine.api.logservice import logservice
import logging

class Tak(ndb.Model):
	lat = ndb.StringProperty() # latitude
	lng = ndb.StringProperty() # longitude
	creator = ndb.StringProperty()