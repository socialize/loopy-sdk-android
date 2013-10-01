import sys, os
source_suffix = '.rst'
master_doc = 'index'
project = u'@name@ Android SDK'
copyright = u'@year@, @company@'
version = '@version@ (b@build@)'
release = '@version@ (b@build@)'
exclude_patterns = []
pygments_style = 'vs'
highlight_language = 'java'
html_theme = 'nature'
html_theme_options = {
    "sidebarwidth": "250",
}
html_favicon = 'favicon.ico'
html_static_path = ['_static']
templates_path=['_templates']
html_sidebars = {
   '**': ['globaltoc.html']
}
html_domain_indices = False
html_use_index = False
html_show_sphinx = False
html_show_copyright = True
html_copy_source=False
html_link_suffix = '.html?v=@version@'