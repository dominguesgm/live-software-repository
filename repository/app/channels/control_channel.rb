class ControlChannel < ApplicationCable::Channel

    def subscribed
        stream_from "control_channel"
    end

end