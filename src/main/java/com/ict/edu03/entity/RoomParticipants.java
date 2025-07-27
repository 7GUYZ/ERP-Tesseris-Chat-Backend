package main.java.com.ict.edu03.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name + "roomparticipants")
@data
@NoArgsConstructor
@AllArgsConstructor
public class RoomParticipants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomparticipants_index", nullable = false)
    /** 방 참여자 번호
     * not null
     * auto increment
     * long bigint
     */
    private Long roomparticipantsindex;
    @Column(name = "joined_at", nullable = true)
    /** 방 참여 시간
     * not null
     * timestamp
     */
    private LocalDateTime joinedat;
    @Column(name = "left_at", nullable = true)
    /** 방 탈퇴 시간
     * not null
     * timestamp
     */
    private LocalDateTime leftat;
    @Column(name = "notifications_enabled", nullable = false , columnDefinition = "TINYINT(1)")
    /** 알림 활성화 여부
     * not null
     * tinyint(1)
     * true : 활성화 1
     * false : 비활성화 0
     */
    private boolean notificationsenabled;
    @Column(name = "user_id", nullable = false)
    /** 누가 참여했는지
     * not null
     * 메인서버 uuid
     */
    private String userid;
    /** 어떤 방에 참여했는지
     * not null
     * long bigint
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_index", nullable = false , referencedColumnName = "room_index", insertable = false, updatable = false)
    private Room room;
}
