## Event Sourcing And CQRS
### Event Sourcing 、CQRS 简述

Event Sourcing 简单来说就是记录对象的每个事件而不是记录对象的最新状态，比如新建、修改等，只记录事件内容，当需要最新的状态的时，通过堆叠事件将最新的状态计算出来。那么这种模式查询的时候性能会变的非常差，这个时候就涉及到了 CQRS ，简单的理解就是读写分离，通过事件触发，将最新状态保存到读库，查询全都走读库，理论上代码层，数据库层，都可以做到分离，当然也可以不分离，一般来说为了保证数据库性能，这里起码会将数据库分离。
	
### 为什么要使用
了解了 Event Sourcing 的基本内容之后，我们可以发现这个模式有很多的好处：

* 记录了对象的事件，相当于记录了整个历史，可以查看到任意一个时间点的对象状态；
* 都是以事件形式进行写入操作，理论上在高并发的情况下，没有死锁，性能会快很多；
* 可以基于历史事件做更多的数据分析。
	
Event Soucing 通常会和 DDD CQRS 一起讨论，在微服务盛行的前提下，DDD 让整个软件系统松耦合，而 Event Soucing 也是面向 Aggregate，这个模式很符合 DDD 思想，同时由于 Event Soucing 的特性，读取数据必然会成为瓶颈，这个时候 CQRS 就起到做用了，通过读写分离，让各自的职责专一，实际上在传统的方式中我们也可能会这么干，只是方式略微不同，比如有一个只读库，时时同步主库，让查询通过只读库进行，那么如果查询量特别大的时候，起码写库不会因为查询而下降性能。

### 背景

由于我们公司的技术体系基本是 Spring 全家桶，而 Java 界似乎 Axon 又是比较流行的 Event Sourcing 框架，本着对新技术的尝试以及某些业务也确实有这方面的需求的出发点，对 Axon 做了一些尝试。后面的一系列文章将会以 Spring Cloud 作为背景，探讨 Axon 如何使用，以及如何出处理一些常见的业务需求（溯源、读写分离、消息可靠等），所以在看后面的文章之前最好对 Spring Boot、Spring Cloud、Spring Cloud Stream、Spring Data JPA 等有一些基本的了解。

### 目录

1. [Event Sourcing 和 CQRS落地（一）：前言](http://soooban.github.io/2019/06/10/Event-Sourcing-And-CQRS/)
2. [Event Sourcing 和 CQRS落地（二）：UID-Generator 实现](http://soooban.github.io/2019/06/10/UID-Generator/)
3. [Event Sourcing 和 CQRS落地（三）：Event-Sourcing 实现](http://soooban.github.io/2019/06/11/%E5%AE%9E%E7%8E%B0-Event-Sourcing/)
4. [Event Sourcing 和 CQRS落地（四）：CQRS 实现](http://soooban.github.io/2019/06/11/%E5%AE%9E%E7%8E%B0-CQRS/)
5. [Event Sourcing 和 CQRS落地（五）：深入使用-Axon](http://soooban.github.io/2019/06/12/%E6%B7%B1%E5%85%A5%E4%BD%BF%E7%94%A8-Axon/)
6. [Event Sourcing 和 CQRS落地（六）：Spring-Cloud-Stream 优化](http://soooban.github.io/2019/06/13/%E5%AE%9E%E7%8E%B0%E5%8F%AF%E9%9D%A0%E6%B6%88%E6%81%AF/)
7. [Event Sourcing 和 CQRS落地（七）：实现可靠消息](http://soooban.github.io/2019/06/13/Spring-Cloud-Stream-%E4%BC%98%E5%8C%96/)
8. [Event Sourcing 和 CQRS落地（八）：服务优化](http://soooban.github.io/2019/06/14/%E6%9C%8D%E5%8A%A1%E4%BC%98%E5%8C%96/)
