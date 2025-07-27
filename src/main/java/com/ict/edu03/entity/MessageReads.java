package main.java.com.ict.edu03.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name + "messagereads")
@data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messageread_index", nullable = false)
    /** 메세지 읽음 번호
     * not null
     * auto increment
     * long bigint
     */
    private Long messagereadindex;
    @Column(name = "read_at", nullable = false)
    /** 메세지 읽은 시간
     * not null
     * timestamp
     */
    private LocalDateTime readat;
    @Column(name = "user_id", nullable = false)
    /** 누가 읽었는지
     * not null
     * 메인서버 uuid
     */
    private String userid;
    /** 어떤 메세지를 읽었는지
     * not null
     * long bigint
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_index", nullable = false , referencedColumnName = "message_index", insertable = false, updatable = false)
    private Message message;
}
