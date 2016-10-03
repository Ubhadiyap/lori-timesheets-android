# <img src="https://raw.githubusercontent.com/artemik/lori-timesheets-android/master/demo/readme-title-image.png" width="35"> Lori Timesheets Android Client 
The application lets you manage your Lori time sheets - view, add, edit, delete time entries, etc.

Mainly, the application consists of two screens - a week overview and a time entry edit dialog (pictures below). A gif video demonstrating the application is in the [demo folder](demo/video-gif.gif).

<img src="https://raw.githubusercontent.com/artemik/lori-timesheets-android/master/demo/main-screenshot.JPG" width="300"> <img src="https://raw.githubusercontent.com/artemik/lori-timesheets-android/master/demo/main-screenshot-adding.JPG" width="300">

The server API is not ready yet, so for demonstration purposes, the application starts a **local mock server**, to which it thens issues network requests. Also, login screen is not shown and authentication is not engaged at the moment.

## Main Libraries Used
Dagger, Nucleus (MVP), Retrofit, RxJava, ButterKnife, IcePick, EventBus