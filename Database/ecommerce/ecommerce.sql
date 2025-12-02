-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 02, 2025 at 12:02 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ecommerce`
--

-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--

CREATE TABLE `addresses` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `recipient_name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `total_money` int(11) NOT NULL,
  `status` varchar(50) DEFAULT 'Pending',
  `payment_method` varchar(100) DEFAULT NULL,
  `note` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `user_id`, `total_money`, `status`, `payment_method`, `note`, `created_at`) VALUES
(1, 1, 399000, 'Đã giao hàng', 'Thanh toán khi nhận hàng (COD)', NULL, '2025-11-17 10:40:58'),
(2, 1, 2396000, 'Đã giao hàng', 'Thanh toán khi nhận hàng (COD)', NULL, '2025-11-19 02:54:41'),
(3, 1, 399000, 'Đã hủy (Admin)', 'Thanh toán khi nhận hàng (COD)', NULL, '2025-11-19 11:52:57'),
(4, 1, 1997000, 'Đang xử lý', 'Ví điện tử Momo', '', '2025-11-24 17:09:41');

-- --------------------------------------------------------

--
-- Table structure for table `order_detail`
--

CREATE TABLE `order_detail` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `size` varchar(10) DEFAULT 'M'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `order_detail`
--

INSERT INTO `order_detail` (`id`, `order_id`, `product_id`, `quantity`, `price`, `size`) VALUES
(1, 1, 1, 1, 399000, 'M'),
(2, 2, 2, 2, 799000, 'M'),
(3, 2, 1, 2, 399000, 'M'),
(4, 3, 1, 1, 399000, 'XXL'),
(5, 4, 1, 1, 399000, 'M'),
(6, 4, 2, 2, 799000, 'M');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `product_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price` int(11) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `gender` varchar(50) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`product_id`, `name`, `description`, `price`, `quantity`, `gender`, `image`, `category`) VALUES
(1, 'Áo Thun Sọc Nhỏ', 'Áo thun sọc nhỏ form rộng tay lỡ, chất liệu 100% cotton.', 399000, 100, 'Nam', 'shirt_man1.png', 'Áo'),
(2, 'Giày Thể Thao Trắng', 'Giày thể thao sneaker, đế cao su, phù hợp đi chơi và vận động.', 799000, 50, 'Unisex', 'shoe1.png', 'Giày'),
(3, 'Quần Jean Nam', 'Quần jean rách gối, phong cách đường phố.', 550000, 75, 'Nam', 'jean_man1.png', 'Quần'),
(4, 'Áo Sơ Mi Nam Trắng', 'Áo sơ mi công sở lịch lãm, chất vải chống nhăn.', 350000, 50, 'Nam', 'shirt_man2.png', 'Áo'),
(5, 'Quần Jean Nam Rách', 'Quần jean phong cách bụi bặm, co giãn tốt.', 450000, 40, 'Nam', 'jean_man2.png', 'Quần'),
(6, 'Áo Thun Nữ Hồng', 'Áo thun cotton màu hồng pastel dễ thương.', 150000, 100, 'Nữ', 'shirt_woman1.png', 'Áo'),
(7, 'Chân Váy Ngắn', 'Chân váy xếp ly năng động, phù hợp đi chơi.', 200000, 30, 'Nữ', 'skirt_woman1.png', 'Quần'),
(8, 'Giày Sneaker Trắng', 'Giày thể thao trắng basic, phù hợp cả nam và nữ.', 650000, 20, 'Unisex', 'shoe2.png', 'Giày'),
(9, 'Giày Chạy Bộ', 'Giày thiết kế êm ái, hỗ trợ vận động mạnh.', 890000, 15, 'Unisex', 'shoe3.png', 'Giày'),
(10, 'Áo Thun Trẻ Em Hình Gấu', 'Áo thun cotton 100% mềm mại, thấm hút mồ hôi, in hình gấu dễ thương.', 120000, 50, 'Trẻ em', 'kid_shirt1.png', 'Áo'),
(11, 'Quần Short Jeans Bé Trai', 'Quần short năng động, lưng thun co giãn thoải mái cho bé vận động.', 150000, 40, 'Trẻ em', 'kid_jeans1.png', 'Quần'),
(12, 'Đầm Công Chúa Elsa', 'Đầm xòe lấp lánh, chất liệu voan nhẹ nhàng cho bé gái dự tiệc.', 250000, 30, 'Trẻ em', 'kid_dress1.png', 'Áo'),
(13, 'Giày Búp Bê Bé Gái', 'Giày búp bê êm chân, có quai dán tiện lợi.', 180000, 24, 'Trẻ em', 'kid_shoes1.png', 'Giày'),
(14, 'Áo Polo Nam Classic', 'Áo polo chất vải cá sấu, form regular fit, thoáng mát.', 250000, 60, 'Nam', 'polo_man1.png', 'Áo'),
(15, 'Quần Kaki Nam Dài', 'Quần kaki ống đứng, màu be lịch sự phù hợp công sở.', 400000, 45, 'Nam', 'kaki_man1.png', 'Quần'),
(16, 'Áo Khoác Da Nam', 'Áo khoác giả da phong cách biker, chống gió tốt.', 850000, 20, 'Nam', 'jacket_man1.png', 'Áo'),
(17, 'Giày Lười Da Bò', 'Giày lười da thật, đế mềm êm chân, phong cách quý ông.', 1200000, 15, 'Nam', 'loafer_man1.png', 'Giày'),
(18, 'Quần Short Thể Thao', 'Quần short thun co giãn 4 chiều, thích hợp tập gym.', 120000, 80, 'Nam', 'short_man1.png', 'Quần'),
(19, 'Áo Hoodie Unisex', 'Áo hoodie nỉ bông ấm áp, form rộng phong cách Hàn Quốc.', 320000, 50, 'Nam', 'hoodie_man1.png', 'Áo'),
(20, 'Quần Jogger Túi Hộp', 'Quần jogger phong cách đường phố, nhiều túi tiện lợi.', 380000, 40, 'Nam', 'jogger_man1.png', 'Quần'),
(21, 'Đầm Maxi Đi Biển', 'Đầm maxi voan hoa nhí, thướt tha, phù hợp đi du lịch.', 450000, 30, 'Nữ', 'maxi_woman1.png', 'Áo'),
(22, 'Áo Crop Top Len', 'Áo len dệt kim dáng ngắn, trẻ trung năng động.', 180000, 60, 'Nữ', 'croptop_woman1.png', 'Áo'),
(23, 'Quần Jean Nữ Skinny', 'Quần jean ôm sát tôn dáng, chất vải co giãn tốt.', 350000, 55, 'Nữ', 'jean_woman1.png', 'Quần'),
(24, 'Áo Blazer Công Sở', 'Áo khoác blazer thanh lịch, màu pastel nhẹ nhàng.', 550000, 25, 'Nữ', 'blazer_woman1.png', 'Áo'),
(25, 'Quần Legging Tập Gym', 'Quần legging thun lạnh, lưng cao, thấm hút mồ hôi.', 220000, 70, 'Nữ', 'legging_woman1.png', 'Quần'),
(26, 'Giày Cao Gót Mũi Nhọn', 'Giày cao gót 7cm, da bóng sang trọng, đế chống trượt.', 480000, 20, 'Nữ', 'heels_woman1.png', 'Giày'),
(27, 'Sandal Đế Xuồng', 'Dép sandal đế cói, quai vải, phù hợp mùa hè.', 290000, 35, 'Nữ', 'sandal_woman1.png', 'Giày'),
(28, 'Áo Thun Siêu Nhân', 'Áo thun in hình người nhện cho bé trai, vải cotton mát.', 99000, 50, 'Trẻ em', 'hero_kid1.png', 'Áo'),
(29, 'Yếm Jean Bé Trai', 'Yếm jean năng động, dây đeo điều chỉnh được.', 210000, 30, 'Trẻ em', 'overalls_kid1.png', 'Quần'),
(30, 'Đồ Bộ Pijama Lụa', 'Bộ đồ ngủ lụa mềm mại cho bé gái, họa tiết hoạt hình.', 160000, 40, 'Trẻ em', 'pajama_kid1.png', 'Áo'),
(31, 'Giày Thể Thao Đèn LED', 'Giày sneaker có đèn phát sáng khi bước đi cho bé.', 320000, 25, 'Trẻ em', 'ledshoe_kid1.png', 'Giày'),
(32, 'Áo Khoác Gió Trẻ Em', 'Áo khoác dù mỏng nhẹ, chống nước, có mũ trùm đầu.', 190000, 45, 'Trẻ em', 'windbreaker_kid1.png', 'Áo'),
(33, 'Váy Yếm Kaki Bé Gái', 'Váy yếm vải kaki mềm, màu hồng dễ thương.', 185000, 35, 'Trẻ em', 'dress_kid2.png', 'Áo');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `image` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `specs` text DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `stock` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `review_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `rating` int(11) NOT NULL,
  `comment` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `reviews`
--

INSERT INTO `reviews` (`review_id`, `user_id`, `product_id`, `rating`, `comment`, `created_at`) VALUES
(2, 1, 2, 5, 'tuyệt', '2025-11-27 03:40:03');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `gender` varchar(50) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_locked` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `username`, `password`, `email`, `gender`, `phone_number`, `address`, `city`, `created_at`, `is_locked`) VALUES
(1, 'anh', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'a@gmail.com', 'nam', '123', 'hn', 'hd', '2025-11-09 11:30:35', 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('admin','user') DEFAULT 'user',
  `avatar` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_cart`
--

CREATE TABLE `user_cart` (
  `cart_item_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `size` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `user_cart`
--

INSERT INTO `user_cart` (`cart_item_id`, `user_id`, `product_id`, `quantity`, `size`) VALUES
(10, 1, 1, 3, 'M');

-- --------------------------------------------------------

--
-- Table structure for table `user_favorites`
--

CREATE TABLE `user_favorites` (
  `favorite_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `user_favorites`
--

INSERT INTO `user_favorites` (`favorite_id`, `user_id`, `product_id`) VALUES
(17, 1, 1),
(12, 1, 2),
(16, 1, 5),
(18, 1, 9);

-- --------------------------------------------------------

--
-- Table structure for table `vouchers`
--

CREATE TABLE `vouchers` (
  `code` varchar(20) NOT NULL,
  `discount_percent` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `expiry_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `vouchers`
--

INSERT INTO `vouchers` (`code`, `discount_percent`, `quantity`, `expiry_date`) VALUES
('FREESHIP', 100, 0, '2023-01-01'),
('GIAM10', 10, 100, '2025-12-31'),
('SALE50', 50, 5, '2025-12-31');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `order_detail`
--
ALTER TABLE `order_detail`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`review_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_cart`
--
ALTER TABLE `user_cart`
  ADD PRIMARY KEY (`cart_item_id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`product_id`,`size`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `user_favorites`
--
ALTER TABLE `user_favorites`
  ADD PRIMARY KEY (`favorite_id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`product_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `vouchers`
--
ALTER TABLE `vouchers`
  ADD PRIMARY KEY (`code`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `addresses`
--
ALTER TABLE `addresses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `order_detail`
--
ALTER TABLE `order_detail`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `review_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_cart`
--
ALTER TABLE `user_cart`
  MODIFY `cart_item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `user_favorites`
--
ALTER TABLE `user_favorites`
  MODIFY `favorite_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `order_detail`
--
ALTER TABLE `order_detail`
  ADD CONSTRAINT `order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `order_detail_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

--
-- Constraints for table `user_cart`
--
ALTER TABLE `user_cart`
  ADD CONSTRAINT `user_cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE;

--
-- Constraints for table `user_favorites`
--
ALTER TABLE `user_favorites`
  ADD CONSTRAINT `user_favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_favorites_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
