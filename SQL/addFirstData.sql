insert into public.category (name) values ('Loai 1');
insert into public.category (name) values ('Loai 2');

insert into public.product (SKU, description, price, stock, category_id)
 values ('Product 1', 'this is description', 5.5, 5, 1);
insert into public.product (SKU, description, price, stock, category_id)
 values ('Product 2', 'this is description', 6.5, 10, 1);
insert into public.product (SKU, description, price, stock, category_id)
 values ('Product 3', 'this is description', 7.5, 15, 2);
insert into public.product (SKU, description, price, stock, category_id)
 values ('Product 4', 'this is description', 8.5, 30, 2);
insert into public.product (SKU, description, price, stock, category_id)
 values ('Product 5', 'this is description', 9.5, 25, 2);
 


insert into shipment (shipment_date, address, city, country, zip_code, customer_id)
 values ('2024-4-20', 'xa cam 2', 'Binh Phuoc', 'Viet Nam', 123, 1);
-- insert into shipment (shipment_date, address, city, country, zip_code, customer_id)
--  values ('2024-4-30', 'Binh trung Tay', 'Ho Chi Minh', 'Viet Nam', 456, 6);
-- insert into shipment (shipment_date, address, city, country, zip_code, customer_id)
--  values ('2024-3-20', 'Disney', 'NewYork', 'America', 789, 7);
 
 
 insert into payment (payment_date, payment_method, amount, customer_id)
  values ('2024-3-14', 'visa', 12, 5);
 insert into payment (payment_date, payment_method, amount, customer_id)
  values (null, 'cod', 14, 6);
 insert into payment (payment_date, payment_method, amount, customer_id)
  values ('2024-3-10', 'visa', 22.5, 7);
 
 
insert into "order" (order_date, total_price, customer_id, payment_id, shipment_id)
 values ('2024-3-14', 12, 5, 2, 1);
insert into "order" (order_date, total_price, customer_id, payment_id, shipment_id)
 values ('2024-3-15', 14, 6, 3, 2);
insert into "order" (order_date, total_price, customer_id, payment_id, shipment_id)
 values ('2024-3-10', 22.5, 7, 4, 3);
 
 
insert into order_item (quantity, price, product_id, order_id)
 values (2, 11, 1, 2);
insert into order_item (quantity, price, product_id, order_id)
 values (3, 19.5, 2, 3);
insert into order_item (quantity, price, product_id, order_id)
 values (1, 19, 5, 4);
