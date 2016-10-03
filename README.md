# <img src="https://raw.githubusercontent.com/artemik/lori-timesheets-android/master/demo/readme-title-image.png" width="35"> Lori Timesheets Android Client 
This is an Android client for the open source timesheets management system [Lori Timesheets](https://github.com/Haulmont/platform-sample-timesheets).

The application lets you manage timesheets - view, add, edit, delete time entries, etc.

Mainly, the application consists of two screens - a week overview screen and a time entry edit dialog (pictures below). A gif video demonstrating the application is in the [demo folder](demo/video-gif.gif).

<img src="https://raw.githubusercontent.com/artemik/lori-timesheets-android/master/demo/main-screenshot.JPG" width="300"> <img src="https://raw.githubusercontent.com/artemik/lori-timesheets-android/master/demo/main-screenshot-adding.JPG" width="300">

The server API is not ready yet, so for demonstration purposes, the application starts a **local mock server** inside Android, to which network requests are issued. Also, login screen is omitted and authentication is not engaged at the moment.

While building the application, it was kept in mind that view/add/edit/remove actions should require as less as possible movements, be at convenient and expected positions on the screen. 

## Main Libraries Used
Dagger, Nucleus (MVP), Retrofit, RxJava, ButterKnife, IcePick, EventBus.