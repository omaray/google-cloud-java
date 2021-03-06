/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsub;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.cloud.AsyncPage;
import com.google.cloud.Page;
import com.google.cloud.Policy;
import com.google.cloud.pubsub.PubSub.ListOption;
import com.google.common.base.Function;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * A Google Cloud Pub/Sub topic. A topic is a named resource to which messages are sent by
 * publishers. Subscribers can receive messages sent to a topic by creating subscriptions.
 * {@code Topic} adds a layer of service-related functionality over {@link TopicInfo}. Objects of
 * this class are immutable. To get a {@code Topic} object with the most recent information use
 * {@link #reload} or {@link #reloadAsync}.
 *
 * @see <a href="https://cloud.google.com/pubsub/overview#data_model">Pub/Sub Data Model</a>
 */
public class Topic extends TopicInfo {

  private static final long serialVersionUID = -2686692223763315944L;

  private final PubSubOptions options;
  private transient PubSub pubsub;

  /**
   * A builder for {@code Topic} objects.
   */
  public static final class Builder extends TopicInfo.Builder {

    private final PubSub pubsub;
    private final BuilderImpl delegate;

    private Builder(Topic topic) {
      pubsub = topic.pubsub;
      delegate = new BuilderImpl(topic);
    }

    @Override
    public Builder name(String name) {
      delegate.name(name);
      return this;
    }

    @Override
    public Topic build() {
      return new Topic(this.pubsub, this.delegate);
    }
  }

  Topic(PubSub pubsub, BuilderImpl builder) {
    super(builder);
    this.pubsub = checkNotNull(pubsub);
    options = pubsub.options();
  }

  @Override
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(options, super.hashCode());
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || !obj.getClass().equals(Topic.class)) {
      return false;
    }
    Topic other = (Topic) obj;
    return baseEquals(other) && Objects.equals(options, other.options);
  }

  /**
   * Returns the topic's {@code PubSub} object used to issue requests.
   */
  public PubSub pubSub() {
    return pubsub;
  }

  /**
   * Deletes this topic.
   *
   * @return {@code true} if the topic was deleted, {@code false} if it was not found
   * @throws PubSubException upon failure
   */
  public boolean delete() {
    return pubsub.deleteTopic(name());
  }

  /**
   * Sends a request for deleting this topic. This method returns a {@code Future} object to consume
   * the result. {@link Future#get()} returns {@code true} if the topic was deleted, {@code false}
   * if it was not found.
   *
   * @throws PubSubException upon failure
   */
  public Future<Boolean> deleteAsync() {
    return pubsub.deleteTopicAsync(name());
  }

  /**
   * Fetches current topic's latest information. Returns {@code null} if the topic does not exist.
   *
   * @return a {@code Topic} object with latest information or {@code null} if not found
   * @throws PubSubException upon failure
   */
  public Topic reload() {
    return pubsub.getTopic(name());
  }

  /**
   * Sends a request to fetch current topic's latest information. This method returns a
   * {@code Future} object to consume the result. {@link Future#get()} returns a {@code Topic}
   * object with latest information or {@code null} if not found.
   *
   * @throws PubSubException upon failure
   */
  public Future<Topic> reloadAsync() {
    return pubsub.getTopicAsync(name());
  }

  /**
   * Publishes a message to this topic. This method returns a service-generated id for the published
   * message. Service-generated ids are guaranteed to be unique within the topic.
   *
   * @param message the message to publish
   * @return a unique service-generated id for the message
   * @throws PubSubException upon failure, if the topic does not exist or if the message has empty
   *     payload and no attributes
   */
  public String publish(Message message) {
    return pubsub.publish(name(), message);
  }

  /**
   * Sends a request for publishing a message to the this topic. This method returns a
   * {@code Future} object to consume the result. {@link Future#get()} returns a service-generated
   * id for the published message. Service-generated ids are guaranteed to be unique within the
   * topic.
   *
   * @param message the message to publish
   * @return a {@code Future} for the unique service-generated id for the message
   */
  public Future<String> publishAsync(Message message) {
    return pubsub.publishAsync(name(), message);
  }

  /**
   * Publishes a number of messages to this topic. This method returns a list of service-generated
   * ids for the published messages. Service-generated ids are guaranteed to be unique within the
   * topic.
   *
   * @param message the first message to publish
   * @param messages other messages to publish
   * @return a list of unique, service-generated, ids. Ids are in the same order as the messages.
   * @throws PubSubException upon failure, if the topic does not exist or if one of the messages has
   *     empty payload and no attributes
   */
  public List<String> publish(Message message, Message... messages) {
    return pubsub.publish(name(), message, messages);
  }

  /**
   * Sends a request to publish a number of messages to this topic. This method returns a
   * {@code Future} object to consume the result. {@link Future#get()} returns a list of
   * service-generated ids for the published messages. Service-generated ids are guaranteed to be
   * unique within the topic.
   *
   * @param message the first message to publish
   * @param messages other messages to publish
   * @return a {@code Future} for the unique, service-generated ids. Ids are in the same order as
   *     the messages.
   */
  public Future<List<String>> publishAsync(Message message, Message... messages) {
    return pubsub.publishAsync(name(), message, messages);
  }

  /**
   * Publishes a number of messages to this topic. This method returns a list ofservice-generated
   * ids for the published messages. Service-generated ids are guaranteed to be unique within the
   * topic.
   *
   * @param messages the messages to publish
   * @return a list of unique, service-generated, ids. Ids are in the same order as the messages.
   * @throws PubSubException upon failure, if the topic does not exist or if one of the messages has
   *     empty payload and no attributes
   */
  public List<String> publish(Iterable<Message> messages) {
    return pubsub.publish(name(), messages);
  }

  /**
   * Sends a request to publish a number of messages to this topic. This method returns a
   * {@code Future} object to consume the result. {@link Future#get()} returns a list of
   * service-generated ids for the published messages. Service-generated ids are guaranteed to be
   * unique within the topic.
   *
   * @param messages the messages to publish
   * @return a {@code Future} for the unique, service-generated ids. Ids are in the same order as
   *     the messages.
   */
  public Future<List<String>> publishAsync(Iterable<Message> messages) {
    return pubsub.publishAsync(name(), messages);
  }

  /**
   * Lists the identities of the subscriptions for this topic. This method returns a {@link Page}
   * object that can be used to consume paginated results. Use {@link ListOption} to specify the
   * page size or the page token from which to start listing subscriptions.
   *
   * @throws PubSubException upon failure
   */
  public Page<SubscriptionId> listSubscriptions(ListOption... options) {
    return pubsub.listSubscriptions(name(), options);
  }

  /**
   * Sends a request for listing the identities of subscriptions for this topic. This method returns
   * a {@code Future} object to consume the result. {@link Future#get()} returns an
   * {@link AsyncPage} object that can be used to asynchronously handle paginated results. Use
   * {@link ListOption} to specify the page size or the page token from which to start listing
   * subscriptions.
   */
  public Future<AsyncPage<SubscriptionId>> listSubscriptionsAsync(ListOption... options) {
    return pubsub.listSubscriptionsAsync(name(), options);
  }

  /**
   * Returns the IAM access control policy for this topic. Returns {@code null} if the topic was not
   * found.
   *
   * @throws PubSubException upon failure
   */
  public Policy getPolicy() {
    return pubsub.getTopicPolicy(this.name());
  }

  /**
   * Sends a request for getting the IAM access control policy for this topic. This method returns a
   * {@code Future} object to consume the result. {@link Future#get()} returns the requested policy
   * or {@code null} if the topic was not found.
   *
   * @throws PubSubException upon failure
   */
  public Future<Policy> getPolicyAsync() {
    return pubsub.getTopicPolicyAsync(this.name());
  }

  /**
   * Sets the IAM access control policy for this topic. Replaces any existing policy. This method
   * returns the new policy.
   *
   * <p>It is recommended that you use the read-modify-write pattern. This pattern entails reading
   * the project's current policy, updating it locally, and then sending the modified policy for
   * writing. Cloud IAM solves the problem of conflicting processes simultaneously attempting to
   * modify a policy by using the {@link Policy#etag etag} property. This property is used to
   * verify whether the policy has changed since the last request. When you make a request with an
   * etag value, the value in the request is compared with the existing etag value associated with
   * the policy. The policy is written only if the etag values match. If the etags don't match, a
   * {@code PubSubException} is thrown, denoting that the server aborted update. If an etag is not
   * provided, the policy is overwritten blindly.
   *
   * @throws PubSubException upon failure
   */
  public Policy replacePolicy(Policy newPolicy) {
    return pubsub.replaceTopicPolicy(this.name(), newPolicy);
  }

  /**
   * Sends a request to set the IAM access control policy for this topic. Replaces any existing
   * policy. This method returns a {@code Future} object to consume the result. {@link Future#get()}
   * returns the new policy.
   *
   * <p>It is recommended that you use the read-modify-write pattern. This pattern entails reading
   * the project's current policy, updating it locally, and then sending the modified policy for
   * writing. Cloud IAM solves the problem of conflicting processes simultaneously attempting to
   * modify a policy by using the {@link Policy#etag etag} property. This property is used to
   * verify whether the policy has changed since the last request. When you make a request with an
   * etag value, the value in the request is compared with the existing etag value associated with
   * the policy. The policy is written only if the etag values match. If the etags don't match,
   * {@link Future#get()} will throw a {@link java.util.concurrent.ExecutionException} caused by a
   * {@code PubSubException}, denoting that the server aborted update. If an etag is not provided,
   * the policy is overwritten blindly.
   *
   * @throws PubSubException upon failure
   */
  public Future<Policy> replacePolicyAsync(Policy newPolicy) {
    return pubsub.replaceTopicPolicyAsync(this.name(), newPolicy);
  }

  /**
   * Returns the permissions that a caller has on this topic.
   *
   * <p>You typically don't call this method if you're using Google Cloud Platform directly to
   * manage permissions. This method is intended for integration with your proprietary software,
   * such as a customized graphical user interface. For example, the Cloud Platform Console tests
   * IAM permissions internally to determine which UI should be available to the logged-in user.
   *
   * @return A list of booleans representing whether the caller has the permissions specified (in
   *     the order of the given permissions)
   * @throws PubSubException upon failure
   * @see <a href="https://cloud.google.com/pubsub/docs/access_control#permissions">
   *     Permissions and Roles</a>
   */
  public List<Boolean> testPermissions(List<String> permissions) {
    return pubsub.testTopicPermissions(this.name(), permissions);
  }

  /**
   * Sends a request to get the permissions that a caller has on this topic.
   *
   * <p>You typically don't call this method if you're using Google Cloud Platform directly to
   * manage permissions. This method is intended for integration with your proprietary software,
   * such as a customized graphical user interface. For example, the Cloud Platform Console tests
   * IAM permissions internally to determine which UI should be available to the logged-in user.
   *
   * @return A {@code Future} object to consume the result. {@link Future#get()} returns a list of
   *     booleans representing whether the caller has the permissions specified (in the order of the
   *     given permissions)
   * @throws PubSubException upon failure
   * @see <a href="https://cloud.google.com/pubsub/docs/access_control#permissions">
   *     Permissions and Roles</a>
   */
  public Future<List<Boolean>> testPermissionsAsync(List<String> permissions) {
    return pubsub.testTopicPermissionsAsync(this.name(), permissions);
  }

  private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
    input.defaultReadObject();
    this.pubsub = options.service();
  }

  static Topic fromPb(PubSub pubsub, com.google.pubsub.v1.Topic topicPb) {
    TopicInfo topicInfo = TopicInfo.fromPb(topicPb);
    return new Topic(pubsub, new BuilderImpl(topicInfo));
  }

  static Function<com.google.pubsub.v1.Topic, Topic> fromPbFunction(final PubSub pubsub) {
    return new Function<com.google.pubsub.v1.Topic, Topic>() {
      @Override
      public Topic apply(com.google.pubsub.v1.Topic topicPb) {
        return topicPb != null ? fromPb(pubsub, topicPb) : null;
      }
    };
  }
}
