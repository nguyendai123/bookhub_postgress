package com.thanhson.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private long userID;

    @Column(name = "UserName", length = 50, nullable = false)
    private String username;

    @Column(name = "Email", length = 50, nullable = false)
    private String email;

    @Column(name = "Password", length = 100, nullable = false)
    private String password;

    @Column(name = "Avatar", length = 255)
    private String avatar;

    @Column(name = "Biography")
    private String biography;

    @Column(name = "FavoritteGenres", length = 255)
    private String favoriteGenres;

    @OneToMany(mappedBy = "userProgress")
    private Set<Progress> progress;

    @OneToMany(mappedBy = "userFol")
    private Set<Follow> followUser;

    @OneToMany(mappedBy = "userFollow")
    private Set<Follow> follow;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "UserID", referencedColumnName = "userID"), inverseJoinColumns = @JoinColumn(name = "RoleID", referencedColumnName = "roleID"))
    private Set<Role> roles;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Like> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "createdUser")
    private Set<Group> group;

    @ManyToMany(mappedBy = "users")
    private Set<Group> groups;

    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(long userID, String username, String email, String avatar, String biography, String favoriteGenres) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.biography = biography;
        this.favoriteGenres = favoriteGenres;
    }
}
