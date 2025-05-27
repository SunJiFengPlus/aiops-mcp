## 设计背景
1. 每种类型的可观测数据(Trace、Log、Metric)都可能会来自不同的组件, 例如trace来自skywalking, metric来自prometheus, log来自es, 也有可能是trace与metric来自skywalking, log来自es;
2. 每个数据源的对于Trace、Log、Metric的协议不同, 即使在传输时大部分符合open-telemetry的协议, 最终展示的协议大都不同;
3. 由于接口协议不同, 基于Skywalking能够提供的Trace Tools 无法保证与其他组件能够提供的Trace Tools完全一致;
4. 单一类型的可观测数据可能会来自多个系统, metric可能会来prometheus与skywalking 


---
- datasource/skywalking: skywalking数据源对接
- datasource/skywalking/config: yaml中的配置读取
- datasource/skywalking/entity: 数据源实体, 用于数据序列化反序列化
- datasource/skywalking/repo: 数据源接口, 直接通过http等协议对接外部系统的
- datasource/skywalking/tools: 工具逻辑