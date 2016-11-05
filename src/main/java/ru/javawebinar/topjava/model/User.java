package ru.javawebinar.topjava.model;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.util.UserUtil;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.util.*;

/**
 * User: gkislin
 * Date: 22.08.2014
 */
@NamedQueries({
        @NamedQuery(name = User.DELETE, query = "DELETE FROM User u WHERE u.id=:id"),
        @NamedQuery(name = User.BY_EMAIL, query = "SELECT u FROM User u WHERE u.email=?1"),
        @NamedQuery(name = User.ALL_SORTED, query = "SELECT u FROM User u ORDER BY u.name, u.email"),
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@NamedEntityGraph(name = User.GRAPH_WITH_MEALS, attributeNodes = {@NamedAttributeNode("meals")})
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "users_unique_email_idx")})
public class User extends NamedEntity {

    public static final String GRAPH_WITH_MEALS = "User.withMeals";

    public static final String DELETE = "User.delete";
    public static final String ALL_SORTED = "User.getAllSorted";
    public static final String BY_EMAIL = "User.getByEmail";

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotEmpty
    private String email;

    @Column(name = "password", nullable = false)
    @NotEmpty
    @Length(min = 5)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "registered", columnDefinition = "timestamp default now()")
    private Date registered = new Date();

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 200)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Role> roles;

    @Column(name = "calories_per_day", columnDefinition = "default 2000")
    @Digits(fraction = 0, integer = 4)
    private int caloriesPerDay = UserUtil.DEFAULT_CALORIES_PER_DAY;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("dateTime DESC")
//    @JsonIgnore
    protected List<Meal> meals;

    public User() {
    }

    public User(User u) {
        this(u.getId(), u.getName(), u.getEmail(), u.getPassword(), u.getCaloriesPerDay(), u.isEnabled(), u.getRoles());
    }

    public User(Integer id, String name, String email, String password, Role role, Role... roles) {
        this(id, name, email, password, UserUtil.DEFAULT_CALORIES_PER_DAY, true, EnumSet.of(role, roles));
    }

    public User(Integer id, String name, String email, String password, int caloriesPerDay, boolean enabled, Set<Role> roles) {
        super(id, name);
        this.email = email;
        this.password = password;
        this.caloriesPerDay = caloriesPerDay;
        this.enabled = enabled;
        setRoles(roles);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCaloriesPerDay() {
        return caloriesPerDay;
    }

    public void setCaloriesPerDay(int caloriesPerDay) {
        this.caloriesPerDay = caloriesPerDay;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? Collections.emptySet() : EnumSet.copyOf(roles);
    }

    public String getPassword() {
        return password;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    @Override
    public String toString() {
        return "User (" +
                "id=" + id +
                ", email=" + email +
                ", name=" + name +
                ", enabled=" + enabled +
                ", roles=" + roles +
                ", caloriesPerDay=" + caloriesPerDay +
                ')';
    }
}
