package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(name = "follows")
//フォローしているかしていないかチェック(フォローされている)　結果は1が返ってくる
@NamedQueries({
    @NamedQuery(
            name = "followCheck",
            query = "SELECT COUNT(f) FROM Follow AS f WHERE f.follow = :follow AND f.follower = :follower AND f.follow_flag = 1 ORDER BY f.id DESC"
            )
})


@Entity
public class Follow {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "follow_id", nullable = false)
    private Employee follow;

    //joincolumnを使う理由
    //主キー以外で検索する場合はつける
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private Employee follower;

    @JoinColumn(name = "follow_flag", nullable = false)
    private Integer follow_flag;

    @Column(name = "created_at", nullable = false)
    private Timestamp created_at;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updated_at;

    //getter,setter
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getFollow() {
        return follow;
    }
    public void setFollow(Employee follow) {
        this.follow = follow;
    }


    public Employee getFollower() {
        return follower;
    }
    public void setFollower(Employee follower) {
        this.follower = follower;
    }

    public Integer getFollow_flag() {
        return follow_flag;
    }
    public void setFollow_flag(Integer follow_flag) {
        this.follow_flag = follow_flag;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }
}