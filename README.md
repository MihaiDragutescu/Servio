# Servio

Servio is an Android mobile app that aims to facilitate the process of taking and serving orders in a restaurant, to streamline the communication between the staff (waiters - chefs) and allow easier management of halls, employees and menu. The app differentiates between 3 types of users: administrator, waiter and chef. Each of them has access to a customized version of the app through which they can perform specific actions.

Technologies used: Java, XML, Cloud Firestore.

## Table of contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Features](#features)
- [Usage](#usage)
   

## Prerequisites

To install and run the project on your Android device, you will need to have the following prerequisites:

1. Android Studio: You must have Android Studio installed on your computer.
2. Java Development Kit (JDK): You must have the Java Development Kit (JDK) installed on your computer.
3. Android SDK: You must have the Android Software Development Kit (SDK) installed on your computer.
4. USB Debugging: You must enable USB debugging on your Android device.
5. Developer Options: You must also enable the "Developer options" setting on your Android device.

## Installation

[(Back to top)](#table-of-contents)

1. Clone the repository to your local machine or by downloading the repository as a ZIP file.
2. Open the project with Android Studio.
3. Connect your Android device to your computer using a USB cable.
4. In Android Studio, click on the "Run" button in the toolbar.
5. Select your connected Android device from the list of available devices.
6. Wait for the app to build and install on your device. Once the installation is complete, the app should automatically launch on your device.

## Features

[(Back to top)](#table-of-contents)

The main features are split in three categories according to the role of the user that is using the app.

#### Administrator version features:
* Employees management.
* Accounts creation for employees.
* Restaurant menu management.
* Restaurant rooms organization.

#### Waiter version features:
* Visualization of the restaurant rooms, available tables and orders in progress.
* Taking orders and sending them on to the chef.

#### Chef version features:
* Visualization of the orders in progress.
* Signaling the start of orders preparation.
* Completion of order preparation.

## Usage

[(Back to top)](#table-of-contents)

The app launches with the log in screen. You will first need to create a new account for your restaurant. After that you will be automatically logged in and redirected to the main menu for the administrator version of the app.

<p float="middle">
   <img src="https://user-images.githubusercontent.com/37268151/227045100-a0f1a0e9-1740-4f6d-9d3d-057820cb5b92.png" width="33%" />
   <img src="https://user-images.githubusercontent.com/37268151/227047446-56b798e2-2827-4a9e-b965-31a32bb99910.png" width="33%" />
   <img src="https://user-images.githubusercontent.com/37268151/227047460-7addbdb0-f157-4231-b182-7fb47d171992.png" width="33%" />
</p>

Now since you are logged in as the administrator you are allowed to manage your employees, restaurant menu and the restaurant rooms. To access the versions of the app for waiter or chef you need to create the profiles and reconnect to the app with the credentials that you set.

A few screenshots from the administrator version of the app:

<p float="middle">
   <img src="https://user-images.githubusercontent.com/37268151/227050989-b457bb7a-adff-428b-9da6-f5b7ea21cfe2.png" width="33%" />
   <img src="https://user-images.githubusercontent.com/37268151/227051007-bc96cd58-c277-4a92-bf81-f639b4afb355.png" width="33%" />
   <img src="https://user-images.githubusercontent.com/37268151/227051016-10de3ffe-53b6-4c68-8195-563ca334dce1.png" width="33%" />
</p>

<p float="middle">
   <img src="https://user-images.githubusercontent.com/37268151/227053624-455a388f-c3c9-4980-b8d8-43cd363e09c1.jpg" width="49.7%" />
   <img src="https://user-images.githubusercontent.com/37268151/227053660-0ceae8b1-501e-4a83-a167-083348f27326.jpg" width="49.7%" />
</p>

Screenshots from the waiter version of the app:

<p float="middle">
   <img src="https://user-images.githubusercontent.com/37268151/227056248-912bb398-713b-47c8-8d7f-6ec6d1635909.jpg" width="49.7%" />
   <img src="https://user-images.githubusercontent.com/37268151/227056140-2e06aa3a-274a-4c6e-b492-0cd13a6112b1.jpg" width="49.7%" />
</p>

Screenshot from the chef version of the app:

<p float="middle">
   <img src="https://user-images.githubusercontent.com/37268151/227057327-c6ef27ef-3f01-4ce5-8142-10d6562b81c6.jpg" width="49.7%" />
</p>

