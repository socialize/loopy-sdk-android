package com.sharethis.loopy.sdk;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents an item being shared.
 * @author Jason Polites
 */
public class Item {

    String type = "website";
    String url;
    String title;
    String imageUrl;
    String description;
    String videoUrl;
    Set<String> tags;

    String shortlink;

    /**
     * The "og:type" corresponding to the content being shared.
     * @return og:type
     */
    public String getType() {
        return type;
    }

    /**
     * Optional.
     * Corresponds to the og:type meta element. Default is 'website'
     * @param type The type of the content being shared (og:type)
     * @see <a href="http://ogp.me/#types" target="_blank">http://ogp.me/#types</a>
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Optional. (MUST specify one of url or title!)
     * The url of the content being shared.  If the content does not have a url, you must at least provide a title.
     * @param url The url of the content being shared.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Optional. (MUST specify one of url or title!)
     * The title of the content being shared.  If the content does not have a url, you must at least provide a title.
     * @param title The title of the content being shared.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Optional. Corresponds to og:image
     * @param imageUrl An image url corresponding to the content being shared.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Optional. Corresponds to og:description
     * @param description The description of the content being shared.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * Optional. Corresponds to og:video
     * @param videoUrl A video url corresponding to the content being shared.
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    /**
     * @return The custom tags associated with the item
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Optional.
     * @param tags Any custom tags associated with the item
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getShortlink() {
        return shortlink;
    }

    void setShortlink(String shortlink) {
        this.shortlink = shortlink;
    }

    public synchronized void addTag(String tag) {
        if(tags == null) {
            tags = new LinkedHashSet<String>();
        }
        tags.add(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (title != null ? !title.equals(item.title) : item.title != null) return false;
        if (url != null ? !url.equals(item.url) : item.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if(url != null) {
            return url;
        }
        else if(title != null) {
            return title;
        }
        return super.toString();
    }
}
