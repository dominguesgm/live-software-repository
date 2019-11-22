class EventsController < ApplicationController

    def create
        
        params.delete("controller")
        params.delete("action")

        events = []

        if !params[:events].empty?
            ActiveRecord::Base.logger.silence do
                
                

                for event in params[:events]

                    @event = Event.new({
                        :origin_class => event[:originClass],
                        :destination_class => event[:destinationClass],
                        :origin_hash => event[:originHash],
                        :destination_hash => event[:destinationHash],
                        :timestamp => DateTime.parse(event[:timestamp]),
                        :kind => event[:kind],
                        :signature => event[:signature],
                        :source_location => event[:sourceLocation],
                        :class_instance => event[:class],
                        :project_name => event[:projectName],
                        :project_id => event[:projectId]

                    })

                    if !(event[:arguments].empty?)
                        arguments = []
                        for arg in event[:arguments]
                            @event.event_arguments.build( {
                                :argument_type => arg[:type],
                                :value => arg[:value]
                            })
                        end
                    end

                    ActionCable.server.broadcast("control_channel", @event.to_json(:include => :event_arguments))

                    
                    events << @event
                end

                @result = Event.import events, recursive: true

                if @result[:failed_instances].empty?
                    render :json =>  {:ids => @result[:ids]}.to_json(), :status => :created
                else
                    render :json =>  {:ids => @result[:ids], :failed_instances => @result[:failed_instances]}.to_json(), :status => :unprocessable_entity
                end

            end
        else
            render :json =>  {}, :status => :created
        end

    end

    def show
        @event = Event.find(params[:id])
        render :json =>  @event.to_json(:include => :event_arguments)
    end


    def index
        if params.key?("from")
            # From but not including
            @dateFrom = DateTime.parse(params[:from])

            @events = Event.where("timestamp > ?", @dateFrom)

            render :json =>  @events.to_json(:include => :event_arguments)
        else
            @events = Event.all

            render :json =>  @events.to_json(:include => :event_arguments)
        end
    end


    def clear

        EventArgument.delete_all

        Event.delete_all

        render :json => {}, :code => :no_content

    end

end
