Getting Started
===============

.. include:: <isonum.txt>

.. |br| raw:: html

   <br />

Implementing Loopy |trade| with your app is a simple 4 step process:

1. Install the Library
----------------------
    .. raw:: html

        <h4><a href="javascript:void(toggleDiv('gradle'));">Using Android Studio / Gradle &#10140;</a></h4>

    .. container:: hidden
        :name: gradle

        .. note:: Disclaimer

            At the time of writing Android Studio was still relatively new and some features in Android Studio do not work as
            expected.

            As a result some of the steps below may become unnecessary in future versions of Android Studio.

        Locate the *dist* folder in your Loopy distribution:

        .. image:: images/as_0.png
            :width: 500

        And **copy** the *loopy* folder into your project under the **libraries** folder

        .. image:: images/as_1.png
            :width: 500

        In your Android Studio project, open the **Project Structure** add the library to your project via **Import Module**

        .. container:: cell

            .. image:: images/as_4.png
                :width: 300

        .. container:: cell

            .. image:: images/as_5.png
                :width: 300

        You *may* also need to add the Loopy project as a dependant module in your app

        .. image:: images/as_6.png

        Next, add the library to your **settings.gradle** file in your project (replace HellowWorld with your project name)

        .. literalinclude:: ../snippets/snippets.txt
            :start-after: begin-snippet-3
            :end-before: end-snippet-3
            :language: javascript

        Finally add a compile time dependency to your module's **build.gradle**

        .. literalinclude:: ../snippets/snippets.txt
            :start-after: begin-snippet-4
            :end-before: end-snippet-4
            :language: javascript


    .. raw:: html

        <h4><a href="javascript:void(toggleDiv('maven'));">Using Maven &#10140;</a></h4>

    .. container:: hidden
        :name: maven

        Within the library distribution you will find an **apklib** version of the library that you can install into a local repository

        .. literalinclude:: ../snippets/snippets.txt
            :start-after: begin-snippet-0
            :end-before: end-snippet-0

        Then add a dependency to your pom.xml

        .. literalinclude:: ../snippets/snippets.txt
            :start-after: begin-snippet-1
            :end-before: end-snippet-1
            :language: xml

        Don't forget to include the **android-maven-plugin** (you probably already have this)

        .. literalinclude:: ../snippets/snippets.txt
            :start-after: begin-snippet-2
            :end-before: end-snippet-2
            :language: xml

    .. raw:: html

        <h4><a href="javascript:void(toggleDiv('eclipse'));">Using Eclipse &#10140;</a></h4>

    .. container:: hidden
        :name: eclipse

        Loopy is distributed as a standard Android Library project.  Just follow the instructions from Google
        to learn how to include an Android Library project in your app.

        .. raw:: html

            <a href="http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject" target="_blank">http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject</a>&nbsp;&#10138;

2. Update your AndroidManifest.xml
----------------------------------

Ensure your app supports SDK version 9 and above:

**<manifest ... >**

.. literalinclude:: ../../AndroidManifest.xml
    :start-after: begin-snippet-0
    :end-before: end-snippet-0

Ensure your app has the **INTERNET** permission.  Geo location permissions are optional but recommended for superior analytics

.. literalinclude:: ../../AndroidManifest.xml
    :start-after: begin-snippet-1
    :end-before: end-snippet-1

To enable **Install Tracking** add the InstallTracker receiver

    **<application...>**

    .. literalinclude:: ../../AndroidManifest.xml
        :start-after: begin-snippet-2
        :end-before: end-snippet-2

    **</application>**

**</manifest>**


.. note::

    If your application already defines a broadcast receiver you can simply call Loopy to track the install

    .. raw:: html

        <a href="javascript:void(toggleDiv('broadcast'));">Show me how...</a>

    .. container:: hidden
        :name: broadcast

            .. literalinclude:: ../com/sharethis/loopy/doc/MyBroadcastReceiver.java
                :start-after: begin-snippet-0
                :end-before: end-snippet-0

3. Implement the Loopy lifecycle in your activity
-------------------------------------------------

.. literalinclude:: ../com/sharethis/loopy/doc/MyActivity.java
    :start-after: begin-snippet-0
    :end-before: end-snippet-0


4. Implement a Share Dialog or Share Menu
-----------------------------------------

Loopy integrates with the two most common ways to offer sharing to your app users

.. container:: clear

    |

    .. container:: block

        .. image:: images/share_dialog_small.png
            :align: left

        .. container:: block-small

            .. container:: subtitle

                Share Dialog

            Quick and simple.  Great for novices or first-time app developers.

            :doc:`Show me how... </share_dialog>`

    .. container:: block

        .. image:: images/share_menu_small.png
            :align: left

        .. container:: block-small

            .. container:: subtitle

                Share Menu

            Follow the official Android Style Guidelines and implement an Action Bar with a Share Menu

            :doc:`Show me how... </share_menu>`
