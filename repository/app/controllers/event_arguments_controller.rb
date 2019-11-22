class EventArgumentsController < ApplicationController

    def show
        @event_argument = EventArgument.find(params[:id])
        render :json =>  @event_argument
    end

end
