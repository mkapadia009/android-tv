package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class WatchListItemAttachment implements Parcelable
{

    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("filesize")
    @Expose
    private Integer filesize;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("alt")
    @Expose
    private String alt;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("uploaded_to")
    @Expose
    private Integer uploadedTo;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("menu_order")
    @Expose
    private Integer menuOrder;
    @SerializedName("mime_type")
    @Expose
    private String mimeType;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("subtype")
    @Expose
    private String subtype;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("hls_url")
    @Expose
    private String hlsUrl;
    @SerializedName("duration")
    @Expose
    private String duration;
    public final static Creator<WatchListItemAttachment> CREATOR = new Creator<WatchListItemAttachment>() {


        @SuppressWarnings({
                "unchecked"
        })
        public WatchListItemAttachment createFromParcel(Parcel in) {
            return new WatchListItemAttachment(in);
        }

        public WatchListItemAttachment[] newArray(int size) {
            return (new WatchListItemAttachment[size]);
        }

    }
            ;

    protected WatchListItemAttachment(Parcel in) {
        this.iD = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.filename = ((String) in.readValue((String.class.getClassLoader())));
        this.filesize = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.link = ((String) in.readValue((String.class.getClassLoader())));
        this.alt = ((String) in.readValue((String.class.getClassLoader())));
        this.author = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.caption = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.uploadedTo = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        this.modified = ((String) in.readValue((String.class.getClassLoader())));
        this.menuOrder = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.mimeType = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.subtype = ((String) in.readValue((String.class.getClassLoader())));
        this.icon = ((String) in.readValue((String.class.getClassLoader())));
        this.hlsUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.duration = ((String) in.readValue((String.class.getClassLoader())));
    }

    public WatchListItemAttachment(JSONObject jsonObject) {
        try {
            if (jsonObject.has("ID")) {
                this.iD = jsonObject.getInt("ID");
            }
            if (jsonObject.has("id")) {
                id = jsonObject.getInt("id");
            }
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }
            if (jsonObject.has("filename")) {
                filename = jsonObject.getString("filename");
            }
            if (jsonObject.has("url")) {
                url = jsonObject.getString("url");
            }
            if (jsonObject.has("link")) {
                link = jsonObject.getString("link");
            }
            if (jsonObject.has("alt")) {
                alt = jsonObject.getString("alt");
            }
            if (jsonObject.has("author")) {
                author = jsonObject.getString("author");
            }
            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }
            if (jsonObject.has("caption")) {
                caption = jsonObject.getString("caption");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("icon")) {
                icon = jsonObject.getString("icon");
            }
            if (jsonObject.has("hlsUrl")) {
                hlsUrl = jsonObject.getString("hlsUrl");
            }

        } catch (Exception e) {

        }
    }
    public WatchListItemAttachment() {
    }

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUploadedTo() {
        return uploadedTo;
    }

    public void setUploadedTo(Integer uploadedTo) {
        this.uploadedTo = uploadedTo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Integer getMenuOrder() {
        return menuOrder;
    }

    public void setMenuOrder(Integer menuOrder) {
        this.menuOrder = menuOrder;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHlsUrl() {
        return hlsUrl;
    }

    public void setHlsUrl(String hlsUrl) {
        this.hlsUrl = hlsUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(iD);
        dest.writeValue(id);
        dest.writeValue(title);
        dest.writeValue(filename);
        dest.writeValue(filesize);
        dest.writeValue(url);
        dest.writeValue(link);
        dest.writeValue(alt);
        dest.writeValue(author);
        dest.writeValue(description);
        dest.writeValue(caption);
        dest.writeValue(name);
        dest.writeValue(status);
        dest.writeValue(uploadedTo);
        dest.writeValue(date);
        dest.writeValue(modified);
        dest.writeValue(menuOrder);
        dest.writeValue(mimeType);
        dest.writeValue(type);
        dest.writeValue(subtype);
        dest.writeValue(icon);
        dest.writeValue(hlsUrl);
        dest.writeValue(duration);
    }

    public int describeContents() {
        return 0;
    }

}
