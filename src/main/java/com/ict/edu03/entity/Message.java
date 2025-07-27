package main.java.com.ict.edu03.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name + "message")
@data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_index", nullable = false)
    /** 메세지 번호
     * not null
     * auto increment
     * long bigint
     */
    private Long messageindex;
    @Column(name = "user_id", nullable = false)
    /** 누가 보냈는지
     * not null
     * 메인서버 uuid
     */
    private String userid;
    @Column(name = "sent_at", nullable = false)
    /** 메세지 보낸 시간
     * not null
     * timestamp
     */
    private LocalDateTime sentat;
    @Column(name = "message", nullable = false)
    /** 메세지 내용
     * not null
     */
    private String message;
    @Column(name = "active", nullable = false , columnDefinition = "TINYINT(1)")
    /** 메세지 활성화 여부
     * not null
     * tinyint(1)
     * true : 활성화 1
     * false : 비활성화 0
     */
    private Integer active;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_index", nullable = false , referencedColumnName = "room_index", insertable = false, updatable = false)
    private Room room;

    @JsonManagedReference
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageReads> messageReads;
}
