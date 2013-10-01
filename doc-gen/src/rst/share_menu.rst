Using the Share Menu
====================

.. include:: <isonum.txt>
.. |br| raw:: html

   <br />

.. |support_lib| raw:: html

    <a href="http://developer.android.com/tools/support-library/index.html" target="_blank">Android Support Library</a>&nbsp;&#10138;

.. |api_14| raw:: html

    <a href="http://developer.android.com/reference/android/widget/ShareActionProvider.html" target="_blank">Android API 14</a>&nbsp;&#10138;

.. |appcompat| raw:: html

    <a href="http://developer.android.com/reference/android/support/v7/widget/ShareActionProvider.html" target="_blank">appcompat library</a>&nbsp;&#10138;

.. |setup| raw:: html

    <a href="http://developer.android.com/tools/support-library/setup.html" target="_blank">instructions provided by Google</a>&nbsp;&#10138;

.. container:: block-padded

    If you are using the ShareActionProvider available in |api_14|
    (or via the |support_lib|) you can easily
    integrate the Loopy |trade| SDK by using the callback provided by the ShareActionProvider.

    For maximum compatiblity the Loopy |trade| assumes you will be using the version of the ShareActionProvider provided
    in the v7 (or greater) |appcompat|

    .. note::

        |br|
        |br| In order to use the ShareActionProvider using the code below you must include both **android-support-v4.jar**
        and **android-support-v7-appcompat.jar** in the **/libs** path of your app project.

        Refer to the |setup| to add these packages.

.. container:: block

    .. image:: images/share_menu.png
        :align: center

.. container:: clear

    First create an Options Menu in the **res/menu/** folder of your app:

    .. literalinclude:: ../../../demo/res/menu/share_menu.xml
        :start-after: begin-snippet-0
        :end-before: end-snippet-0

    Then override the **onCreateOptionsMenu** method in your activity:

    .. literalinclude:: ../../../demo/src/com/sharethis/loopy/demo/ShareActionProviderActivity.java
        :start-after: begin-snippet-0
        :end-before: end-snippet-0
