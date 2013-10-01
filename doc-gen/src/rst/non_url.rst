Sharing Non-URL Content
=======================

.. |br| raw:: html

   <br />

.. |og| raw:: html

    <a href="http://ogp.me/" target="_blank">Open Graph</a>&nbsp;&#10138;


.. container:: block-padded

    If your app has content that does not live on the web (i.e. it does not have URL)
    you can still allow your users to share this content using a non-URL share.

    When the share occurs we will create a webpage for you that renders the content you
    include in the share item.

    Non-URL components of the share item correspond to |og| tags automatically added
    to the page we create so that post to social networks like Facebook render with the correct
    text and images you intend.

    .. note::

        |br| You can also use the **Item** described below even if you *do* have a URL to add more meaning to your analytics


.. container:: block

    .. image:: images/og_logo.png
        :align: center

.. container:: clear

    The example below is the same as the :doc:`Share Dialog </share_dialog>` example but rather than using a URL for
    the share data you would use an **Item**

    **Instead** of this:

    .. container:: strikethrough

        .. literalinclude:: ../com/sharethis/loopy/doc/ShareDialogSnippets.java
                :start-after: begin-snippet-2
                :end-before: end-snippet-2

    Use this:

    .. literalinclude:: ../com/sharethis/loopy/doc/ShareDialogSnippets.java
            :start-after: begin-snippet-3
            :end-before: end-snippet-3

    The full example looks like this:

    .. literalinclude:: ../com/sharethis/loopy/doc/ShareDialogSnippets.java
            :start-after: begin-snippet-1
            :end-before: end-snippet-1
