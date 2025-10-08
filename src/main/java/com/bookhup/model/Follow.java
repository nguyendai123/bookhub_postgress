package com.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="follows")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "FollowID", nullable = false)
    private long followID;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User userFol;

    @ManyToOne
    @JoinColumn(name = "FollowerUserID")
    private User userFollow;

    @Column(name = "Status", nullable = false)
    private String status;

    
}
