/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package main

import (
	"context"
	"flag"
	"fmt"
	_ "net/http/pprof"
)

import (
	clusterutils "dubbo.apache.org/dubbo-go/v3/cluster/utils"
	"dubbo.apache.org/dubbo-go/v3/config"
	_ "dubbo.apache.org/dubbo-go/v3/imports"

	"github.com/dubbogo/gost/log/logger"

	testerpkg "github.com/dubbogo/tools/pkg/tester"
)

import (
	"github.com/dubbogo/dubbo-go-benchmark/3.0/filters/offline_simulator"
)

const (
	defaultTps        = 200
	defaultDuration   = "1m"
	defaultFibonacciN = 1
)

var (
	duration   string
	tps        int
	fibonacciN int64
)

const ErrConsumerRequestTimeoutStr = "maybe the client read timeout or fail to decode tcp stream in Writer.Write"

type Provider struct {
	Fibonacci func(ctx context.Context, n int64) (int64, error)
}

func main() {
	flag.IntVar(&tps, "c", defaultTps, "TPS")
	flag.StringVar(&duration, "d", defaultDuration, "Test duration. It should be a string representing a time, like \"1h\", \"30m\", etc")
	flag.Int64Var(&fibonacciN, "n", defaultFibonacciN, "Parameter for the call to fibonacci")
	flag.Parse()

	provider := &Provider{}
	config.SetConsumerService(provider)

	if err := config.Load(); err != nil {
		panic(err)
	}

	ctx := context.TODO()

	doInvoke := func(uid int) {
		if result, err := fibonacci(ctx, provider); err != nil {
			handleErr(err)
		} else {
			fmt.Printf("result: %d\n", result)
		}
	}

	tester := testerpkg.NewStressTester()
	tester.
		SetTPS(tps).
		SetDuration(duration).
		SetTestFn(doInvoke).
		Run()
	fmt.Printf("Sent request num: %d\n", tester.GetTransactionNum())
	fmt.Printf("TPS: %.2f\n", tester.GetTPS())
	fmt.Printf("RT: %.2fs\n", tester.GetAverageRTSeconds())
}

func fibonacci(ctx context.Context, provider *Provider) (result int64, err error) {
	result, err = provider.Fibonacci(ctx, fibonacciN)
	return
}

func handleErr(err error) {
	if clusterutils.DoesAdaptiveServiceReachLimitation(err) {
		logger.Infof("Reach Limitation")
	} else if err.Error() == ErrConsumerRequestTimeoutStr {
		logger.Warnf("Consumer Request Timeout, err: %v", err)
	} else if offline_simulator.IsServerOfflineErr(err) {
		logger.Warnf("Server offline, err: %v", err)
	} else {
		panic(err)
	}
}
