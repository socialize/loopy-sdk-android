Using the Share Dialog
======================

.. include:: <isonum.txt>

.. container:: block-padded

    If you currently have a share function in your app you are most likely using the default
    share dialog provided by the Android platform.

    The Loopy |trade| Share Dialog mimics the default Android share dialog but provides important
    callbacks to allow more sophisticated social analytics.

    To implement the Loopy |trade| share dialog, simply use the **showShareDialog** method to create a
    trackable URL for sharing and display the default share dialog.

.. container:: block

    .. image:: images/share_dialog.png
        :align: center

.. container:: clear

    If you don't already have one, add a button to your layout:

    .. literalinclude:: ../../res/layout/snippet_layout.xml
        :start-after: begin-snippet-0
        :end-before: end-snippet-0

    Add an **onClick** event in your Activity:

    .. literalinclude:: ../com/sharethis/loopy/doc/ShareDialogSnippets.java
        :start-after: begin-snippet-0
        :end-before: end-snippet-0