SF Movies

A service that shows on a map where movies have been filmed in San Francisco. The user can filter the view by providing a search criteria.

Demo URL: http://dayohack1.appspot.com/

Data Source: The data was gotten from DataSF (https://data.sfgov.org/Culture-and-Recreation/Film-Locations-in-San-Francisco/yitu-d5am). Issues when dealing with the data were: Missing locations, Missing movie titles, Duplicate entries. In addition the data had to be geocoded using Google Geocoding Service in order to convert the location names to proper latitude/longitude combinations to display on a map. Hence, from an original data size of 1622 records, only 1408 were actually usable.

Backend API:
GET /search: Get all locations where movies were filmed in San Francisco.

POST /search?searchQuery=<Search_Criteria>: Get all locations based on the given search criteria. The search can be based on Movie title, San Francisco locations or the Release Year.

The API response for both GET and POST is:
[{
  location:<String>,
  latitude:<Double>,
  longitude:<Double>,
  moviesForLocation:[{
    title:<String>,
    releaseYear:<Integer>
  }]
}]

Technical Specs: Google App Engine SDK 1.9.60, Java, Google Maps JavaScript API, Google Maps Geocoding API, Twitter Bootstrap 4.0.0, Twitter Typeahead, Objectify 5.1.22, Apache Maven 3.5.2, Apache Commons CSV 1.5, Jackson JSON project 2.8.7, Eclipse IDE 4.7.2 (Oxygen), JQuery 3.3.1
