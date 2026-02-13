package com.bookhup.controller.service;

import com.bookhup.dto.request.shelf.ReadingAddRequest;
import com.bookhup.model.*;
import com.bookhup.repository.*;
import com.bookhup.service.CommentService;
import com.bookhup.service.LikeService;
import com.bookhup.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class DevPostService {

    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReadingProgressRepository readingRepo;
    private final ReadingService readingService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final ShareRepository shareRepo;


    private final Random random = new Random();
    private final ExecutorService imageExecutor =
            Executors.newFixedThreadPool(8); // giới hạn 5 luồng

    // ================= CONTENT MAP (VI -> EN) =================
    private final Map<String, String> contentMap = Map.ofEntries(
            Map.entry("Cuốn này đọc càng về sau càng cuốn.", "The further I read, the more hooked I got."),
            Map.entry("Văn phong tác giả nhẹ nhàng mà thấm thật sự.", "The author's writing style is gentle yet deeply touching."),
            Map.entry("Đọc chương này mà tim đập nhanh luôn.", "My heart was racing while reading this chapter."),
            Map.entry("Không ngờ nội dung lại sâu sắc đến vậy.", "I didn’t expect the story to be this profound."),
            Map.entry("Cuốn sách này làm mình suy nghĩ khác đi rất nhiều.", "This book changed the way I think in many ways."),
            Map.entry("Mỗi chương đều để lại một cảm xúc riêng.", "Each chapter leaves a unique emotion."),
            Map.entry("Đúng kiểu sách càng đọc càng thấm.", "This is the kind of book that sinks in the more you read."),
            Map.entry("Nhân vật phụ mà cũng có chiều sâu ghê.", "Even the supporting characters have great depth."),
            Map.entry("Cảm giác như tác giả đang kể chuyện cho riêng mình vậy.", "It feels like the author is telling the story just for me."),
            Map.entry("Đọc đoạn này mà phải gập sách lại suy nghĩ.", "I had to close the book for a moment to reflect on this part."),
            Map.entry("Một cuốn sách chữa lành đúng nghĩa.", "A truly healing book."),
            Map.entry("Đọc xong chỉ muốn giới thiệu cho mọi người liền.", "After finishing, I just wanted to recommend it to everyone."),
            Map.entry("Không khí trong truyện được xây dựng quá đỉnh.", "The atmosphere in the story is incredibly well-built."),
            Map.entry("Đây là cuốn sách khiến mình thức khuya mấy hôm liền.", "This is the book that kept me up late for several nights."),
            Map.entry("Tình tiết chậm mà không hề chán.", "The pacing is slow but never boring."),
            Map.entry("Đọc mà thấy bản thân mình đâu đó trong nhân vật.", "I see a bit of myself in the characters."),
            Map.entry("Câu chữ đơn giản nhưng cảm xúc rất mạnh.", "The words are simple but emotionally powerful."),
            Map.entry("Đoạn kết làm mình suy nghĩ mãi không thôi.", "The ending left me thinking for a long time."),
            Map.entry("Đúng kiểu sách đọc xong là nhớ hoài.", "The kind of book you remember long after finishing."),
            Map.entry("Cảm xúc khi đọc chương này thật sự rất khó tả.", "The emotions from this chapter are hard to describe."),
            Map.entry("Cuốn này mà không đọc thì tiếc lắm luôn.", "You’d really miss out if you didn’t read this book."),
            Map.entry("Đọc tới đây là mình bắt đầu không thể dừng lại.", "From this point on, I couldn’t stop reading."),
            Map.entry("Từng đoạn nhỏ cũng đủ làm mình rung động.", "Even small passages were enough to move me."),
            Map.entry("Cuốn này đúng kiểu đọc để ngẫm.", "This book is perfect for thoughtful reading."),
            Map.entry("Không cần quá kịch tính mà vẫn rất cuốn.", "It’s not overly dramatic but still incredibly engaging."),
            Map.entry("Tác giả xây dựng tâm lý nhân vật quá tốt.", "The author portrays the characters’ psychology brilliantly."),
            Map.entry("Càng đọc càng thấy thương nhân vật hơn.", "The more I read, the more I sympathized with the character."),
            Map.entry("Một cuốn sách nhẹ nhàng nhưng để lại dư âm lâu dài.", "A gentle book that leaves a lasting impression."),
            Map.entry("Đọc đoạn này mà mình phải dừng lại thở nhẹ một cái.", "I had to pause and take a breath at this part."),
            Map.entry("Sách mà làm mình vừa đọc vừa mỉm cười.", "This book made me smile while reading."),
            Map.entry("Đọc tới khúc này mà nổi da gà thật sự.", "I literally got goosebumps at this part."),
            Map.entry("Từng chương như từng lát cắt cuộc sống.", "Each chapter feels like a slice of life."),
            Map.entry("Một cuốn sách càng đọc càng thấy đáng giá.", "A book that feels more valuable with every page."),
            Map.entry("Cảm xúc cứ tăng dần theo từng trang sách.", "The emotions keep building with every page."),
            Map.entry("Đúng kiểu sách đọc xong là muốn đọc lại lần nữa.", "The kind of book that makes you want to reread it."),
            Map.entry("Cuốn này đọc xong là chỉ muốn ngồi yên một lúc.", "After finishing, I just wanted to sit quietly for a while."),
            Map.entry("Một cuốn sách khiến mình thấy lòng dịu lại.", "A book that calmed my heart."),
            Map.entry("Đọc đoạn này mà thấy lòng mình nặng trĩu.", "This part made my heart feel heavy."),
            Map.entry("Cuốn này đúng kiểu càng đọc càng thấy thương đời hơn.", "This book makes you feel more compassionate toward life."),
            Map.entry("Đọc mà cứ phải đánh dấu lại những câu hay.", "I kept bookmarking beautiful quotes while reading."),
            Map.entry("Một cuốn sách rất hợp để đọc vào buổi tối.", "A perfect book for late-night reading."),
            Map.entry("Câu chuyện đơn giản mà cảm xúc thì không hề đơn giản.", "The story is simple but the emotions are not."),
            Map.entry("Đọc đoạn này mà mình phải đọc chậm lại.", "I had to slow down while reading this part."),
            Map.entry("Sách mà khiến mình muốn sống chậm lại một chút.", "This book makes me want to slow down in life."),
            Map.entry("Cuốn này đọc xong là thấy lòng nhẹ hơn hẳn.", "After reading, I felt much lighter."),
            Map.entry("Đọc chương này mà thấy như đang xem phim vậy.", "Reading this chapter felt like watching a movie."),
            Map.entry("Càng đọc càng thấy thương cho số phận nhân vật.", "The more I read, the more I felt for the character’s fate."),
            Map.entry("Một cuốn sách rất đáng để suy ngẫm.", "A book truly worth reflecting on."),
            Map.entry("Đọc mà nhiều lúc phải dừng lại vì xúc động.", "I had to stop reading at times because I was so moved."),
            Map.entry("Không ngờ một cuốn sách mỏng mà lại sâu như vậy.", "Didn’t expect such a thin book to be this deep."),
            Map.entry("Cuốn này đọc xong là muốn ôm ai đó liền.", "After finishing, I just wanted to hug someone."),
            Map.entry("Một cuốn sách nhỏ nhưng mang nhiều ý nghĩa lớn.", "A small book with big meanings."),
            Map.entry("Đọc mà thấy lòng mình dịu xuống từng chút.", "Reading this slowly calmed my heart."),
            Map.entry("Cuốn này đúng kiểu đọc xong là trưởng thành hơn một chút.", "This book makes you grow up a little after reading."),
            Map.entry("Từng trang sách đều mang lại một cảm giác rất riêng.", "Each page brings a unique feeling."),
            Map.entry("Một cuốn sách đọc mà thấy bản thân mình trong đó.", "A book where I saw myself reflected."),
            Map.entry("Đọc mà cứ thấy tiếc vì sách ngắn quá.", "I kept wishing the book was longer."),
            Map.entry("Cuốn này đọc mà cảm giác thời gian trôi nhanh hơn hẳn.", "Time flew by while reading this book."),
            Map.entry("Đúng kiểu sách đọc để chữa lành tâm hồn.", "The kind of book that heals your soul."),
            Map.entry("Đọc mà thấy mọi thứ xung quanh chậm lại.", "Reading this made everything around me slow down."),
            Map.entry("Cuốn này đúng kiểu đọc một lần là không đủ.", "This book definitely deserves more than one read."),
            Map.entry("Càng đọc càng thấy trân trọng những điều nhỏ bé.", "The more I read, the more I appreciated little things."),
            Map.entry("Một cuốn sách khiến mình phải nhìn lại bản thân.", "A book that made me reflect on myself."),
            Map.entry("Đọc mà thấy thương cả những nhân vật mình từng ghét.", "I even started to feel for characters I used to dislike."),
            Map.entry("Cuốn này đọc xong là cảm giác khó quên thật sự.", "Finishing this book left an unforgettable feeling."),
            Map.entry("Đọc tới đây mà chỉ muốn ngồi yên và suy nghĩ.", "This part made me just want to sit quietly and think."),
            Map.entry("Một cuốn sách mang lại cảm giác rất bình yên.", "A book that brings a peaceful feeling."),
            Map.entry("Đọc mà cứ phải dừng lại để lưu lại câu hay.", "I kept pausing to save beautiful lines."),
            Map.entry("Cuốn này đọc vào ngày mưa chắc hợp lắm.", "This book would be perfect for a rainy day."),
            Map.entry("Đọc mà thấy lòng nhẹ nhàng hơn rất nhiều.", "Reading this made my heart feel much lighter."),
            Map.entry("Một cuốn sách khiến mình thấy yêu cuộc sống hơn.", "A book that made me love life more."),
            Map.entry("Đọc mà có cảm giác như được an ủi vậy.", "Reading this felt comforting."),
            Map.entry("Cuốn này đọc xong là chỉ muốn im lặng một lúc.", "After finishing, I just wanted some quiet time."),
            Map.entry("Một cuốn sách rất hợp để đọc khi cần chút bình yên.", "A perfect book for when you need some peace."),
            Map.entry("Đọc mà thấy những điều nhỏ bé cũng trở nên quan trọng hơn.", "It made small things feel more important."),
            Map.entry("Cuốn này đúng kiểu đọc càng lớn càng thấm.", "This book hits deeper the older you get."),
            Map.entry("Đọc mà có cảm giác được lắng nghe.", "Reading this felt like being understood."),
            Map.entry("Một cuốn sách đọc mà thấy lòng mình mềm lại.", "A book that softened my heart."),
            Map.entry("Đọc mà chỉ muốn gửi cho ai đó cùng đọc chung.", "I just wanted to send this book to someone to read together."),
            Map.entry("Cuốn này đọc xong là chỉ muốn ngồi viết vài dòng cảm nhận.", "After finishing, I just wanted to write down my thoughts."),
            Map.entry("Một cuốn sách khiến mình phải dừng lại giữa guồng quay cuộc sống.", "A book that made me pause amidst life’s rush."),
            Map.entry("Đọc mà thấy mình sống chậm lại một nhịp.", "Reading this slowed my pace of life."),
            Map.entry("Cuốn này đọc xong là cảm giác rất trọn vẹn.", "Finishing this book felt truly fulfilling."),
            Map.entry("Một cuốn sách đọc mà thấy lòng mình ấm hơn.", "A book that warmed my heart."),
            Map.entry("Đọc mà thấy mọi cảm xúc như được gọi tên.", "Reading this felt like my emotions were being named."),
            Map.entry("Cuốn này đúng kiểu đọc xong là không nỡ gấp sách lại.", "I didn’t even want to close the book after finishing."),
            Map.entry("Một cuốn sách khiến mình muốn đọc chậm lại từng trang.", "A book that made me slow down and savor each page."),
            Map.entry("Đọc mà cảm giác như từng câu chữ đang chạm vào mình.",
                    "Reading this felt like every word was touching my heart."),

            Map.entry("Cuốn này đúng kiểu đọc càng chậm càng hay.",
                    "This is the kind of book that gets better the slower you read."),

            Map.entry("Đọc đến đây mà mình phải thở dài một cái.",
                    "I had to let out a sigh when I reached this part."),

            Map.entry("Từng chi tiết nhỏ đều được chăm chút rất kỹ.",
                    "Every little detail is crafted so carefully."),

            Map.entry("Cuốn sách này mang lại cảm giác rất thật.",
                    "This book feels incredibly real."),

            Map.entry("Đọc mà cứ thấy lòng mình dịu lại từng chút một.",
                    "Reading this gradually calmed my heart."),

            Map.entry("Càng đọc càng bị cuốn vào thế giới trong sách.",
                    "The more I read, the more I got drawn into its world."),

            Map.entry("Một cuốn sách không ồn ào nhưng rất sâu.",
                    "A quiet book, yet deeply profound."),

            Map.entry("Đọc đoạn này mà chỉ muốn đọc lại thêm lần nữa.",
                    "I just wanted to reread this part again."),

            Map.entry("Cuốn này đúng kiểu đọc xong là phải suy nghĩ rất lâu.",
                    "This is the kind of book that makes you think for a long time after finishing."),

            Map.entry("Câu chuyện tưởng đơn giản mà lại rất ám ảnh.",
                    "The story seems simple but is surprisingly haunting."),

            Map.entry("Đọc mà thấy như đang được an ủi vậy.",
                    "Reading this felt comforting."),

            Map.entry("Cuốn này làm mình thay đổi góc nhìn về nhiều thứ.",
                    "This book changed my perspective on many things."),

            Map.entry("Đọc tới đoạn này mà tim mình nặng lại.",
                    "My heart felt heavy at this part."),

            Map.entry("Một cuốn sách càng nghĩ lại càng thấy hay.",
                    "The more I reflect on it, the better it feels."),

            Map.entry("Đọc mà thấy từng cảm xúc rất rõ ràng.",
                    "Every emotion felt so clear while reading."),

            Map.entry("Cuốn này đúng kiểu càng đọc càng thấy thương nhân vật.",
                    "The more I read, the more I cared about the characters."),

            Map.entry("Đọc mà nhiều lúc phải đặt sách xuống một chút.",
                    "At times I had to put the book down for a moment."),

            Map.entry("Câu chữ không cầu kỳ nhưng rất chạm.",
                    "The writing isn’t fancy but deeply touching."),

            Map.entry("Cuốn sách này khiến mình muốn đọc chậm lại.",
                    "This book made me want to slow down."),

            Map.entry("Đọc mà cảm giác như mình đang sống trong khoảnh khắc đó.",
                    "It felt like I was living in that very moment."),

            Map.entry("Một cuốn sách mang lại nhiều suy ngẫm về cuộc sống.",
                    "A book that offers many reflections about life."),

            Map.entry("Đọc tới đây mà mình thấy lòng dịu hẳn.",
                    "This part made my heart feel calmer."),

            Map.entry("Cuốn này đọc mà không cần quá nhiều drama vẫn rất cuốn.",
                    "It’s engaging without needing too much drama."),

            Map.entry("Từng chương đều có một sức hút riêng.",
                    "Each chapter has its own charm."),

            Map.entry("Đọc mà cảm giác như đang nghe ai đó tâm sự.",
                    "It felt like someone was confiding in me."),

            Map.entry("Cuốn này đúng kiểu đọc xong là nhớ mãi một câu nói.",
                    "You’ll remember at least one quote long after finishing."),

            Map.entry("Đọc mà thấy mình trưởng thành hơn một chút.",
                    "Reading this made me feel a bit more mature."),

            Map.entry("Một cuốn sách khiến mình muốn nhìn mọi thứ tích cực hơn.",
                    "A book that makes you see things more positively."),

            Map.entry("Đọc đoạn này mà thấy thương cả chính mình.",
                    "This part made me feel compassion for myself."),

            Map.entry("Cuốn này đọc mà thấy lòng ấm lại.",
                    "This book warmed my heart."),

            Map.entry("Đọc mà cảm giác thời gian như chậm lại.",
                    "Time seemed to slow down while reading."),

            Map.entry("Một cuốn sách rất hợp để đọc vào những ngày buồn.",
                    "A perfect book for gloomy days."),

            Map.entry("Đọc mà thấy từng cảm xúc lan tỏa rất rõ.",
                    "The emotions spread so clearly while reading."),

            Map.entry("Cuốn này đúng kiểu đọc một mình vào buổi tối là thấm lắm.",
                    "This book hits differently when read alone at night."),

            Map.entry("Đọc mà thấy cuộc sống nhẹ nhàng hơn một chút.",
                    "Reading this made life feel a bit lighter."),

            Map.entry("Một cuốn sách khiến mình phải nhìn lại hành trình của mình.",
                    "A book that made me reflect on my own journey."),

            Map.entry("Đọc đoạn này mà chỉ muốn ngồi yên thật lâu.",
                    "This part made me want to sit quietly for a long time."),

            Map.entry("Cuốn này đọc mà thấy từng câu chữ đều có ý nghĩa.",
                    "Every sentence feels meaningful."),

            Map.entry("Đọc mà cảm giác như được thấu hiểu.",
                    "Reading this felt like being understood."),

            Map.entry("Một cuốn sách không quá dài nhưng rất đậm.",
                    "Not too long, but very impactful."),

            Map.entry("Đọc mà thấy lòng mình mềm lại.",
                    "It softened my heart while reading."),

            Map.entry("Cuốn này đúng kiểu đọc xong là không quên được.",
                    "This is the kind of book you won’t forget."),

            Map.entry("Đọc mà cảm giác như đang được ai đó lắng nghe.",
                    "It felt like someone was listening to me."),

            Map.entry("Một cuốn sách khiến mình muốn sống chậm hơn.",
                    "A book that makes you want to slow down in life."),

            Map.entry("Đọc đoạn này mà thấy mọi thứ thật hơn bao giờ hết.",
                    "This part made everything feel more real than ever."),

            Map.entry("Cuốn này đọc mà thấy mình học được nhiều điều.",
                    "I learned so much from this book."),

            Map.entry("Đọc mà cảm giác như từng trang đều có hồn.",
                    "Every page feels alive."),

            Map.entry("Một cuốn sách nhỏ nhưng dư âm rất dài.",
                    "A small book with a long-lasting echo."),

            Map.entry("Đọc mà thấy lòng mình yên lại.",
                    "It made my heart peaceful."),

            Map.entry("Cuốn này đúng kiểu đọc xong là muốn ôm sách lại.",
                    "After finishing, I just wanted to hold the book close."),

            Map.entry("Đọc mà thấy từng khoảnh khắc đều đáng giá.",
                    "Every moment in it feels worthwhile."),

            Map.entry("Một cuốn sách khiến mình suy nghĩ về những điều rất nhỏ.",
                    "A book that makes you think about the little things."),

            Map.entry("Đọc mà cảm giác như được tiếp thêm năng lượng.",
                    "Reading this felt energizing."),

            Map.entry("Cuốn này đọc mà không hề thấy mệt.",
                    "Reading this never felt tiring."),

            Map.entry("Đọc đoạn này mà thấy lòng mình chùng xuống.",
                    "This part made my heart sink a little."),

            Map.entry("Một cuốn sách khiến mình phải dừng lại giữa dòng suy nghĩ.",
                    "A book that interrupts your thoughts in a good way."),

            Map.entry("Đọc mà thấy bản thân mình trong từng nhân vật.",
                    "I saw myself in every character."),

            Map.entry("Cuốn này đúng kiểu đọc xong là muốn cảm ơn tác giả.",
                    "After finishing, I just wanted to thank the author."),

            Map.entry("Đọc mà thấy từng trang đều rất chân thành.",
                    "Every page feels sincere."),

            Map.entry("Một cuốn sách khiến mình muốn đọc thêm nhiều sách nữa.",
                    "A book that makes you want to read more books."),

            Map.entry("Đọc mà thấy lòng mình được xoa dịu.",
                    "It soothed my heart while reading."),

            Map.entry("Cuốn này đọc mà không cần quá nhiều cao trào vẫn rất sâu.",
                    "It doesn’t need dramatic twists to be deep."),

            Map.entry("Đọc mà thấy mình được tiếp thêm động lực.",
                    "Reading this gave me motivation."),

            Map.entry("Một cuốn sách khiến mình muốn viết lại cảm xúc của mình.",
                    "A book that makes me want to write down my feelings."),

            Map.entry("Đọc mà thấy từng câu chữ đều rất tinh tế.",
                    "Every sentence feels delicate and refined."),

            Map.entry("Cuốn này đúng kiểu đọc xong là muốn đọc thêm lần nữa.",
                    "This book definitely deserves a reread."),

            Map.entry("Đọc mà thấy lòng mình rộng mở hơn.",
                    "It made my heart feel more open."),

            Map.entry("Một cuốn sách khiến mình phải suy nghĩ về tương lai.",
                    "A book that makes you think about the future."),

            Map.entry("Đọc mà cảm giác như đang được truyền cảm hứng.",
                    "It felt inspiring to read."),

            Map.entry("Cuốn này đọc mà không hề thấy phí thời gian.",
                    "Not a single moment felt wasted while reading.")
    );


    private final List<List<String>> sampleHashtags = List.of(
            List.of("#mọt_sách", "#đang_đọc"),
            List.of("#sách_và_tôi", "#đang_đọc_gì"),
            List.of("#nghiện_sách", "#đọc_nhiều_hơn"),
            List.of("#yêu_sách", "#cộng_đồng_đọc_sách"),
            List.of("#tiểu_thuyết", "#câu_chuyện"),
            List.of("#phi_hư_cấu", "#học_hỏi_mỗi_ngày"),
            List.of("#fantasy", "#thế_giới_tưởng_tượng"),
            List.of("#truyện_tình_cảm", "#yêu_thương"),
            List.of("#trinh_thám", "#hồi_hộp"),
            List.of("#phát_triển_bản_thân", "#trưởng_thành"),
            List.of("#động_lực", "#cảm_hứng"),
            List.of("#tư_duy", "#thành_công"),
            List.of("#đọc_sách_mỗi_ngày", "#giờ_đọc_sách"),
            List.of("#sách_nên_đọc", "#yêu_cộng_đồng_sách"),
            List.of("#danh_sách_đọc", "#sắp_đọc"),
            List.of("#văn_học", "#kinh_điển"),
            List.of("#văn_học_hiện_đại", "#kể_chuyện"),
            List.of("#review_sách", "#cuộc_sống_độc_giả"),
            List.of("#trích_dẫn_sách", "#suy_ngẫm"),
            List.of("#cảm_hứng_đọc_sách", "#góc_đọc_sách"),
            List.of("#sách_điện_tử", "#nghe_sách"),
            List.of("#đọc_cuối_tuần", "#thư_giãn"),
            List.of("#hội_đọc_sách", "#mọt_sách_chính_hiệu"),
            List.of("#yêu_tiểu_thuyết", "#từng_trang_sách"),
            List.of("#gợi_ý_sách_hay", "#sách_đáng_đọc"),
            List.of("#độc_giả_việt", "#yêu_văn_học"),
            List.of("#câu_lạc_bộ_sách", "#bạn_đọc_sách"),
            List.of("#thử_thách_đọc_sách", "#đọc_2026"),
            List.of("#đọc_khuya", "#tâm_trạng_đọc"),
            List.of("#yêu_câu_chuyện", "#trí_tưởng_tượng"),
            List.of("#yeusach", "#camxucdoc"),
            List.of("#motngaydocsach"),
            List.of("#truyenhay", "#sayme"),
            List.of("#doctruyen", "#thu_gian"),
            List.of("#thegioitruyen"),
            List.of("#sachvaem"),
            List.of("#nghien_doc"),
            List.of("#trang_sach", "#mo_tuong"),
            List.of("#bookvibes"),
            List.of("#docmotchut"),
            List.of("#truyenngan", "#danhchoem"),
            List.of("#sachhaymoingay"),
            List.of("#ngaymoisachmoi"),
            List.of("#booklife", "#readingmood"),
            List.of("#doclachill"),
            List.of("#truyencamhung"),
            List.of("#sachvaocafe"),
            List.of("#readingcorner"),
            List.of("#bookmoment"),
            List.of("#thichdocsach"),
            List.of("#fantasylover", "#thegioigiaotuong"),
            List.of("#romancebooks", "#camxuctinhyeu"),
            List.of("#thrillernight"),
            List.of("#selfhelpvn", "#phattrienbanthan"),
            List.of("#trietlycuocsong"),
            List.of("#songchamdocsach"),
            List.of("#readingtherapy"),
            List.of("#bookhealing"),
            List.of("#docsachmoingay"),
            List.of("#mottrangsach"),
            List.of("#bookaddictvn"),
            List.of("#readingaddict"),
            List.of("#nghien_truyen"),
            List.of("#sachtruocngu"),
            List.of("#latenightreading"),
            List.of("#bookandcoffee"),
            List.of("#docdehieu"),
            List.of("#ngamtrangsach"),
            List.of("#sachlamtoi"),
            List.of("#bookthoughts"),
            List.of("#readingquotes"),
            List.of("#truyenvaem"),
            List.of("#muadongdocsach"),
            List.of("#hevebooks"),
            List.of("#reviewtruyen"),
            List.of("#bookreviewvn"),
            List.of("#sachtrongtoi"),
            List.of("#yeusachhon"),
            List.of("#docsachla_vui"),
            List.of("#readingnotes"),
            List.of("#truyendangdoc"),
            List.of("#bookrecommendvn"),
            List.of("#tbrvietnam"),
            List.of("#readingjourney"),
            List.of("#sachvaocuocsong"),
            List.of("#bookpassion"),
            List.of("#docxonglaighim"),
            List.of("#trangsachcuocsong"),
            List.of("#bookcommunityvn"),
            List.of("#ngaykhongdocsach")
    );


    private final List<String> shareTemplates = List.of(
            "Đọc tới đoạn \"%s\" mà mình phải dừng lại vài giây.",
            "Trời ơi khúc \"%s\" đúng kiểu đọc mà nổi da gà.",
            "Ai từng đọc tới \"%s\" chắc hiểu cảm giác này lắm.",
            "Không biết mọi người sao chứ mình đọc tới \"%s\" là bị cuốn hẳn luôn.",
            "Đoạn \"%s\" thật sự ám ảnh mình mãi.",
            "Tới đoạn \"%s\" là mình biết cuốn này hợp gu mình rồi.",
            "Đang đọc mà gặp đoạn \"%s\" phải share liền cho mọi người.",
            "Đọc tới \"%s\" mà tim mình chững lại luôn.",
            "Cảm giác khi đọc đoạn \"%s\" đúng là khó tả thật.",
            "Không ngờ đoạn \"%s\" lại chạm cảm xúc mình như vậy.",
            "Vừa đọc tới \"%s\" là mình phải quay lại đọc thêm lần nữa.",
            "Đoạn \"%s\" thật sự làm mình suy nghĩ rất nhiều.",
            "Gặp đoạn \"%s\" là mình biết cuốn này không phải dạng vừa.",
            "Tự nhiên đọc tới \"%s\" mà thấy đồng cảm ghê luôn.",
            "Đoạn \"%s\" đúng kiểu đọc xong là nhớ hoài.",
            "Mình vừa đọc tới \"%s\" và phải nói là quá xuất sắc.",
            "Tới khúc \"%s\" là mình bắt đầu không thể rời mắt khỏi sách nữa.",
            "Đọc mà tới đoạn \"%s\" là chỉ muốn ngồi ngẫm mãi.",
            "Khúc \"%s\" đúng kiểu đọc xong là phải chia sẻ liền.",
            "Không ngờ đoạn \"%s\" lại khiến mình xúc động đến vậy.",
            "Đọc tới \"%s\" mà thấy thương nhân vật hơn hẳn.",
            "Chỉ riêng đoạn \"%s\" thôi cũng đủ làm mình nhớ mãi.",
            "Ai mà đọc tới \"%s\" chắc cũng sẽ dừng lại một chút.",
            "Đoạn \"%s\" đúng kiểu đọc xong là phải thở dài nhẹ một cái.",
            "Tới đoạn \"%s\" là mình phải gập sách lại một lúc.",
            "Đọc tới \"%s\" mà cảm giác mọi thứ xung quanh chậm lại.",
            "Đoạn \"%s\" thật sự làm mình thay đổi cách nhìn.",
            "Chỉ mới đọc tới \"%s\" thôi mà đã thấy đáng đọc rồi.",
            "Đoạn \"%s\" đúng kiểu đọc ban đêm là càng thấm.",
            "Đọc tới \"%s\" mà chỉ muốn nhắn cho ai đó đọc chung.",
            "Khúc \"%s\" đúng kiểu đọc xong là phải ngồi yên một lúc.",
            "Đoạn \"%s\" làm mình phải đọc chậm lại.",
            "Tới \"%s\" là mình thấy cuốn này thật sự đặc biệt.",
            "Đọc tới đoạn \"%s\" mà lòng mình nhẹ hẳn.",
            "Đoạn \"%s\" đúng kiểu đọc xong là chỉ muốn im lặng.",
            "Vừa đọc tới \"%s\" mà phải lưu lại ngay.",
            "Đoạn \"%s\" khiến mình phải suy nghĩ mãi không thôi.",
            "Tới đoạn \"%s\" mà cảm xúc cứ dâng lên.",
            "Đọc tới \"%s\" là thấy rõ vì sao cuốn này được khen nhiều.",
            "Khúc \"%s\" đúng kiểu đọc xong là phải gật gù.",
            "Đoạn \"%s\" làm mình thấy bản thân trong đó.",
            "Đọc tới \"%s\" mà thấy thương câu chuyện hơn hẳn.",
            "Tới đoạn \"%s\" là mình biết mình sẽ nhớ cuốn này lâu lắm.",
            "Đoạn \"%s\" thật sự làm mình không thể đọc nhanh được.",
            "Đọc tới \"%s\" mà cảm giác như đang xem phim vậy.",
            "Khúc \"%s\" đúng kiểu đọc xong là phải ngẫm lại.",
            "Đoạn \"%s\" khiến mình thấy lòng dịu lại.",
            "Đọc tới \"%s\" mà tự nhiên thấy thương đời hơn.",
            "Tới đoạn \"%s\" là mình chỉ muốn đọc tiếp ngay.",
            "Đoạn \"%s\" làm mình thấy cuốn này đáng đọc hơn hẳn.",
            "Đọc tới đoạn \"%s\" mà mình phải dừng lại một chút để cảm nhận.",
            "Không biết mọi người sao chứ tới đoạn \"%s\" là mình nổi da gà luôn.",
            "Đúng đoạn \"%s\" là cảm xúc mình trào lên thật sự.",
            "Tới khúc \"%s\" là mình chỉ biết ngồi im một lúc.",
            "Vừa đọc tới \"%s\" là phải share ngay vì quá hay.",
            "Ai đọc tới \"%s\" chắc cũng hiểu cảm giác này.",
            "Đoạn \"%s\" làm mình nhớ mãi không quên.",
            "Tự nhiên đọc tới \"%s\" mà thấy lòng chùng xuống.",
            "Cảm giác khi đọc tới \"%s\" thật sự rất khó tả.",
            "Đúng đoạn \"%s\" là mình bị cuốn hẳn vào câu chuyện.",
            "Đọc tới \"%s\" mà tim mình đập nhanh hơn hẳn.",
            "Không ngờ đoạn \"%s\" lại chạm cảm xúc mình vậy luôn.",
            "Tới khúc \"%s\" là mình phải đọc lại lần nữa.",
            "Đoạn \"%s\" đúng kiểu đọc xong là thẫn thờ luôn.",
            "Vừa chạm tới \"%s\" là mình phải gấp sách lại vài giây.",
            "Ai đang đọc tới \"%s\" chắc cũng không thoát nổi cảm xúc này.",
            "Đọc đoạn \"%s\" mà mình thấy thương nhân vật kinh khủng.",
            "Khúc \"%s\" đúng kiểu ám ảnh mình mãi.",
            "Đọc tới \"%s\" là mình biết cuốn này hợp gu mình rồi.",
            "Không hiểu sao đoạn \"%s\" lại khiến mình xúc động vậy.",
            "Tới đoạn \"%s\" là chỉ muốn ngồi nghĩ mãi thôi.",
            "Đọc mà tới \"%s\" là mình phải hít sâu một cái.",
            "Đoạn \"%s\" khiến mình nhớ lại nhiều chuyện cũ.",
            "Vừa đọc tới \"%s\" là mình phải gửi cho bạn mình đọc chung.",
            "Đúng khúc \"%s\" là mình thấy bản thân trong đó luôn.",
            "Đọc đoạn \"%s\" mà cảm giác như đang ở trong câu chuyện.",
            "Tới \"%s\" là mình biết cuốn này không hề bình thường.",
            "Đọc tới đoạn \"%s\" mà chỉ biết lặng người.",
            "Không nghĩ đoạn \"%s\" lại sâu sắc đến vậy.",
            "Vừa chạm tới \"%s\" là cảm xúc mình dâng lên liền.",
            "Đoạn \"%s\" đúng kiểu đọc xong là muốn chia sẻ ngay.",
            "Đọc tới \"%s\" mà mình phải ngước lên nhìn trần nhà suy nghĩ.",
            "Tự nhiên tới khúc \"%s\" mà thấy lòng nhẹ hẳn.",
            "Đoạn \"%s\" làm mình phải dừng lại thật lâu.",
            "Ai từng đọc tới \"%s\" chắc sẽ hiểu vì sao mình share đoạn này.",
            "Đọc tới \"%s\" mà cảm giác như tim mình chậm lại.",
            "Khúc \"%s\" đúng là cao trào cảm xúc luôn.",
            "Đoạn \"%s\" làm mình suy nghĩ mãi không thôi.",
            "Vừa đọc tới \"%s\" là mình biết phải lưu lại ngay.",
            "Đúng đoạn \"%s\" là mình thấy mọi thứ lắng xuống.",
            "Tới \"%s\" là mình bắt đầu thấy nghẹn nghẹn.",
            "Đọc tới đoạn \"%s\" mà chỉ muốn ôm lấy cuốn sách.",
            "Khúc \"%s\" làm mình nhớ lại lý do vì sao mình yêu đọc sách.",
            "Đọc tới \"%s\" mà cảm giác như đang nghe ai đó kể chuyện đời mình.",
            "Đoạn \"%s\" thật sự làm mình lặng đi vài giây.",
            "Tới khúc \"%s\" là cảm xúc mình bị kéo đi luôn.",
            "Đọc tới \"%s\" mà thấy thương nhân vật vô cùng.",
            "Đúng đoạn \"%s\" là mình phải dừng lại để thở.",
            "Khúc \"%s\" làm mình nổi da gà thật sự.",
            "Đọc tới \"%s\" mà thấy cả thế giới xung quanh như im lại.",
            "Vừa đọc đoạn \"%s\" là mình phải share liền không chần chừ.",
            "Đoạn \"%s\" khiến mình muốn đọc tiếp ngay lập tức.",
            "Tới \"%s\" là mình chỉ biết lắc đầu vì quá hay.",
            "Đọc tới \"%s\" mà cảm giác như bị hút vào đó.",
            "Đoạn \"%s\" đúng kiểu đọc xong là phải thở dài.",
            "Tới khúc \"%s\" là mình thấy lòng nặng trĩu.",
            "Đọc tới \"%s\" mà tự nhiên thấy sống chậm lại.",
            "Khúc \"%s\" làm mình thấy mọi thứ thật hơn bao giờ hết.",
            "Đoạn \"%s\" đúng là điểm mình thích nhất trong chương này.",
            "Tới \"%s\" là mình bắt đầu không thể rời mắt khỏi trang sách.",
            "Đọc tới đoạn \"%s\" mà mình thấy mọi cảm xúc ùa về.",
            "Khúc \"%s\" khiến mình phải suy nghĩ rất lâu.",
            "Đọc tới \"%s\" mà thấy thương cả chính mình luôn.",
            "Đoạn \"%s\" đúng kiểu đọc xong là muốn nhắn ai đó ngay.",
            "Tới \"%s\" là mình bắt đầu thấy câu chuyện đổi hướng hẳn.",
            "Đọc tới \"%s\" mà cảm giác như đang xem phim vậy.",
            "Khúc \"%s\" làm mình phải ngồi im một lúc thật lâu.",
            "Đoạn \"%s\" khiến mình muốn đọc chậm lại để cảm nhận.",
            "Tới \"%s\" là mình phải quay lại đọc thêm lần nữa.",
            "Đọc tới \"%s\" mà cảm giác như mọi thứ xung quanh mờ đi.",
            "Khúc \"%s\" thật sự làm mình bất ngờ.",
            "Đoạn \"%s\" làm mình nhớ mãi từ nãy tới giờ.",
            "Tới \"%s\" là mình biết chương này quá xuất sắc rồi.",
            "Đọc tới đoạn \"%s\" mà mình phải gật gù liên tục.",
            "Khúc \"%s\" làm mình thấy đồng cảm ghê luôn.",
            "Đoạn \"%s\" đúng kiểu đọc xong là không thể quên.",
            "Tới \"%s\" là mình thấy mọi cảm xúc bị đẩy lên cao.",
            "Đọc tới \"%s\" mà chỉ muốn đọc tiếp ngay chương sau.",
            "Khúc \"%s\" làm mình thấy nhân vật thật hơn bao giờ hết.",
            "Đoạn \"%s\" khiến mình thấy câu chuyện sâu sắc hẳn lên.",
            "Tới \"%s\" là mình phải thừa nhận cuốn này quá đỉnh.",
            "Đọc tới đoạn \"%s\" mà mình thấy nghẹn lại.",
            "Khúc \"%s\" làm mình chỉ biết thở dài vì quá nhiều cảm xúc.",
            "Đoạn \"%s\" đúng kiểu đọc xong là phải suy ngẫm.",
            "Tới \"%s\" là mình biết mình sẽ nhớ đoạn này rất lâu.",
            "Đọc tới \"%s\" mà cảm giác như vừa trải qua điều gì đó cùng nhân vật.",
            "Khúc \"%s\" làm mình phải ngồi lặng một lúc lâu.",
            "Đoạn \"%s\" khiến mình muốn đọc lại từ đầu chương.",
            "Tới \"%s\" là mình thấy cuốn sách này thật sự đặc biệt.",
            "Đọc tới đoạn \"%s\" mà chỉ biết mỉm cười một mình.",
            "Khúc \"%s\" làm mình thấy mọi thứ thật gần gũi.",
            "Đoạn \"%s\" đúng kiểu đọc xong là phải lưu lại liền.",
            "Tới \"%s\" là mình bắt đầu cảm thấy dính chặt vào câu chuyện.",
            "Đọc tới \"%s\" mà mình chỉ biết thì thầm: hay thật sự."
    );

    private final List<String> commentSamples = List.of(
            "Đúng gu mình luôn đó!",
            "Đọc tới đoạn này là mê thật sự.",
            "Cuốn này càng đọc càng cuốn.",
            "Mình cũng thích khúc này cực.",
            "Chỗ này đọc mà nổi da gà luôn.",
            "Tác giả viết đoạn này quá đỉnh.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình.",
            "Đoạn này làm mình nhớ mãi.",
            "Đọc mà không dứt ra được luôn.",
            "Khúc này đúng là cao trào luôn.",
            "Cảm xúc thật sự bùng nổ ở đoạn này.",
            "Mình phải đọc lại đoạn này tới mấy lần.",
            "Chỗ này đọc xong mà cứ nghĩ mãi.",
            "Đây là đoạn mình thích nhất trong chương.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi.",
            "Càng đọc càng thấy cuốn.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn.",
            "Tới đây là bắt đầu thấy cuốn hẳn.",
            "Chỗ này tác giả xử lý quá khéo.",
            "Đoạn này đọc mà tim đập nhanh thật sự.",
            "Khúc này đúng là làm mình bất ngờ nhất.",
            "Ai mà không thích đoạn này chắc lạ lắm luôn.",
            "Đọc tới đây là mình dừng lại vài phút để ngẫm.",
            "Cảm giác đoạn này rất khó tả.",
            "Khúc này làm mình nổi hết da gà.",
            "Chỗ này mà đọc ban đêm chắc càng ám ảnh.",
            "Đọc tới đoạn này là mình phải share liền.",
            "Đây là đoạn làm mình thay đổi suy nghĩ về nhân vật luôn.",
            "Khúc này đúng là viết quá đã.",
            "Đoạn này mà đọc lần đầu chắc ai cũng sốc.",
            "Mình đọc tới đây là phải comment liền.",
            "Chỗ này thật sự rất đáng nhớ.",
            "Đoạn này đúng là không thể quên được.",
            "Khúc này làm mình đọc mà không thở nổi luôn.",
            "Tới đây là bắt đầu thấy câu chuyện lên tầm mới.",
            "Đọc đoạn này là mình phải ngồi yên vài phút.",
            "Chỗ này đọc xong mà cứ ám ảnh mãi.",
            "Khúc này đúng là tác giả chơi lớn thật.",
            "Đoạn này mà không xúc động chắc khó lắm.",
            "Mình đọc tới đây mà tim muốn rớt ra ngoài.",
            "Chỗ này mà đọc lần hai vẫn còn thấy hay.",
            "Đoạn này đúng là làm mình bất ngờ hoàn toàn.",
            "Khúc này mà không ấn tượng thì chịu luôn.",
            "Đọc tới đây là mình biết mình chọn đúng sách rồi.",
            "Chỗ này đọc mà cười một mình luôn.",
            "Đoạn này mà đọc lúc buồn chắc khóc luôn.",
            "Khúc này đúng là chạm cảm xúc thật sự.",
            "Đọc đoạn này xong là mình phải ngồi lặng một lúc.",
            "Chỗ này thật sự rất thấm.",
            "Đoạn này đúng là đọc mà không thể quên được.",
            "Khúc này đúng là làm mình suy nghĩ rất nhiều.",
            "Đọc tới đây là thấy tác giả quá cao tay.",
            "Chỗ này mà đọc ban ngày còn thấy nổi da gà.",
            "Đoạn này mà đọc lúc khuya chắc ám ảnh luôn.",
            "Khúc này đúng là đỉnh của chương luôn.",
            "Đọc đoạn này mà tim đập nhanh hơn hẳn.",
            "Chỗ này đọc mà phải dừng lại thở.",
            "Đoạn này mà đọc xong là không thể ngủ liền được.",
            "Khúc này đúng là làm mình phải đọc lại từ đầu đoạn.",
            "Đọc tới đây là mình biết cuốn này không tầm thường.",
            "Chỗ này thật sự quá đã.",
            "Đoạn này mà đọc mà không bất ngờ thì lạ thật.",
            "Khúc này đúng là đọc mà nổi da gà thiệt.",
            "Đọc đoạn này mà cứ muốn kể cho người khác nghe.",
            "Chỗ này mà đọc một mình chắc còn ám ảnh hơn.",
            "Đoạn này đúng là đọc mà không nói nên lời.",
            "Khúc này mà đọc ban đêm chắc càng thấm hơn.",
            "Đọc tới đây là mình phải pause lại vài phút.",
            "Chỗ này thật sự quá xuất sắc.",
            "Đoạn này mà đọc xong là phải ngẫm rất lâu.",
            "Khúc này đúng là làm mình thay đổi cảm nhận về câu chuyện.",
            "Đọc đoạn này mà cứ muốn đọc tiếp ngay lập tức.",
            "Chỗ này mà đọc mà không thích thì chắc gu khác mình rồi.",
            "Đoạn này đúng là làm mình cười rồi lại trầm xuống liền.",
            "Khúc này mà đọc là không thể lướt qua được.",
            "Đọc tới đây là mình biết cuốn này sẽ còn nhiều bất ngờ nữa.",
            "Chỗ này thật sự làm mình ấn tượng mạnh.",
            "Đoạn này mà đọc mà không xúc động thì hơi khó tin.",
            "Khúc này đúng là làm mình dừng lại suy nghĩ về nhân vật.",
            "Đọc đoạn này mà cứ muốn đọc lại thêm lần nữa.",
            "Chỗ này mà đọc một mình chắc càng thấy thấm hơn.",
            "Đoạn này đúng là đọc mà không thể dừng lại.",
            "Khúc này mà đọc mà không nổi da gà thì chắc hiếm lắm.",
            "Đọc tới đây là mình phải ngồi im một lúc mới đọc tiếp được.",
            "Chỗ này thật sự quá cảm xúc.",
            "Đoạn này mà đọc mà không ấn tượng thì chắc khó lắm.",
            "Khúc này đúng là làm mình nhớ mãi về sau.",
            "Đọc đoạn này mà cứ thấy lòng nặng nặng.",
            "Chỗ này mà đọc mà không để lại cảm giác gì thì chắc hơi lạ.",
            "Đoạn này đúng là làm mình phải đọc chậm lại.",
            "Khúc này mà đọc mà không thấy cuốn thì hơi phí đó.",
            "Đọc tới đây là mình phải gửi cho bạn mình đọc cùng liền.",
            "Chỗ này thật sự rất đáng để đọc lại nhiều lần.",
            "Đoạn này mà đọc mà không cảm nhận được thì chắc đọc lướt mất rồi.",
            "Khúc này đúng là làm mình không quên được cuốn sách này.",
            "Đọc đoạn này mà cứ thấy xúc động nhẹ.",
            "Chỗ này mà đọc mà không thấy hay thì chắc tùy gu rồi.",
            "Đoạn này đúng là làm mình muốn đọc tiếp ngay không nghỉ.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được.",
            "Đúng gu mình luôn đó! thật sự luôn, đọc mà mê.",
            "Đọc tới đoạn này là mê thật sự. luôn luôn, đọc mà cuốn.",
            "Cuốn này càng đọc càng cuốn. cực kỳ luôn, đọc mà ấn tượng.",
            "Mình cũng thích khúc này cực. quá trời luôn, đọc mà xúc động.",
            "Chỗ này đọc mà nổi da gà luôn. ghê luôn luôn, đọc mà bất ngờ.",
            "Tác giả viết đoạn này quá đỉnh. hết sức luôn, đọc mà thích.",
            "Ai đọc tới đây chắc cũng dừng lại suy nghĩ giống mình. siêu luôn, đọc mà ghiền.",
            "Đoạn này làm mình nhớ mãi. khủng khiếp luôn, đọc mà ám ảnh.",
            "Đọc mà không dứt ra được luôn. không tưởng luôn, đọc mà nhớ mãi.",
            "Khúc này đúng là cao trào luôn. hết nói luôn, đọc mà không quên được.",
            "Cảm xúc thật sự bùng nổ ở đoạn này. thật sự luôn, đọc mà mê.",
            "Mình phải đọc lại đoạn này tới mấy lần. luôn luôn, đọc mà cuốn.",
            "Chỗ này đọc xong mà cứ nghĩ mãi. cực kỳ luôn, đọc mà ấn tượng.",
            "Đây là đoạn mình thích nhất trong chương. quá trời luôn, đọc mà xúc động.",
            "Đọc đoạn này xong là mình biết cuốn này hợp gu rồi. ghê luôn luôn, đọc mà bất ngờ.",
            "Càng đọc càng thấy cuốn. hết sức luôn, đọc mà thích.",
            "Đoạn này mà bỏ qua là tiếc lắm luôn. siêu luôn, đọc mà ghiền.",
            "Tới đây là bắt đầu thấy cuốn hẳn. khủng khiếp luôn, đọc mà ám ảnh.",
            "Chỗ này tác giả xử lý quá khéo. không tưởng luôn, đọc mà nhớ mãi.",
            "Đoạn này đọc mà tim đập nhanh thật sự. hết nói luôn, đọc mà không quên được."
    );
    private static final int THREAD_COUNT = 8;
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;


    // =========================================================
    public List<Post> generateFakePosts(Integer totalPosts) throws Exception {

        List<Post> posts = new ArrayList<>();
        List<User> allUsers = userRepository.findAllById(
                java.util.stream.LongStream.rangeClosed(1, 102).boxed().toList()
        );

        List<Book> allBooks = bookRepository.findAllById(
                java.util.stream.LongStream.rangeClosed(1, 62).boxed().toList()
        );

        long existingPostCount = postRepository.count(); // gọi 1 lần

        List<ReadingProgress> progressBatch = new ArrayList<>();

        for (int i = 0; i < totalPosts; i++) {

            User user = allUsers.get(random.nextInt(allUsers.size()));
            Book book = allBooks.get(random.nextInt(allBooks.size()));

            updateReadingProgress(user, book, progressBatch);

            boolean isShare = random.nextInt(12) == 0 && existingPostCount > 10;

            Post post = isShare ? createSharePost(user) : createNormalPost(user, book);

            posts.add(post);
        }

        postRepository.saveAll(posts);
        readingRepo.saveAll(progressBatch);


        // 🔥 SAU KHI TẠO XONG → TẠO LIKE & COMMENT THẬT
        generateRealInteractions(posts);

        return posts;
    }

    public void generateRealInteractions(List<Post> posts) throws Exception {

        List<User> allUsers = userRepository.findAll(); // 🔥 chỉ query 1 lần

        int chunkSize = (int) Math.ceil(posts.size() / (double) THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < posts.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, posts.size());
            List<Post> subList = posts.subList(i, end);

            futures.add(executor.submit(() ->
                    processChunk(subList, allUsers)
            ));
        }

        for (Future<?> f : futures) {
            f.get(); // đợi hoàn thành
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processChunk(List<Post> posts, List<User> allUsers) {

        Random random = new Random();

        List<Like> likeBatch = new ArrayList<>();
        List<Comment> commentBatch = new ArrayList<>();

        // Map để cộng dồn count giống toggleLike/addComment
        Map<Long, Integer> likeCountMap = new HashMap<>();
        Map<Long, Integer> commentCountMap = new HashMap<>();

        for (Post post : posts) {

            int likeTimes = random.nextInt(allUsers.size());
            int commentTimes = random.nextInt(allUsers.size()/3);

            Set<Long> usedUserIds = new HashSet<>(); // tránh 1 user like 2 lần

            // ================= LIKE =================
            for (int i = 0; i < likeTimes; i++) {

                User user = allUsers.get(random.nextInt(allUsers.size()));
                if (!usedUserIds.add(user.getUserId())) continue;

                // 🔥 Check tồn tại giống toggleLike
                boolean existed = likeRepository
                        .existsByUserUserIdAndTargetTypeAndTargetId(user.getUserId(), "POST", post.getPostId());
                if (existed) continue;

                Like like = new Like();
                like.setPost(post);
                like.setUser(user);
                like.setCreatedAt(LocalDateTime.now());

                likeBatch.add(like);

                likeCountMap.merge(post.getPostId(), 1, Integer::sum);
            }

            // ================= COMMENT =================
            for (int i = 0; i < commentTimes; i++) {

                User user = allUsers.get(random.nextInt(allUsers.size()));

                Comment comment = new Comment();
                comment.setPost(post);
                comment.setUser(user);
                comment.setContent(randomComment());
                comment.setParentId(null);
                comment.setLikesCount(0);
                comment.setCreatedAt(LocalDateTime.now());

                commentBatch.add(comment);

                commentCountMap.merge(post.getPostId(), 1, Integer::sum);
            }
        }

        // 🔥 BATCH INSERT
        likeRepository.saveAll(likeBatch);
        commentRepository.saveAll(commentBatch);

        // ================= UPDATE POST COUNTS GIỐNG SERVICE =================
        for (Post post : posts) {

            int addLikes = likeCountMap.getOrDefault(post.getPostId(), 0);
            int addComments = commentCountMap.getOrDefault(post.getPostId(), 0);

            if (addLikes > 0) {
                post.setLikesCount(post.getLikesCount() + addLikes);
                post.setScoreDirty(true); // ⭐ QUAN TRỌNG
            }

            if (addComments > 0) {
                post.setCommentsCount(post.getCommentsCount() + addComments);
                post.setScoreDirty(true); // ⭐ QUAN TRỌNG
            }
        }

        likeCountMap.forEach((postId, likeInc) -> {
            int commentInc = commentCountMap.getOrDefault(postId, 0);
            postRepository.updateCounters(postId, likeInc, commentInc);
        });

        commentCountMap.forEach((postId, commentInc) -> {
            if (!likeCountMap.containsKey(postId)) {
                postRepository.updateCounters(postId, 0, commentInc);
            }
        });

    }


    private String randomComment() {
        return commentSamples.get(random.nextInt(commentSamples.size()));
    }


    // ================= NORMAL POST =================
    private Post createNormalPost(User user, Book book) {

        String content = randomContent();
        String translated = contentMap.get(content);

        String fileName = "post_" + user.getUserId() + "_" + System.currentTimeMillis() + ".jpg";
        imageExecutor.submit(() -> {
            try {
                downloadImageFromBing(content, fileName);
            } catch (Exception e) {
                System.err.println("Download image failed: " + e.getMessage());
            }
        });


        LocalDateTime created = randomTime();
        LocalDateTime updated = randomTimeUpdate(created);
        return Post.builder()
                .user(user)
                .ownerId(user.getUserId())
                .content(content)
                .translatedText(translated)
                .imageUrl("/posts/" + fileName)
                .hashtags(sampleHashtags.get(random.nextInt(sampleHashtags.size())))
                .book(book)
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .views(random.nextInt(2000))
                .createdAt(created)
                .updatedAt(updated)
                .build();
    }

    // ================= SHARE POST =================
    @Transactional
    protected Post createSharePost(User user) {

        Post original = getRandomExistingPost();
        String newContent = generateShareContent(original);
        String fileName = "post_" + user.getUserId() + "_" + System.currentTimeMillis() + ".jpg";

        imageExecutor.submit(() -> {
            try {
                downloadImageFromBing(newContent, fileName);
            } catch (Exception e) {
                System.err.println("Download image failed: " + e.getMessage());
            }
        });

        List<String> hashtagsAll = new ArrayList<>(original.getHashtags());

        hashtagsAll.addAll(
                sampleHashtags.get(random.nextInt(sampleHashtags.size()))
        );

        LocalDateTime created = randomTime();
        LocalDateTime updated = randomTimeUpdate(created);
        Post sharePost = Post.builder()
                .user(user)
                .ownerId(user.getUserId())
                .content(newContent)
                .imageUrl("/posts/" + fileName)
                .hashtags(new ArrayList<>(hashtagsAll))
                .book(original.getBook())
                .shareOf(original.getPostId())
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .views(random.nextInt(1500))
                .createdAt(created)
                .updatedAt(updated)
                .build();

        postRepository.save(sharePost);

        // 🔥 TĂNG SHARE CHO BÀI GỐC
        // 2️⃣ Ghi record Share
        Share share = Share.builder()
                .user(user)
                .post(original)
                .sharedAt(LocalDateTime.now())
                .build();

        shareRepo.save(share);

        // 3️⃣ Tăng share cho bài gốc
        original.setSharesCount(original.getSharesCount() + 1);
        original.setScoreDirty(true);
        postRepository.save(original);

        return sharePost;
    }

    // ================= READING PROGRESS =================
    private void updateReadingProgress(User user, Book book, List<ReadingProgress> batch) {

        ReadingProgress progress = readingRepo
                .findByUser_UserIdAndBook_BookId(user.getUserId(), book.getBookId())
                .orElseGet(() -> readingService.addToShelf(user,
                        new ReadingAddRequest(book.getBookId(), ReadingStatus.READING, 0)));

        int totalPages = book.getTotalPages();
        int randomPage = 1 + random.nextInt(Math.max(totalPages, 1));

        progress.setCurrentPage(randomPage);
        progress.setPercentDone(Math.round(((randomPage * 100f) / totalPages) * 10) / 10f);

        if (progress.getPercentDone() >= 100f) {
            progress.setReadingStatus(ReadingStatus.FINISHED);
            progress.setFinishedDate(LocalDateTime.now());
        }

        batch.add(progress);
    }


    // ================= HELPERS =================
    private Book getRandomBook() {
        long id = 1 + random.nextInt(62);
        return bookRepository.findById(id).orElse(null);
    }

    private String randomContent() {
        List<String> keys = new ArrayList<>(contentMap.keySet());
        return keys.get(random.nextInt(keys.size()));
    }

    private LocalDateTime randomTime() {
        return LocalDateTime.now()
                .minusDays(random.nextInt(30))
                .minusHours(random.nextInt(24));
    }

    private LocalDateTime randomTimeUpdate(LocalDateTime createdTime) {
        long minutesBetween = ChronoUnit.MINUTES.between(createdTime, LocalDateTime.now());

        if (minutesBetween <= 0) {
            return createdTime;
        }

        return createdTime.plusMinutes(random.nextLong(minutesBetween));
    }

    private String generateShareContent(Post originalPost) {
        String snippet = extractMeaningfulPart(originalPost.getContent());
        String template = shareTemplates.get(random.nextInt(shareTemplates.size()));
        return String.format(template, snippet);
    }

    private String extractMeaningfulPart(String content) {
        if (content == null || content.length() < 40) return content;
        int start = random.nextInt(content.length() / 2);
        int end = Math.min(start + 60, content.length());
        return content.substring(start, end).trim();
    }

    private Post getRandomExistingPost() {
        long count = postRepository.count();
        int index = random.nextInt((int) count);
        return postRepository.findAll(PageRequest.of(index, 1)).getContent().get(0);
    }

    // ================= IMAGE DOWNLOAD =================
    public void downloadImageFromBing(String keyword, String fileName) {
        try {
            String searchUrl = "https://www.bing.com/images/search?q=" +
                               URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            Document doc = Jsoup.connect(searchUrl).userAgent("Mozilla/5.0").get();
            Element img = doc.select("img.mimg").first();
            if (img == null) return;

            String imgUrl = img.attr("src");
            InputStream in = new URL(imgUrl).openStream();

            Path uploadDir = Paths.get("uploads", "posts");
            Files.createDirectories(uploadDir); // đảm bảo thư mục tồn tại

            Path savePath = uploadDir.resolve(fileName);
            Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

