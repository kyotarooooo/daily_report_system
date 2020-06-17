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

@Table(name = "favorites")
@NamedQueries({
    @NamedQuery(
            name = "favoriteExistenceCheck",
            query = "SELECT COUNT(fa) FROM Favorite AS fa WHERE fa.employee = :employee AND fa.report = :report"
            ),
    @NamedQuery(
            name = "getFavoriteTable",
            query = "SELECT fa FROM Favorite AS fa WHERE fa.employee = :employee AND fa.report = :report"
            ),
    @NamedQuery(
            name = "favoriteCheck",
            query = "SELECT COUNT(fa) FROM Favorite AS fa WHERE fa.favorite_flag = 1 AND fa.employee = :employee AND fa.report = :report"
            //employeeとreportを条件に追加する
            ),
    //特定の日報をお気に入りした従業員をすべて取得
    @NamedQuery(
            name = "getFavoriteEmployees",
            query = "SELECT fa FROM Favorite AS fa WHERE fa.report = :report AND fa.favorite_flag = 1 ORDER BY fa.id DESC"
            ),
    //ログインしているユーザーがお気に入りしている日報
    @NamedQuery(
            name = "getFavoriteReportsIndex",
            query = "SELECT fa FROM Favorite AS fa WHERE fa.employee = :employee AND fa.favorite_flag = 1 ORDER BY fa.id DESC"
            )
})

@Entity
public class Favorite {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "favorite_flag", nullable = false)
    private Integer favorite_flag;

    @Column(name = "created_at", nullable = false)
    private Timestamp created_at;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updated_at;

    //ゲッターセッター
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Report getReport() {
        return report;
    }
    public void setReport(Report report) {
        this.report = report;
    }

    public Integer getFavorite_flag() {
        return favorite_flag;
    }
    public void setFavorite_flag(Integer favorite_flag) {
        this.favorite_flag = favorite_flag;
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
