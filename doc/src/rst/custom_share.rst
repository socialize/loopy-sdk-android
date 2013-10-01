Using a Custom Share UI
=======================

.. include:: <isonum.txt>

.. container:: block-padded

    If you have your own custom implementation of a share UI you can still benefit from the Loopy |trade| social
    analytics platform by creating a trackable URL to be shared.

    Simply use the **shorten** method to create a trackable URL for sharing when the user executes a share.

.. container:: block

    .. image:: images/custom_ui.png
        :align: center

.. container:: clear

   When the user elects to share using your custom UI:

    .. literalinclude:: ../com/sharethis/loopy/doc/CustomShareSnippets.java
        :start-after: begin-snippet-0
        :end-before: end-snippet-0