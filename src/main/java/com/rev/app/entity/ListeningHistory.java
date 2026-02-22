package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listening_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListeningHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private int historyId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "song_id", nullable = false)
    private int songId;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @Column(name = "action_type")
    private String actionType;
}
