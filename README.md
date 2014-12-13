RemindMe.
=========
RemindMe. is an android application, which stores reminders. It's integrated with android wear, so that notifications are also pushed to the smart watches. RemindMe. aims to be the simplest to-do app with simple features, and easy to understand UI. It follows Android's Holo Interface. App has separate interface for portrait and landscape

User Interface is divided into four components, which organizes reminders into separate groups

1. Today
2. Next 7 days
3. This month
4. All 

![MainActivity](/screenshots/MainActivity.png)

There are four actions for notifications

1. Tasks can be marked as done from the watch itself
2. Tasks can be emailed to a friend (Uses JavaMail API)
3. Share interface can be brought up from the watch itself
4. Finally, open the app on the phone (default)
![Android Wear Phases](/screenshots/AndroidWear.png)

App also demonstrates the use of Google Voice API to set reminders using voice (speech-to-text conversion)

App uses JavaMail API to login to an email account, which can later be used send tasks to friends via email, from smartwatch as well as the notification action button.

![LoginActivity](/screenshots/LoginActivity.png)
