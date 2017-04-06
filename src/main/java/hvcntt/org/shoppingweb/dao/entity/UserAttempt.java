package hvcntt.org.shoppingweb.dao.entity;import javax.persistence.*;import java.io.Serializable;import java.util.Date;import java.util.UUID;@Entity@Table(name="user_attempts")public class UserAttempt implements Serializable {    private static final long serialVersionUID = 6386194692328761683L;    @Id    @Column(name = "iduserattempts")    @GeneratedValue(strategy = GenerationType.IDENTITY)    private String id;    private int attempts = 0;    private Date lastModified;    @Column(name="username")    private String username;    public UserAttempt() {    }    public UserAttempt(String username, int attempts, Date lastModified) {        setId(UUID.randomUUID().toString());        this.username = username;        this.attempts = attempts;        this.lastModified = lastModified;    }    public UserAttempt(String username, int attempts) {        super();        this.username = username;        this.attempts = attempts;    }    public int getAttempts() {        return this.attempts;    }    public void setAttempts(int attempts) {        this.attempts = attempts;    }    public Date getLastModified() {        return this.lastModified;    }    public void setLastModified(Date lastModified) {        this.lastModified = lastModified;    }    public String getUsername() {        return username;    }    public void setUsername(String username) {        this.username = username;    }    public String getId() {        return id;    }    public void setId(String id) {        this.id = id;    }}