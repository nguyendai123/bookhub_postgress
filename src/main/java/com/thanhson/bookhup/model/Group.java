package com.thanhson.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "GroupID")
    private Long groupID;

    @Column(name="GroupName")
    private String groupName;

    @Column(name="GroupDescription")
    private String groupDescription;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    @Column(name="PrivacySetting")
    private String privacySetting;

    @Column(name="MemberCount")
    private int memberCount;

    @Column(name="Category")
    private String category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_user",
            joinColumns = {@JoinColumn(name = "GroupID")},
            inverseJoinColumns = {@JoinColumn(name = "UserID")})
    private Set<User> users = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "CreatedByUserID")
    private User createdUser;
}
