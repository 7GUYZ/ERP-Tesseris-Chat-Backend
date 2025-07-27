package main.java.com.ict.edu03.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name + "chatlog")
@data
@NoArgsConstructor
@AllArgsConstructor
public class ChatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_index", nullable = false)
    /** 로그 번호
     * not null
     * auto increment
     * long bigint
     */
    private Long logindex;
    @Column(name = "message", nullable = false)
    /** 어떤 메세지가 오고 갔는지
     * not null
     */
    private String message;
    @Column(name = "log_type", nullable = false)
    /** 파일인지 텍스트인지
     * not null
     */
    private String logtype;
    @Column(name = "sent_at", nullable = false)
    /** 메세지 보낸 시간
     * not null
     */
    private LocalDateTime sentat;
    @Column(name = "room_index", nullable = false)
    /** 어떤 방에서 보냈는지
     * not null
     * long bigint
     */
    private Long roomindex;
    @Column(name = "user_id", nullable = false)
    /** 누가 보냈는지
     * not null
     */
    private String userid;
}