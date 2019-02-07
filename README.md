# CGI Assignment

**Background** 

Your local bird watching association needs a brand new application where users can share their observations to other users. These observations include name of the species, rarity, notes and timestamp. They also wish that user could include geolocations, pictures, video, sound etc. to the observation. Because observations are usually done in areas where network connection is limited user should be able to record observations while offline. 

**Acceptance Criteria**
 - Main view should include a dynamic list of observations 
	- Observations should be ordered by timestamp 
	- Observation should display name of species, rarity, excerpt of notes, and date and time
 - Main view should include a button for adding new observation 
	- Clicking the button should navigate user to observation form view 
 - In observation form view user should be able to add a new observation 
	- User should be able to input name of the species and notes
	- User should be able to select rarity from predefined list (common, rare, extremely rare) 
	- User should be able to save the observation 
	- Timestamp should be created when observation is saved 
	- User should be able to cancel the new entry. Any data entered by the user will be discarded and the observation will not be saved. 
 - All observations should be saved locally on the device 
	- Saved observations should be persisted when application is closed
	 
**Your task** 

**1.** Choose your preferred technology 	
-  Responsive Web: **Angular, React**  	
-  Cross platform/Hybrid: **React Native, Ionic** 
-  Native: **Android, iOS**

**2.** Create a mobile application that fulfills the given acceptance criteria and at least one of the following bonus features	
- Automated tests (instructions for running tests should be included in Readme file) 
- User is able to change the sorting of the observation list 
- Geolocation saved with observation.  Observation coordinates are shown in the list of observations 
- User is able to attach a picture to the observation. The attached picture is shown in the list of observations. 

**3.** Your solution should include 
- Readme file with instructions for running and building the application 
- Source code 
- APK/IPA file (if applicable) 
- Short description of your solution and experience with the assignment 

**4.** Your solution should be available in GitHub or zipped in some file sharing service (e.g. Dropbox) Send the link to your solution by email, ensuring that the receiver has permissions to access your submission.


## My solution

I chose to do the assignment for Android, using Kotlin. I implemented all the obligatory features and from the bonus features, I chose to add sorting of the observation list and attaching pictures to the observations.

In my main view(image 1.) I use RecylerView to display the observations. You can sort the observations by timestamp or species. You can also select specific rarity. Sorting criteria are stored in SharedPreferences. Observations are saved locally to SQLite database.

<img src="https://github.com/jonitef/CGIAssignment/blob/master/readmeIMG/mainView.jpeg" height="440" width="240"></img>
<br>
image 1.

In observation form view (image 2. and image 3.), you have to ad name of the species and notes. Rarity is set to common in default, you can select rarity from predefined list. You can take a photo or select existing one from the gallery. Image is optional

<img src="https://github.com/jonitef/CGIAssignment/blob/master/readmeIMG/addObservation.jpeg" height="440" width="240"></img>
<br>
image 2.

<img src="https://github.com/jonitef/CGIAssignment/blob/master/readmeIMG/addObservationFilled.jpeg" height="440" width="240"></img>
<br>
image 3.

I enjoyed doing the assignment, it was fun and I managed to implement all the features I had originally planned. Although one thing, image in observations is rotated. I managed to make a solution to prevent that with new images taken by camera, but not with all the images from the gallery. So I decided not to implement something that only half working.

## Build

**1.** Option, clone the repo

    git clone https://github.com/jonitef/CGIAssignment.git
   Then open the project in Android Studio and run on emulator or on your device.

**2.** Option, load the APK file(app.debug.apk) to your device and install it.
