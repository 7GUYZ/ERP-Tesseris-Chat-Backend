package main.java.com.ict.edu03.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name + "room")
@data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_index", nullable = false)
    /** 방 번호
     * not null
     * auto increment
     * long bigint
     */
    private Long roomindex;
    @Column(name = "room_name", nullable = false)
    /** 방 이름
     * not null
     * uuid
     */
    private String roomname;
    @Column(name = "created_at", nullable = true)
    /** 방 생성 시간
     * not null
     * timestamp
     */
    private LocalDateTime createdat;
    @Column(name = "created_by", nullable = true)
    /** 방 삭제된 시간
     * null
     * timestamp
     */
    private LocalDateTime createdby;

    @JsonManagedReference
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    @JsonManagedReference
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomParticipants> participants;
}
