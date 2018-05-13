package eu.wise_iot.wanderlust.models.DatabaseModel;

public class UserComment {

    private long com_id;
    private String text;
    private String updatedAt;
    private String createdAt;
    private String nickname;
    private long tour;
    private long user;

    public UserComment(long com_id, String text, String updatedAt,
                       String createdAt, String nickname, long tour, long user) {
        this.com_id = com_id;
        this.text = text;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.nickname = nickname;
        this.tour = tour;
        this.user = user;
    }
    public long getCom_id() {
        return com_id;
    }

    public void setCom_id(long com_id) {
        this.com_id = com_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getTour() {
        return tour;
    }

    public void setTour(long tour) {
        this.tour = tour;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }
}
